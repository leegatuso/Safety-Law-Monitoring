package com.company.safetylaw.controller;

import com.company.safetylaw.domain.*;
import com.company.safetylaw.dto.*;
import com.company.safetylaw.service.*;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/kosha-guides")
public class GuideController {

    private final GuideService guides;
    private final GuideFileService files;

    public GuideController(GuideService guides, GuideFileService files) {
        this.guides = guides;
        this.files = files;
    }

    public record Bulk(
            @jakarta.validation.constraints.NotNull
                    @jakarta.validation.constraints.Size(max = 10000)
                    List<@jakarta.validation.constraints.NotNull @Valid GuideSaveRequest> items) {}

    @GetMapping
    public List<KoshaGuide> list(@RequestParam(required = false) String field) {
        return guides.list(field);
    }

    @GetMapping("/search")
    public List<Map<String, Object>> search(@RequestParam String query) {
        return guides.search(query);
    }

    @PostMapping
    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    public KoshaGuide add(@Valid @RequestBody GuideSaveRequest request) {
        return guides.save(null, request);
    }

    @PutMapping("/{id}")
    public KoshaGuide update(@PathVariable long id, @Valid @RequestBody GuideSaveRequest request) {
        return guides.save(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) throws java.io.IOException {
        guides.delete(id);
    }

    @PostMapping("/bulk-delete")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void deleteBulk(@Valid @RequestBody BulkIdsRequest request) throws java.io.IOException {
        guides.deleteBulk(request.ids());
    }

    @PostMapping("/bulk-import")
    public Map<String, Object> bulk(@Valid @RequestBody Bulk request) {
        return guides.bulk(request.items());
    }

    @PostMapping(value = "/{id}/file", consumes = "multipart/form-data")
    public KoshaGuide upload(
            @PathVariable long id,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file)
            throws java.io.IOException {
        return guides.upload(id, file.getBytes());
    }

    @GetMapping("/{id}/file")
    public org.springframework.http.ResponseEntity<org.springframework.core.io.FileSystemResource>
            download(@PathVariable long id) {
        guides.get(id);
        if (!files.exists(id))
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND, "첨부 파일이 없습니다.");
        return org.springframework.http.ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .header("Content-Disposition", "inline; filename=kosha-" + id + ".pdf")
                .body(new org.springframework.core.io.FileSystemResource(files.path(id)));
    }

    @GetMapping("/{id}/original")
    public org.springframework.http.ResponseEntity<?> original(@PathVariable long id) {
        var g = guides.get(id);
        if (files.exists(id)) return download(id);
        if (com.company.safetylaw.support.Checks.text(g.getFileLink()).isBlank())
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND, "원문 링크나 PDF를 등록하세요.");
        com.company.safetylaw.support.Checks.link(g.getFileLink());
        return org.springframework.http.ResponseEntity.status(302)
                .header("Location", g.getFileLink())
                .build();
    }

    @DeleteMapping("/{id}/file")
    public KoshaGuide deleteFile(@PathVariable long id) throws java.io.IOException {
        return guides.deleteFile(id);
    }

    @PostMapping("/cache-content")
    public Map<String, Object> cache(@RequestParam(defaultValue = "20") int limit) {
        return guides.cache(limit);
    }

    @PostMapping("/sync")
    public Map<String, Object> sync() {
        return guides.sync();
    }
}
