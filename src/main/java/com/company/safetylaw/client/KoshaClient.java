package com.company.safetylaw.client;

import static com.company.safetylaw.support.Rows.row;

import com.company.safetylaw.domain.KoshaGuide;
import com.company.safetylaw.service.SettingsService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class KoshaClient {
    private final RemoteClient remote;
    private final SettingsService settings;
    private final ObjectMapper json;

    public KoshaClient(RemoteClient remote, SettingsService settings, ObjectMapper json) {
        this.remote = remote;
        this.settings = settings;
        this.json = json;
    }

    public record Page(List<KoshaGuide> items, int total) {}

    public Page page(int page) {
        String key = settings.get("kosha_guide_api_key");
        if (key.isBlank()) throw new IllegalArgumentException("KOSHA 인증키를 설정하세요.");
        if (key.contains("%")) key = URLDecoder.decode(key, StandardCharsets.UTF_8);
        try {
            var data =
                    json.readTree(
                            remote.bytes(
                                    RemoteClient.query(
                                            settings.get("kosha_guide_api_url"),
                                            row(
                                                    "serviceKey",
                                                    key,
                                                    "callApiId",
                                                    "1050",
                                                    "pageNo",
                                                    page,
                                                    "numOfRows",
                                                    100))));
            if (!data.path("header").path("resultCode").asText("00").equals("00"))
                throw new IllegalArgumentException("KOSHA API 인증 또는 요청 오류입니다.");
            var body = data.path("body");
            var items = body.path("items").path("item");
            if (items.isObject()) items = json.createArrayNode().add(items);
            var result = new ArrayList<KoshaGuide>();
            if (items.isArray())
                for (var item : items) {
                    String title = item.path("techGdlnNm").asText();
                    if (title.isBlank()) continue;
                    var g = new KoshaGuide();
                    g.setTitle(title);
                    g.setCode(item.path("techGdlnNo").asText());
                    g.setIssuedDate(item.path("techGdlnOfancYmd").asText().replace("-", ""));
                    g.setFileLink(item.path("fileDownloadUrl").asText());
                    result.add(g);
                }
            return new Page(result, body.path("totalCount").asInt(-1));
        } catch (java.io.IOException ex) {
            throw new IllegalArgumentException("KOSHA 응답을 해석하지 못했습니다.");
        }
    }
}
