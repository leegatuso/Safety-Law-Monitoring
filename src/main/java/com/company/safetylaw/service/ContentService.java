package com.company.safetylaw.service;

import static com.company.safetylaw.support.Checks.*;
import static com.company.safetylaw.support.Rows.row;

import com.company.safetylaw.client.LawClient;
import com.company.safetylaw.dto.*;
import com.company.safetylaw.mapper.*;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class ContentService {
    private final ContentMapper contents;
    private final LawMapper laws;
    private final LawClient client;
    private final SettingsService settings;
    private final ThreadPoolTaskExecutor worker;
    private final TransactionTemplate tx;
    private final AtomicBoolean running = new AtomicBoolean();
    private volatile Map<String, Object> progress =
            row(
                    "running",
                    false,
                    "processed",
                    0,
                    "skipped",
                    0,
                    "total",
                    null,
                    "started_at",
                    null,
                    "finished_at",
                    null,
                    "error",
                    null);

    public ContentService(
            ContentMapper contents,
            LawMapper laws,
            LawClient client,
            SettingsService settings,
            ThreadPoolTaskExecutor contentExecutor,
            TransactionTemplate tx) {
        this.contents = contents;
        this.laws = laws;
        this.client = client;
        this.settings = settings;
        this.worker = contentExecutor;
        this.tx = tx;
    }

    public Map<String, Object> status() {
        return row("cached_count", contents.count(), "last_cached_at", contents.lastCachedAt());
    }

    public List<ContentSearchResult> search(String source, String query) {
        if (query.isBlank()) throw new IllegalArgumentException("검색어를 입력하세요.");
        if (!List.of("all", "law", "admrul").contains(source))
            throw new IllegalArgumentException("법령 구분을 확인하세요.");
        return contents.search(source, query, 200).stream()
                .map(c -> ContentSearchResult.from(c, query))
                .toList();
    }

    public synchronized void cache(LawSnapshot law) {
        var body = client.content(law);
        tx.executeWithoutResult(
                s -> {
                    var old = contents.selectIdentity(law.sourceType(), law.externalId());
                    var c = law.toContent();
                    c.setContent(body.text());
                    c.setArticleContentLen(body.articleLength());
                    c.setCachedAt(now());
                    if (old == null) contents.insert(c);
                    else {
                        c.setId(old.getId());
                        contents.update(c);
                    }
                });
    }

    public Map<String, Object> refresh() {
        int count = 0;
        for (var law : laws.selectList(true, null)) {
            cache(LawSnapshot.from(law));
            count++;
        }
        String department = settings.get("new_admrul_department");
        for (String word : settings.get("new_admrul_keywords").split(",")) {
            if (word.isBlank()) continue;
            for (String source : List.of("law", "admrul"))
                for (int page = 1; page <= 2000; page++) {
                    var rows = client.search(source, word.strip(), page);
                    for (var law : rows)
                        if (department.isBlank()
                                || text(law.department()).isBlank()
                                || law.department().contains(department)) {
                            cache(law);
                            count++;
                        }
                    if (rows.size() < 100) break;
                    if (page == 2000) throw new IllegalArgumentException("본문 조회 한도 초과");
                }
        }
        var result = status();
        result.put("refreshed", count);
        return result;
    }

    public Map<String, Object> progress() {
        return progress;
    }

    public Map<String, Object> start() {
        if (!running.compareAndSet(false, true))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "전체 캐시 갱신이 진행 중입니다.");
        progress =
                row(
                        "running",
                        true,
                        "processed",
                        0,
                        "skipped",
                        0,
                        "total",
                        null,
                        "started_at",
                        now(),
                        "finished_at",
                        null,
                        "error",
                        null);
        try {
            worker.execute(this::refreshAll);
        } catch (RuntimeException ex) {
            running.set(false);
            progress =
                    row(
                            "running",
                            false,
                            "processed",
                            0,
                            "skipped",
                            0,
                            "total",
                            0,
                            "started_at",
                            progress.get("started_at"),
                            "finished_at",
                            now(),
                            "error",
                            "작업을 시작하지 못했습니다.");
            throw ex;
        }
        return Map.of("started", true);
    }

    private void refreshAll() {
        int processed = 0, skipped = 0;
        String error = null;
        try {
            for (String source : List.of("law", "admrul"))
                for (int page = 1; page <= 2000; page++) {
                    var rows = client.search(source, "", page);
                    for (var law : rows) {
                        if (Thread.currentThread().isInterrupted())
                            throw new InterruptedException();
                        var old = contents.selectIdentity(source, law.externalId());
                        if (old != null
                                && !text(old.getContent()).isBlank()
                                && Objects.equals(
                                        text(old.getPromulgationNo()), text(law.promulgationNo()))
                                && Objects.equals(
                                        text(old.getPromulgationDate()),
                                        text(law.promulgationDate()))
                                && Objects.equals(
                                        text(old.getEnforcementDate()),
                                        text(law.enforcementDate()))) skipped++;
                        else {
                            cache(law);
                            processed++;
                        }
                        progress =
                                row(
                                        "running",
                                        true,
                                        "processed",
                                        processed,
                                        "skipped",
                                        skipped,
                                        "total",
                                        null,
                                        "started_at",
                                        progress.get("started_at"),
                                        "finished_at",
                                        null,
                                        "error",
                                        null);
                        Thread.sleep(200);
                    }
                    if (rows.size() < 100) break;
                    if (page == 2000) throw new IllegalArgumentException("조회 한도 초과");
                }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            error = "작업이 중단되었습니다.";
        } catch (Exception ex) {
            LoggerFactory.getLogger(ContentService.class)
                    .warn("Content refresh failed: {}", ex.getClass().getSimpleName());
            error = "캐시 갱신 실패. 인증키와 네트워크를 확인하세요.";
        } finally {
            progress =
                    row(
                            "running",
                            false,
                            "processed",
                            processed,
                            "skipped",
                            skipped,
                            "total",
                            processed + skipped,
                            "started_at",
                            progress.get("started_at"),
                            "finished_at",
                            now(),
                            "error",
                            error);
            running.set(false);
        }
    }
}
