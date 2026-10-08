package com.company.safetylaw.controller;

import com.company.safetylaw.domain.*;
import com.company.safetylaw.dto.*;
import com.company.safetylaw.service.*;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/mappings")
public class MappingController {

    private final MappingService mappings;

    public MappingController(MappingService mappings) {
        this.mappings = mappings;
    }

    @com.fasterxml.jackson.databind.annotation.JsonNaming(
            com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Create(
            @jakarta.validation.constraints.NotNull @jakarta.validation.constraints.Positive
                    Long documentId,
            @jakarta.validation.constraints.NotNull @jakarta.validation.constraints.Positive
                    Long trackedLawId,
            @jakarta.validation.constraints.Size(max = 255) String note) {}

    @GetMapping
    public List<DocumentLawMapping> list(
            @RequestParam(required = false) Long document_id,
            @RequestParam(required = false) Long tracked_law_id) {
        return mappings.list(document_id, tracked_law_id);
    }

    @PostMapping
    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    public DocumentLawMapping add(@Valid @RequestBody Create request) {
        return mappings.add(request.documentId(), request.trackedLawId(), request.note());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        mappings.delete(id);
    }
}
