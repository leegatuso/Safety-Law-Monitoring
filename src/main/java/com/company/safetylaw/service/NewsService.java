package com.company.safetylaw.service;

import static com.company.safetylaw.client.RemoteClient.*;
import static com.company.safetylaw.support.Checks.*;
import static com.company.safetylaw.support.Rows.row;

import com.company.safetylaw.client.RemoteClient;
import com.company.safetylaw.domain.NewsItem;
import com.company.safetylaw.mapper.NewsMapper;
import com.company.safetylaw.support.NewsDedupe;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class NewsService {
    private final NewsMapper news;
    private final SettingsService settings;
    private final RemoteClient remote;
    private boolean fullDedupeDone;

    public NewsService(NewsMapper news, SettingsService settings, RemoteClient remote) {
        this.news = news;
        this.settings = settings;
        this.remote = remote;
    }

    private List<NewsItem> searchRows(
            String category,
            String query,
            String from,
            String to,
            Boolean archived,
            boolean issues,
            int limit) {
        if (from != null && !from.isBlank()) LocalDate.parse(from);
        if (to != null && !to.isBlank()) LocalDate.parse(to);
        if (!text(from).isBlank() && !text(to).isBlank() && from.compareTo(to) > 0)
            throw new IllegalArgumentException("시작일은 종료일 이전이어야 합니다.");
        var rows = news.selectSearch(category, query, from, to, archived, issues, limit);
        for (var n : rows) if ("issue".equals(n.getCategory())) n.setSourceName("안전보건 이슈");
        return rows;
    }

    public List<NewsItem> list(String category, int limit) {
        if (!Boolean.parseBoolean(settings.get("news_ticker_enabled"))) return List.of();
        return searchRows(
                category, null, null, null, null, false, Math.max(1, Math.min(limit, 200)));
    }

    public Map<String, Object> search(
            String category, String query, String from, String to, Boolean archived, int limit) {
        var rows =
                searchRows(
                        category,
                        query,
                        from,
                        to,
                        archived,
                        true,
                        Math.max(1, Math.min(limit, 300)));
        return row(
                "items",
                rows,
                "total",
                news.countSearch(category, query, from, to, archived, true));
    }

    @Transactional
    public NewsItem archive(long id, boolean archived) {
        var n = found(news.selectById(id));
        n.setIsArchived(archived);
        news.update(n);
        return n;
    }

    public synchronized Map<String, Object> sync() {
        if (settings.localPreview())
            return row("added", 0, "errors", List.of("로컬 검증 모드에서는 자동 뉴스 수집을 실행하지 않습니다."));
        var previous = new HashSet<Long>();
        for (var n : news.selectAll()) previous.add(n.getId());
        var errors = new ArrayList<String>();
        for (String category : List.of("moel", "kosha", "accident")) {
            String url = settings.get("news_source_" + category + "_url");
            if (url.isBlank()) continue;
            try {
                var root = xml(remote.bytes(url)).getDocumentElement();
                var items = elements(root, "item");
                if (items.isEmpty()) items = elements(root, "entry");
                for (var item : items) {
                    String title = text(item, "title"), link = text(item, "link");
                    if (link.isBlank() && !elements(item, "link").isEmpty())
                        link = elements(item, "link").get(0).getAttribute("href");
                    if (title.isBlank() || !link.matches("https?://[^\\s]+")) continue;
                    String guid = text(item, "guid", "id");
                    if (guid.isBlank()) guid = link;
                    guid =
                            HexFormat.of()
                                    .formatHex(
                                            java.security.MessageDigest.getInstance("SHA-256")
                                                    .digest(
                                                            guid.getBytes(
                                                                    java.nio.charset
                                                                            .StandardCharsets
                                                                            .UTF_8)));
                    if (news.selectGuid(category, guid) != null) continue;
                    var n = new NewsItem();
                    n.setCategory(category);
                    n.setSourceName(
                            switch (category) {
                                case "moel" -> "고용노동부";
                                case "kosha" -> "안전보건공단";
                                default -> "중대재해 뉴스";
                            });
                    n.setTitle(title.substring(0, Math.min(512, title.length())));
                    n.setLink(link);
                    n.setGuid(guid);
                    String raw = text(item, "pubDate", "published", "updated");
                    try {
                        n.setPublishedAt(
                                ZonedDateTime.parse(raw, DateTimeFormatter.RFC_1123_DATE_TIME)
                                        .toInstant()
                                        .toString());
                    } catch (Exception ex) {
                        try {
                            n.setPublishedAt(Instant.parse(raw).toString());
                        } catch (Exception ignored) {
                        }
                    }
                    n.setFetchedAt(now());
                    n.setIsDemo(false);
                    n.setIsArchived(false);
                    news.insert(n);
                }
            } catch (Exception ex) {
                LoggerFactory.getLogger(NewsService.class)
                        .warn(
                                "News collection failed for {}: {}",
                                category,
                                ex.getClass().getSimpleName());
                errors.add(category + ": 뉴스 수집 실패");
            }
        }
        for (var group :
                NewsDedupe.groups(
                        news.selectDedupeWindow(
                                fullDedupeDone
                                        ? Instant.now().minus(Duration.ofDays(14)).toString()
                                        : null))) {
            var archived =
                    group.stream()
                            .filter(n -> Boolean.TRUE.equals(n.getIsArchived()))
                            .map(NewsItem::getId)
                            .toList();
            var keep =
                    archived.isEmpty()
                            ? List.of(
                                    group.stream()
                                            .min(
                                                    Comparator.comparing(
                                                                    (NewsItem n) ->
                                                                            !previous.contains(
                                                                                    n.getId()))
                                                            .thenComparing(NewsDedupe::time))
                                            .orElseThrow()
                                            .getId())
                            : archived;
            for (var n : group) if (!keep.contains(n.getId())) news.deleteById(n.getId());
        }
        fullDedupeDone = true;
        news.deleteExpired(
                Instant.now()
                        .minus(
                                Duration.ofDays(
                                        Integer.parseInt(settings.get("news_retention_days"))))
                        .toString());
        for (String category : List.of("moel", "kosha", "accident"))
            for (var n :
                    news.selectCategory(category, Integer.MAX_VALUE).stream()
                            .filter(n -> !Boolean.TRUE.equals(n.getIsArchived()))
                            .skip(5000)
                            .toList()) news.deleteById(n.getId());
        return row(
                "added",
                news.selectAll().stream().filter(n -> !previous.contains(n.getId())).count(),
                "errors",
                errors);
    }
}
