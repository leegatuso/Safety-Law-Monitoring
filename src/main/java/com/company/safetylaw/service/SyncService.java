package com.company.safetylaw.service;

import static com.company.safetylaw.support.Checks.*;
import static com.company.safetylaw.support.Rows.row;

import com.company.safetylaw.client.LawClient;
import com.company.safetylaw.domain.*;
import com.company.safetylaw.dto.LawSnapshot;
import com.company.safetylaw.mapper.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class SyncService {
    private static final Logger log = LoggerFactory.getLogger(SyncService.class);
    private final LawMapper laws;
    private final RevisionMapper revisions;
    private final CandidateMapper candidates;
    private final LawClient client;
    private final ContentService contents;
    private final SettingsService settings;
    private final TransactionTemplate tx;
    private final AtomicBoolean running = new AtomicBoolean();

    public SyncService(
            LawMapper laws,
            RevisionMapper revisions,
            CandidateMapper candidates,
            LawClient client,
            ContentService contents,
            SettingsService settings,
            TransactionTemplate tx) {
        this.laws = laws;
        this.revisions = revisions;
        this.candidates = candidates;
        this.client = client;
        this.contents = contents;
        this.settings = settings;
        this.tx = tx;
    }

    public synchronized boolean syncOne(long id) {
        var law = found(laws.selectById(id));
        var latest = LawClient.match(law, client.search(law.getSourceType(), law.getName(), 1));
        if (latest == null) throw new IllegalArgumentException("추적 중인 법령을 찾지 못했습니다.");
        return Boolean.TRUE.equals(tx.execute(status -> applyLatest(id, latest)));
    }

    private boolean applyLatest(long id, LawSnapshot latest) {
        var law = found(laws.selectById(id));
        boolean first = text(law.getLastSyncedAt()).isBlank();
        boolean changed =
                first
                        ? (!text(latest.promulgationDate()).isBlank()
                                || !text(latest.enforcementDate()).isBlank())
                        : !Objects.equals(
                                        text(law.getCurrentPromulgationNo()),
                                        text(latest.promulgationNo()))
                                || !Objects.equals(
                                        text(law.getCurrentPromulgationDate()),
                                        text(latest.promulgationDate()))
                                || !Objects.equals(
                                        text(law.getCurrentEnforcementDate()),
                                        text(latest.enforcementDate()));
        if (changed) {
            var r = new LawRevision();
            r.setTrackedLawId(id);
            r.setPromulgationNo(latest.promulgationNo());
            r.setPromulgationDate(latest.promulgationDate());
            r.setEnforcementDate(latest.enforcementDate());
            if (!first) {
                r.setPreviousPromulgationNo(law.getCurrentPromulgationNo());
                r.setPreviousPromulgationDate(law.getCurrentPromulgationDate());
                r.setPreviousEnforcementDate(law.getCurrentEnforcementDate());
            }
            r.setReviewStatus("미검토");
            r.setDetectedAt(now());
            revisions.insert(r);
        }
        law.setCurrentPromulgationNo(latest.promulgationNo());
        law.setCurrentPromulgationDate(latest.promulgationDate());
        law.setCurrentEnforcementDate(latest.enforcementDate());
        law.setExternalId(latest.externalId());
        law.setMasterId(latest.masterId());
        law.setDetailLink(latest.detailLink());
        law.setLastSyncedAt(now());
        law.setUpdatedAt(now());
        laws.update(law);
        return changed;
    }

    public Map<String, Object> run() {
        if (!running.compareAndSet(false, true))
            throw new IllegalArgumentException("동기화가 진행 중입니다.");
        int checked = 0, changed = 0, added = 0;
        var errors = new ArrayList<String>();
        try {
            for (var law : laws.selectList(true, null)) {
                checked++;
                try {
                    if (syncOne(law.getId())) changed++;
                    contents.cache(LawSnapshot.from(found(laws.selectById(law.getId()))));
                } catch (Exception ex) {
                    log.warn(
                            "Law sync failed for id={}: {}",
                            law.getId(),
                            ex.getClass().getSimpleName());
                    errors.add(law.getName() + ": 조회 또는 저장 실패");
                }
            }
            try {
                added = scan();
            } catch (Exception ex) {
                log.warn("Candidate scan failed: {}", ex.getClass().getSimpleName());
                errors.add("신규 고시 탐색 실패");
            }
            return row(
                    "checked",
                    checked,
                    "new_revisions",
                    changed,
                    "new_admrul_candidates",
                    added,
                    "errors",
                    errors);
        } finally {
            running.set(false);
        }
    }

    public int scan() {
        var words =
                Arrays.stream(settings.get("new_admrul_keywords").split(","))
                        .map(String::strip)
                        .filter(s -> !s.isBlank())
                        .toList();
        String department = settings.get("new_admrul_department"),
                since = settings.get("new_admrul_since_date");
        var tracked = laws.selectList(true, "admrul");
        int added = 0;
        for (var c : candidates.selectStatus("신규")) {
            boolean stale =
                    (!department.isBlank()
                                    && !text(c.getDepartment()).isBlank()
                                    && !c.getDepartment().contains(department))
                            || (!words.isEmpty() && words.stream().noneMatch(c.getName()::contains))
                            || (!since.isBlank()
                                    && !text(c.getPromulgationDate()).isBlank()
                                    && c.getPromulgationDate().compareTo(since) < 0);
            if (stale
                    || tracked.stream()
                            .anyMatch(l -> Objects.equals(l.getExternalId(), c.getExternalId())))
                candidates.deleteById(c.getId());
        }
        for (String word : words)
            for (int page = 1; page <= 2000; page++) {
                var rows = client.search("admrul", word, page);
                for (var s : rows) {
                    if (!department.isBlank()
                            && !text(s.department()).isBlank()
                            && !s.department().contains(department)) continue;
                    if (!since.isBlank()
                            && !text(s.promulgationDate()).isBlank()
                            && s.promulgationDate().compareTo(since) < 0) continue;
                    if (tracked.stream()
                                    .anyMatch(
                                            l -> Objects.equals(l.getExternalId(), s.externalId()))
                            || candidates.selectIdentity(s.sourceType(), s.externalId()) != null)
                        continue;
                    var c = s.toCandidate();
                    c.setMatchedKeyword(word);
                    c.setStatus("신규");
                    c.setFirstSeenAt(now());
                    candidates.insert(c);
                    added++;
                }
                if (rows.size() < 100) break;
                if (page == 2000) throw new IllegalArgumentException("신규 고시 조회 한도 초과");
            }
        return added;
    }
}
