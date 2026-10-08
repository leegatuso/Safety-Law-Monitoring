package com.company.safetylaw.controller;

import com.company.safetylaw.domain.*;
import com.company.safetylaw.dto.*;
import com.company.safetylaw.service.*;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/laws")
public class LawController {

    private final LawService laws;
    private final SyncService sync;
    private final ContentService contents;

    public LawController(LawService laws, SyncService sync, ContentService contents) {
        this.laws = laws;
        this.sync = sync;
        this.contents = contents;
    }

    @GetMapping
    public List<Law> list(
            @RequestParam(defaultValue = "true") boolean active_only,
            @RequestParam(required = false) String source_type) {
        return laws.list(active_only, source_type);
    }

    @GetMapping("/search")
    public List<LawSnapshot> search(@RequestParam String source_type, @RequestParam String query) {
        return laws.search(source_type, query);
    }

    @PostMapping
    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    public Law add(@Valid @RequestBody LawCreateRequest request) {
        var law = laws.register(request);
        String warning = null;
        try {
            sync.syncOne(law.getId());
            contents.cache(LawSnapshot.from(laws.get(law.getId())));
        } catch (org.springframework.web.server.ResponseStatusException
                | IllegalArgumentException ex) {
            warning = "등록했습니다. 외부 조회는 동기화로 재시도하세요.";
        }
        var result = laws.get(law.getId());
        result.setSyncWarning(warning);
        return result;
    }

    @GetMapping("/{id}")
    public Law get(@PathVariable long id) {
        return laws.get(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        laws.deactivate(id);
    }
}
