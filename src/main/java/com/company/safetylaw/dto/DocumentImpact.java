package com.company.safetylaw.dto;

import com.company.safetylaw.domain.LawRevision;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record DocumentImpact(
        long documentId, String documentTitle, String docType, List<LawRevision> revisions) {}
