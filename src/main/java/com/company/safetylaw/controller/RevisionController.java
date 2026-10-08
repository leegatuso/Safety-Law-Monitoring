package com.company.safetylaw.controller;

import com.company.safetylaw.domain.*;
import com.company.safetylaw.dto.*;
import com.company.safetylaw.service.*;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/revisions")
public class RevisionController {

    private final RevisionService revisions;

    public RevisionController(RevisionService revisions) {
        this.revisions = revisions;
    }

    @GetMapping
    public List<LawRevision> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long tracked_law_id,
            @RequestParam(defaultValue = "false") boolean has_mapped_documents,
            @RequestParam(defaultValue = "200") int limit) {
        return revisions.list(status, tracked_law_id, has_mapped_documents, limit);
    }

    @PatchMapping("/{id}")
    public LawRevision review(@PathVariable long id, @Valid @RequestBody ReviewRequest request) {
        return revisions.review(id, request);
    }

    @PatchMapping("/bulk-status")
    public List<LawRevision> bulk(@Valid @RequestBody BulkReviewRequest request) {
        return revisions.reviewBulk(request);
    }

    @PostMapping("/bulk-delete")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void delete(@Valid @RequestBody BulkIdsRequest request) {
        revisions.delete(request.ids());
    }
}
