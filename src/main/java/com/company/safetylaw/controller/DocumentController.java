package com.company.safetylaw.controller;

import com.company.safetylaw.domain.*;
import com.company.safetylaw.dto.*;
import com.company.safetylaw.service.*;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documents;

    public DocumentController(DocumentService documents) {
        this.documents = documents;
    }

    @GetMapping
    public List<CompanyDocument> list(@RequestParam(required = false) String doc_type) {
        return documents.list(doc_type);
    }

    @GetMapping("/{id}")
    public CompanyDocument get(@PathVariable long id) {
        return documents.get(id);
    }

    @PostMapping
    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    public CompanyDocument add(@Valid @RequestBody DocumentSaveRequest request) {
        return documents.save(null, request);
    }

    @PutMapping("/{id}")
    public CompanyDocument update(
            @PathVariable long id, @Valid @RequestBody DocumentSaveRequest request) {
        return documents.save(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        documents.delete(id);
    }

    @GetMapping("/impacts")
    public List<DocumentImpact> impacts(@RequestParam(required = false) String status) {
        return documents.impacts(status, false);
    }
}
