package com.company.safetylaw.dto;

import com.company.safetylaw.domain.CompanyDocument;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.validation.constraints.*;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record DocumentSaveRequest(
        @Size(max = 16) String docType,
        @Size(max = 64) String docNumber,
        @NotBlank @Size(max = 255) String title,
        @Size(max = 32) String revisionNo,
        @Pattern(regexp = "^$|[0-9]{8}|[0-9]{4}-[0-9]{2}-[0-9]{2}") String revisionDate,
        @Size(max = 64) String owner,
        @Size(max = 512) String fileLink,
        String note,
        @Size(max = 512) String tags,
        @Size(max = 10000) List<@NotNull @Positive Long> lawIds,
        @Size(max = 4000) String changeReason,
        @Size(max = 64) String changedBy) {
    public CompanyDocument toEntity() {
        var value = new CompanyDocument();
        value.setDocType(docType == null || docType.isBlank() ? "절차서" : docType);
        value.setDocNumber(docNumber);
        value.setTitle(title);
        value.setRevisionNo(revisionNo);
        value.setRevisionDate(revisionDate);
        value.setOwner(owner);
        value.setFileLink(fileLink);
        value.setNote(note);
        value.setTags(tags);
        return value;
    }
}
