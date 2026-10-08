package com.company.safetylaw.client;

import static com.company.safetylaw.client.RemoteClient.*;
import static com.company.safetylaw.support.Rows.*;

import com.company.safetylaw.domain.Law;
import com.company.safetylaw.dto.LawSnapshot;
import com.company.safetylaw.service.SettingsService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class LawClient {
    private final RemoteClient remote;
    private final SettingsService settings;
    private final List<Map<String, Object>> demo;

    LawClient(RemoteClient remote, SettingsService settings, ObjectMapper json)
            throws java.io.IOException {
        this.remote = remote;
        this.settings = settings;
        try (var input = new ClassPathResource("data/demo-laws.json").getInputStream()) {
            demo = json.readValue(input, new TypeReference<>() {});
        }
    }

    private List<Map<String, Object>> searchRaw(String source, String query, int page) {
        if (!List.of("law", "admrul").contains(source))
            throw new IllegalArgumentException("법령 구분을 확인하세요.");
        if (settings.demo())
            return page > 1
                    ? List.of()
                    : demo.stream()
                            .filter(
                                    l ->
                                            same(l.get("source_type"), source)
                                                    && str(l, "name").contains(query))
                            .map(LinkedHashMap::new)
                            .map(m -> (Map<String, Object>) m)
                            .toList();
        if (settings.get("law_api_oc").isBlank())
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE, "설정에서 국가법령정보센터 인증키를 입력하세요.");
        var root =
                xml(remote.bytes(
                                query(
                                        "https://www.law.go.kr/DRF/lawSearch.do",
                                        row(
                                                "OC",
                                                settings.get("law_api_oc"),
                                                "target",
                                                source,
                                                "type",
                                                "XML",
                                                "query",
                                                query,
                                                "display",
                                                100,
                                                "page",
                                                page))))
                        .getDocumentElement();
        if (!text(root, "resultCode", "에러코드").isBlank() && elements(root, source).isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "법령 API 인증 또는 요청 오류입니다.");
        var result = new ArrayList<Map<String, Object>>();
        for (var item : elements(root, source)) {
            boolean law = source.equals("law");
            String link = text(item, law ? "법령상세링크" : "행정규칙상세링크");
            if (link.startsWith("/")) link = "https://www.law.go.kr" + link;
            if (!link.matches("https?://[^\\s]+")) link = "";
            result.add(
                    row(
                            "source_type",
                            source,
                            "external_id",
                            text(item, law ? "법령일련번호" : "행정규칙일련번호", law ? "MST" : "ID"),
                            "master_id",
                            text(item, law ? "법령ID" : "행정규칙ID"),
                            "name",
                            law ? text(item, "법령명한글", "법령명_한글", "법령명") : text(item, "행정규칙명"),
                            "category",
                            law ? text(item, "법령구분명", "법종구분") : text(item, "행정규칙종류명", "행정규칙종류"),
                            "department",
                            text(item, "소관부처명", "소관부처"),
                            "promulgation_no",
                            text(item, law ? "공포번호" : "발령번호"),
                            "promulgation_date",
                            text(item, law ? "공포일자" : "발령일자"),
                            "enforcement_date",
                            text(item, "시행일자"),
                            "detail_link",
                            link));
        }
        return result;
    }

    public record Content(String text, int articleLength) {}

    public Content content(LawSnapshot law) {
        if (settings.demo()) {
            String text =
                    demo.stream()
                            .filter(l -> same(l.get("external_id"), law.externalId()))
                            .map(l -> str(l, "content"))
                            .findFirst()
                            .orElse("");
            return new Content(text, 0);
        }
        String source = law.sourceType();
        var root =
                xml(remote.bytes(
                                query(
                                        "https://www.law.go.kr/DRF/lawService.do",
                                        row(
                                                "OC",
                                                settings.get("law_api_oc"),
                                                "target",
                                                source,
                                                "type",
                                                "XML",
                                                source.equals("law") ? "MST" : "ID",
                                                law.externalId()))))
                        .getDocumentElement();
        return extractContent(root);
    }

    public static Content extractContent(org.w3c.dom.Element root) {
        var parts = new ArrayList<String>();
        var covered =
                Collections.newSetFromMap(new IdentityHashMap<org.w3c.dom.Element, Boolean>());
        var tags = List.of("조문내용", "항내용", "호내용", "목내용", "행정규칙내용", "별표내용", "부칙내용");
        for (var unit : elements(root, "조문단위")) {
            var texts = new ArrayList<String>();
            for (String tag : tags)
                for (var element : elements(unit, tag)) {
                    if (!element.getTextContent().isBlank()) {
                        texts.add(element.getTextContent().strip());
                        covered.add(element);
                    }
                }
            if (texts.isEmpty()) continue;
            String block = String.join(" ", texts),
                    number = text(unit, "조문번호"),
                    sub = text(unit, "조문가지번호");
            if (number.matches("\\d{1,4}")) {
                String label = "제" + Integer.parseInt(number) + "조";
                if (sub.matches("\\d{1,3}") && Integer.parseInt(sub) > 0)
                    label += "의" + Integer.parseInt(sub);
                if (!block.startsWith(label)) block = label + " " + block;
            }
            parts.add(block);
        }
        int articleLength = String.join("\n", parts).length();
        for (String tag : tags)
            for (var element : elements(root, tag))
                if (!covered.contains(element) && !element.getTextContent().isBlank())
                    parts.add(element.getTextContent().strip());
        return new Content(String.join("\n", parts), articleLength);
    }

    public List<LawSnapshot> search(String source, String query, int page) {
        return searchRaw(source, query, page).stream().map(LawSnapshot::from).toList();
    }

    public static LawSnapshot match(Law law, List<LawSnapshot> results) {
        for (var r : results)
            if (!Objects.toString(law.getMasterId(), "").isBlank()
                    && Objects.equals(r.masterId(), law.getMasterId())) return r;
        for (var r : results) if (Objects.equals(r.externalId(), law.getExternalId())) return r;
        for (var r : results) if (Objects.equals(r.name(), law.getName())) return r;
        return null;
    }
}
