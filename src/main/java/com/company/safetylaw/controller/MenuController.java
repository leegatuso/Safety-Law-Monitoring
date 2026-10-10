package com.company.safetylaw.controller;

import com.company.safetylaw.security.LoginUser;
import com.company.safetylaw.service.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class MenuController {
    private final LawService laws;
    private final RevisionService revisions;
    private final DocumentService documents;
    private final GuideService guides;
    private final DashboardService dashboard;
    private final NewsService news;
    private final SettingsService settings;

    public MenuController(
            LawService laws,
            RevisionService revisions,
            DocumentService documents,
            GuideService guides,
            DashboardService dashboard,
            NewsService news,
            SettingsService settings) {
        this.laws = laws;
        this.revisions = revisions;
        this.documents = documents;
        this.guides = guides;
        this.dashboard = dashboard;
        this.news = news;
        this.settings = settings;
    }

    @ModelAttribute
    public void common(
            Model model,
            @Value("${app.demo}") boolean demo,
            @AuthenticationPrincipal LoginUser currentUser) {
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("localPreview", demo);
        model.addAttribute("headerCounts", dashboard.counts());
        model.addAttribute("initialData", new LinkedHashMap<String, Object>());
    }

    private String view(Model model, String page) {
        model.addAttribute("page", page);
        return "pages/" + page;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        var summary = dashboard.summary();
        model.addAttribute("summary", summary);
        model.addAttribute(
                "initialData",
                Map.of("/api/dashboard/summary", summary, "/api/news", news.list(null, 40)));
        return view(model, "dashboard");
    }

    @GetMapping("/laws")
    public String laws(Model model) {
        var rows = laws.list(true, null);
        model.addAttribute("laws", rows);
        model.addAttribute("initialData", Map.of("/api/laws", rows));
        return view(model, "laws");
    }

    @GetMapping("/revisions")
    public String revisions(Model model, @RequestParam(required = false) Long law_id) {
        var rows = revisions.list(null, law_id, false, 200);
        model.addAttribute("revisions", rows);
        model.addAttribute(
                "initialData",
                Map.of(
                        law_id == null
                                ? "/api/revisions"
                                : "/api/revisions?tracked_law_id=" + law_id,
                        rows));
        return view(model, "revisions");
    }

    @GetMapping("/documents")
    public String documents(Model model) {
        var rows = documents.list(null);
        model.addAttribute("documents", rows);
        model.addAttribute(
                "initialData", Map.of("/api/documents", rows, "/api/laws", laws.list(true, null)));
        return view(model, "documents");
    }

    @GetMapping("/document-revisions")
    public String history(
            Model model,
            @RequestParam(required = false) Long document_id,
            @RequestParam(defaultValue = "") String query) {
        model.addAttribute("query", query);
        model.addAttribute("documentId", document_id);
        model.addAttribute("history", documents.history(document_id, query, 200));
        return view(model, "document-revisions");
    }

    @GetMapping("/document-impacts")
    public String impacts(Model model) {
        model.addAttribute("page", "doc-revisions");
        return "pages/document-impacts";
    }

    @GetMapping("/kosha-guides")
    public String guides(Model model) {
        var rows = guides.list(null);
        model.addAttribute("guides", rows);
        model.addAttribute("initialData", Map.of("/api/kosha-guides", rows));
        return view(model, "kosha-guides");
    }

    @GetMapping("/news")
    public String news(Model model) {
        return view(model, "news");
    }

    @GetMapping("/search")
    public String search(Model model) {
        return view(model, "keyword-search");
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("initialData", Map.of("/api/settings", settings.output()));
        return view(model, "settings");
    }
}
