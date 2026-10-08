package com.company.safetylaw.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.validation.constraints.*;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record BulkReviewRequest(
        @NotEmpty @Size(max = 10000) List<@NotNull @Positive Long> ids,
        @NotBlank @Pattern(regexp = "미검토|검토중|반영완료|해당없음") String reviewStatus) {}
