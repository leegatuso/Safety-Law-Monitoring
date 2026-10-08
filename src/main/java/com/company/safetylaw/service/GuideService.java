package com.company.safetylaw.service;

import static com.company.safetylaw.support.Checks.*;
import static com.company.safetylaw.support.Rows.row;

import com.company.safetylaw.client.*;
import com.company.safetylaw.domain.KoshaGuide;
import com.company.safetylaw.dto.GuideSaveRequest;
import com.company.safetylaw.mapper.GuideMapper;
import com.company.safetylaw.support.ContentText;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.util.*;

@Service
public class GuideService {
    private final GuideMapper guides;
    private final GuideFileService files;
    private final KoshaClient client;
    private final RemoteClient remote;
    private final TransactionTemplate tx;

    public GuideService(
            GuideMapper guides,
            GuideFileService files,
            KoshaClient client,
            RemoteClient remote,
            TransactionTemplate tx) {
        this.guides = guides;
        this.files = files;
        this.client = client;
        this.remote = remote;
        this.tx = tx;
    }

    public List<KoshaGuide> list(String field) {
        return guides.selectList(field);
    }

    public KoshaGuide get(long id) {
        return found(guides.selectById(id));
    }

    public List<Map<String, Object>> search(String query) {
        if (query.isBlank()) throw new IllegalArgumentException("검색어를 입력하세요.");
        return guides.search(query).stream()
                .map(
                        g ->
                                row(
                                        "id",
                                        g.getId(),
                                        "code",
                                        g.getCode(),
                                        "field",
                                        g.getField(),
                                        "title",
                                        g.getTitle(),
                                        "issued_date",
                                        g.getIssuedDate(),
                                        "file_link",
                                        g.getFileLink(),
                                        "content",
                                        g.getContent(),
                                        "matched_in",
                                        text(g.getContent()).contains(query) ? "content" : "title",
                                        "snippet",
                                        ContentText.snippet(text(g.getContent()), query)))
                .toList();
    }

    @Transactional
    public KoshaGuide save(Long id, GuideSaveRequest request) {
        var old = id == null ? null : get(id);
        var g = request.toEntity();
        String local = "/api/kosha-guides/" + id + "/file";
        if (id != null
                && files.exists(id)
                && (text(g.getFileLink()).isBlank() || local.equals(g.getFileLink())))
            g.setFileLink(local);
        else link(g.getFileLink());
        if (old != null && request.content() == null) g.setContent(old.getContent());
        boolean changed =
                old != null && !Objects.equals(text(old.getFileLink()), text(g.getFileLink()));
        g.setContentStale(changed || text(g.getContent()).isBlank());
        g.setUpdatedAt(now());
        if (old == null) {
            g.setCreatedAt(g.getUpdatedAt());
            guides.insert(g);
        } else {
            g.setId(id);
            g.setCreatedAt(old.getCreatedAt());
            guides.update(g);
        }
        return g;
    }

    @Transactional
    public Map<String, Object> bulk(List<GuideSaveRequest> items) {
        int added = 0, updated = 0;
        for (var item : items) {
            var old = text(item.code()).isBlank() ? null : guides.selectCode(item.code());
            save(old == null ? null : old.getId(), item);
            if (old == null) added++;
            else updated++;
        }
        return row("added", added, "updated", updated, "skipped", 0);
    }

    public synchronized void delete(long id) throws IOException {
        get(id);
        var previous = files.read(id);
        try {
            files.write(id, null);
            tx.executeWithoutResult(s -> guides.deleteById(id));
        } catch (IOException | RuntimeException ex) {
            files.write(id, previous);
            throw ex;
        }
    }

    public synchronized void deleteBulk(List<Long> ids) throws IOException {
        var backup = new LinkedHashMap<Long, byte[]>();
        for (long id : ids) {
            get(id);
            backup.put(id, files.read(id));
        }
        try {
            for (long id : ids) files.write(id, null);
            tx.executeWithoutResult(
                    s -> {
                        for (long id : ids) guides.deleteById(id);
                    });
        } catch (IOException | RuntimeException ex) {
            for (var entry : backup.entrySet()) files.write(entry.getKey(), entry.getValue());
            throw ex;
        }
    }

    public synchronized KoshaGuide upload(long id, byte[] bytes) throws IOException {
        var g = get(id);
        String content = files.extract(bytes);
        byte[] previous = files.read(id);
        try {
            files.write(id, bytes);
            g.setFileLink("/api/kosha-guides/" + id + "/file");
            g.setContent(content);
            g.setContentStale(false);
            g.setUpdatedAt(now());
            tx.executeWithoutResult(s -> guides.update(g));
            return g;
        } catch (IOException | RuntimeException ex) {
            files.write(id, previous);
            throw ex;
        }
    }

    public synchronized KoshaGuide deleteFile(long id) throws IOException {
        var g = get(id);
        byte[] previous = files.read(id);
        try {
            files.write(id, null);
            g.setFileLink(null);
            g.setContent(null);
            g.setContentStale(true);
            g.setUpdatedAt(now());
            tx.executeWithoutResult(s -> guides.update(g));
            return g;
        } catch (IOException | RuntimeException ex) {
            files.write(id, previous);
            throw ex;
        }
    }

    public Map<String, Object> cache(int limit) {
        int processed = 0, succeeded = 0;
        for (var g : guides.selectPending(Math.max(1, Math.min(limit, 100)))) {
            processed++;
            try {
                byte[] bytes =
                        files.exists(g.getId())
                                ? files.read(g.getId())
                                : remote.bytes(g.getFileLink());
                g.setContent(files.extract(bytes));
                g.setContentStale(false);
                g.setUpdatedAt(now());
                guides.update(g);
                succeeded++;
            } catch (Exception ex) {
                LoggerFactory.getLogger(GuideService.class)
                        .warn(
                                "Guide PDF cache failed for id={}: {}",
                                g.getId(),
                                ex.getClass().getSimpleName());
            }
        }
        return row(
                "processed",
                processed,
                "succeeded",
                succeeded,
                "failed",
                processed - succeeded,
                "remaining",
                guides.countPending());
    }

    public synchronized Map<String, Object> sync() {
        int found = 0, added = 0, updated = 0;
        var errors = new ArrayList<String>();
        try {
            for (int page = 1; page <= 100; page++) {
                var response = client.page(page);
                for (var incoming : response.items()) {
                    found++;
                    link(incoming.getFileLink());
                    var old =
                            text(incoming.getCode()).isBlank()
                                    ? null
                                    : guides.selectCode(incoming.getCode());
                    if (old == null) {
                        incoming.setContentStale(true);
                        incoming.setCreatedAt(now());
                        incoming.setUpdatedAt(now());
                        guides.insert(incoming);
                        added++;
                    } else {
                        old.setTitle(incoming.getTitle());
                        old.setIssuedDate(incoming.getIssuedDate());
                        if (!text(incoming.getFileLink()).isBlank() && !files.exists(old.getId())) {
                            if (!Objects.equals(old.getFileLink(), incoming.getFileLink()))
                                old.setContentStale(true);
                            old.setFileLink(incoming.getFileLink());
                        }
                        old.setUpdatedAt(now());
                        guides.update(old);
                        updated++;
                    }
                }
                if (response.items().size() < 100
                        || response.total() >= 0 && page * 100 >= response.total()) break;
                if (page == 100) errors.add("조회 페이지 한도에 도달했습니다.");
            }
        } catch (Exception ex) {
            LoggerFactory.getLogger(GuideService.class)
                    .warn("KOSHA synchronization failed: {}", ex.getClass().getSimpleName());
            errors.add("KOSHA 수집 실패. 인증키와 서비스 주소를 확인하세요.");
        }
        return row("found", found, "added", added, "updated", updated, "errors", errors);
    }
}
