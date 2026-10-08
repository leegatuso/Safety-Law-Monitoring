package com.company.safetylaw.controller;

import com.company.safetylaw.domain.*;
import com.company.safetylaw.dto.*;
import com.company.safetylaw.service.*;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/document-revisions")
public class DocumentRevisionController {

    private final DocumentService documents;

    public DocumentRevisionController(DocumentService documents) {
        this.documents = documents;
    }

    @GetMapping
    public List<DocumentRevision> list(
            @RequestParam(required = false) Long document_id,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "200") int limit) {
        return documents.history(document_id, query, limit);
    }
}
