package com.company.safetylaw.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.validation.constraints.*;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ReviewRequest(
        @Pattern(regexp = "미검토|검토중|반영완료|해당없음") String reviewStatus,
        @Size(max = 64) String reviewer,
        String note) {}
