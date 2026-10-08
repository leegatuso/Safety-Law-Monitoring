package com.company.safetylaw.service;

import static com.company.safetylaw.support.Rows.*;

import com.company.safetylaw.client.RemoteClient;
import com.company.safetylaw.domain.AppSetting;
import com.company.safetylaw.mapper.NewsMapper;
import com.company.safetylaw.mapper.SettingsMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class SettingsService {
    private final SettingsMapper mapper;
    private final NewsMapper news;
    private final Map<String, Object> defaults = new LinkedHashMap<>();
    private final boolean localDemo;
    private final long syncDelayMs;

    SettingsService(
            SettingsMapper mapper,
            NewsMapper news,
            Environment env,
            @Value("${app.demo}") boolean localDemo,
            @Value("${app.sync-delay-ms}") long syncDelayMs) {
        this.mapper = mapper;
        this.news = news;
        this.localDemo = localDemo;
        this.syncDelayMs = syncDelayMs;
        defaults.putAll(
                row(
                        "law_api_oc",
                        "",
                        "kosha_guide_api_key",
                        "",
                        "kosha_guide_api_url",
                        "https://apis.data.go.kr/B552468/koshaguide/getKoshaGuide",
                        "new_admrul_keywords",
                        "안전보건,산업안전,중대재해,위험성평가,유해위험,보건관리,안전관리",
                        "new_admrul_department",
                        "고용노동부",
                        "new_admrul_since_date",
                        "",
                        "full_law_cache_enabled",
                        "false",
                        "news_ticker_enabled",
                        "true",
                        "news_retention_days",
                        "180"));
        for (String category : List.of("moel", "kosha", "accident")) {
            String query =
                    switch (category) {
                        case "moel" -> "고용노동부 안전보건";
                        case "kosha" -> "안전보건공단";
                        default -> "중대재해";
                    };
            defaults.put(
                    "news_source_" + category + "_url",
                    "https://news.google.com/rss/search?q="
                            + URLEncoder.encode(query, StandardCharsets.UTF_8)
                            + "&hl=ko&gl=KR&ceid=KR:ko");
        }
        defaults.replaceAll(
                (key, value) -> env.getProperty(key.toUpperCase(Locale.ROOT), value.toString()));
    }

    Map<String, Object> values() {
        var result = new LinkedHashMap<>(defaults);
        mapper.selectAll()
                .forEach(
                        r ->
                                result.put(
                                        r.getSettingKey(),
                                        Objects.toString(r.getSettingValue(), "")));
        return result;
    }

    public String get(String key) {
        return str(values(), key);
    }

    public boolean demo() {
        return localDemo && get("law_api_oc").isBlank();
    }

    public boolean localPreview() {
        return localDemo;
    }

    public Map<String, Object> output() {
        var result = values();
        result.put("demo_mode", demo());
        result.put("local_preview", localDemo);
        result.put("law_api_oc_set", !get("law_api_oc").isBlank());
        result.put("kosha_guide_api_key_set", !get("kosha_guide_api_key").isBlank());
        for (String key : List.of("full_law_cache_enabled", "news_ticker_enabled"))
            result.put(key, Boolean.parseBoolean(str(result, key)));
        result.put("news_retention_days", Integer.parseInt(get("news_retention_days")));
        result.put("auto_sync_interval_hours", syncDelayMs / 3600000.0);
        for (String category : List.of("moel", "kosha", "accident")) {
            var latest = news.selectCategory(category, 1).stream().findFirst();
            result.put(
                    "news_" + category + "_showing_demo",
                    latest.map(n -> n.getIsDemo()).orElse(null));
        }
        return result;
    }

    @Transactional
    public void save(Map<String, Object> values) {
        values.forEach(
                (key, value) -> {
                    if (!defaults.containsKey(key) || value == null) return;
                    if (key.equals("news_retention_days")) {
                        int days;
                        try {
                            days = Integer.parseInt(value.toString());
                        } catch (NumberFormatException ex) {
                            throw new IllegalArgumentException("보관 기간은 숫자로 입력하세요.");
                        }
                        if (days < 1 || days > 3650)
                            throw new IllegalArgumentException("보관 기간은 1~3650일입니다.");
                    }
                    if (key.endsWith("_url") && !value.toString().isBlank())
                        RemoteClient.validateUrl(value.toString());
                    put(key, value.toString());
                });
    }

    public void put(String key, String value) {
        var setting = new AppSetting();
        setting.setSettingKey(key);
        setting.setSettingValue(value);
        if (mapper.selectById(key) == null) mapper.insert(setting);
        else mapper.update(setting);
    }
}
