package com.company.safetylaw.controller;

import com.company.safetylaw.domain.*;
import com.company.safetylaw.dto.*;
import com.company.safetylaw.service.*;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/content-cache")
public class ContentController {

    private final ContentService contents;

    public ContentController(ContentService contents) {
        this.contents = contents;
    }

    @GetMapping("/status")
    public Map<String, Object> status() {
        return contents.status();
    }

    @GetMapping("/search")
    public List<ContentSearchResult> search(
            @RequestParam String query, @RequestParam(defaultValue = "all") String source_type) {
        return contents.search(source_type, query);
    }

    @PostMapping("/refresh")
    public Map<String, Object> refresh() {
        return contents.refresh();
    }

    @PostMapping("/full-refresh")
    public Map<String, Object> full() {
        return contents.start();
    }

    @GetMapping("/full-refresh-status")
    public Map<String, Object> progress() {
        return contents.progress();
    }
}
