package com.company.safetylaw.dto;

import jakarta.validation.constraints.*;

import java.util.List;

public record BulkIdsRequest(@NotEmpty @Size(max = 10000) List<@NotNull @Positive Long> ids) {}
