package com.company.safetylaw.dto;

import com.company.safetylaw.domain.KoshaGuide;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.validation.constraints.*;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GuideSaveRequest(
        @Size(max = 64) String code,
        @Size(max = 64) String field,
        @NotBlank @Size(max = 512) String title,
        @Pattern(regexp = "^$|[0-9]{8}|[0-9]{4}-[0-9]{2}-[0-9]{2}") String issuedDate,
        @Size(max = 512) String fileLink,
        String content,
        String note) {
    public KoshaGuide toEntity() {
        var value = new KoshaGuide();
        value.setCode(code);
        value.setField(field);
        value.setTitle(title);
        value.setIssuedDate(issuedDate);
        value.setFileLink(fileLink);
        value.setContent(content);
        value.setNote(note);
        return value;
    }
}
