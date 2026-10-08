package com.company.safetylaw.controller;

import com.company.safetylaw.domain.*;
import com.company.safetylaw.dto.*;
import com.company.safetylaw.service.*;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/laws/new-admrul-candidates")
public class CandidateController {

    private final CandidateService candidates;

    public CandidateController(CandidateService candidates) {
        this.candidates = candidates;
    }

    @GetMapping("/dismissed")
    public List<LawCandidate> dismissed() {
        return candidates.list("무시됨");
    }

    @PostMapping("/{id}/{action}")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void action(@PathVariable long id, @PathVariable String action) {
        candidates.action(id, action);
    }

    @PostMapping("/bulk-restore")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void restore(@Valid @RequestBody BulkIdsRequest request) {
        candidates.restore(request.ids());
    }
}
