package com.company.safetylaw.dto;

import com.company.safetylaw.domain.Law;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.validation.constraints.*;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record LawCreateRequest(
        @NotBlank @Size(max = 16) String sourceType,
        @NotBlank @Size(max = 64) String externalId,
        @Size(max = 64) String masterId,
        @NotBlank @Size(max = 255) String name,
        @Size(max = 64) String category,
        @Size(max = 128) String department,
        @Size(max = 64) String promulgationNo,
        @Pattern(regexp = "^$|[0-9]{8}|[0-9]{4}-[0-9]{2}-[0-9]{2}") String promulgationDate,
        @Pattern(regexp = "^$|[0-9]{8}|[0-9]{4}-[0-9]{2}-[0-9]{2}") String enforcementDate,
        @Size(max = 512) String detailLink) {
    public Law toEntity() {
        var value = new Law();
        value.setSourceType(sourceType);
        value.setExternalId(externalId);
        value.setMasterId(masterId);
        value.setName(name);
        value.setCategory(category);
        value.setDepartment(department);
        value.setCurrentPromulgationNo(promulgationNo);
        value.setCurrentPromulgationDate(promulgationDate);
        value.setCurrentEnforcementDate(enforcementDate);
        value.setDetailLink(detailLink);
        return value;
    }
}
