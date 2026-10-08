package com.company.safetylaw.dto;

import com.company.safetylaw.domain.LawContent;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ContentSearchResult(
        String sourceType,
        String externalId,
        String masterId,
        String name,
        String category,
        String department,
        String promulgationNo,
        String promulgationDate,
        String enforcementDate,
        String detailLink,
        String matchedIn,
        String snippet,
        String articleLink) {
    public static ContentSearchResult from(LawContent c, String query) {
        String body = c.getContent() == null ? "" : c.getContent();
        return new ContentSearchResult(
                c.getSourceType(),
                c.getExternalId(),
                c.getMasterId(),
                c.getName(),
                c.getCategory(),
                c.getDepartment(),
                c.getPromulgationNo(),
                c.getPromulgationDate(),
                c.getEnforcementDate(),
                c.getDetailLink(),
                body.contains(query) ? "content" : "name",
                com.company.safetylaw.support.ContentText.snippet(body, query),
                com.company.safetylaw.support.ContentText.articleLink(c, query));
    }
}
