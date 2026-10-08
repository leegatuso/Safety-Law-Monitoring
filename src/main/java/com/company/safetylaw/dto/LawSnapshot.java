package com.company.safetylaw.dto;

import static com.company.safetylaw.support.Rows.str;

import com.company.safetylaw.domain.*;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.Map;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record LawSnapshot(
        String sourceType,
        String externalId,
        String masterId,
        String name,
        String category,
        String department,
        String promulgationNo,
        String promulgationDate,
        String enforcementDate,
        String detailLink) {
    public static LawSnapshot from(Map<String, Object> m) {
        return new LawSnapshot(
                str(m, "source_type"),
                str(m, "external_id"),
                str(m, "master_id"),
                str(m, "name"),
                str(m, "category"),
                str(m, "department"),
                str(m, "promulgation_no"),
                str(m, "promulgation_date"),
                str(m, "enforcement_date"),
                str(m, "detail_link"));
    }

    public static LawSnapshot from(Law l) {
        return new LawSnapshot(
                l.getSourceType(),
                l.getExternalId(),
                l.getMasterId(),
                l.getName(),
                l.getCategory(),
                l.getDepartment(),
                l.getCurrentPromulgationNo(),
                l.getCurrentPromulgationDate(),
                l.getCurrentEnforcementDate(),
                l.getDetailLink());
    }

    public LawContent toContent() {
        var c = new LawContent();
        c.setSourceType(sourceType);
        c.setExternalId(externalId);
        c.setMasterId(masterId);
        c.setName(name);
        c.setCategory(category);
        c.setDepartment(department);
        c.setPromulgationNo(promulgationNo);
        c.setPromulgationDate(promulgationDate);
        c.setEnforcementDate(enforcementDate);
        c.setDetailLink(detailLink);
        return c;
    }

    public LawCandidate toCandidate() {
        var c = new LawCandidate();
        c.setSourceType(sourceType);
        c.setExternalId(externalId);
        c.setMasterId(masterId);
        c.setName(name);
        c.setCategory(category);
        c.setDepartment(department);
        c.setPromulgationNo(promulgationNo);
        c.setPromulgationDate(promulgationDate);
        c.setEnforcementDate(enforcementDate);
        c.setDetailLink(detailLink);
        return c;
    }
}
