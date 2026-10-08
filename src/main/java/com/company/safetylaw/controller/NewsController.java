package com.company.safetylaw.controller;

import com.company.safetylaw.domain.*;
import com.company.safetylaw.dto.*;
import com.company.safetylaw.service.*;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService news;

    public NewsController(NewsService news) {
        this.news = news;
    }

    public record Archive(
            @jakarta.validation.constraints.NotNull
                    @com.fasterxml.jackson.annotation.JsonProperty("is_archived")
                    Boolean archived) {}

    @GetMapping
    public List<NewsItem> list(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "40") int limit) {
        return news.list(category, limit);
    }

    @GetMapping("/search")
    public Map<String, Object> search(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String date_from,
            @RequestParam(required = false) String date_to,
            @RequestParam(required = false) Boolean archived,
            @RequestParam(defaultValue = "200") int limit) {
        return news.search(category, q, date_from, date_to, archived, limit);
    }

    @PatchMapping("/{id}/archive")
    public NewsItem archive(@PathVariable long id, @Valid @RequestBody Archive request) {
        return news.archive(id, request.archived());
    }

    @PostMapping("/sync")
    public Map<String, Object> sync() {
        return news.sync();
    }
}
