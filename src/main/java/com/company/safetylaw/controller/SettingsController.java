package com.company.safetylaw.controller;

import com.company.safetylaw.domain.*;
import com.company.safetylaw.dto.*;
import com.company.safetylaw.service.*;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    private final SettingsService settings;

    public SettingsController(SettingsService settings) {
        this.settings = settings;
    }

    @GetMapping
    public Map<String, Object> get() {
        return settings.output();
    }

    @PutMapping
    public Map<String, Object> save(@RequestBody Map<String, Object> request) {
        settings.save(request);
        return settings.output();
    }

    @GetMapping("/help-shown")
    public Map<String, Boolean> help() {
        return Map.of("shown", "1".equals(settings.get("help_shown_once")));
    }

    @PostMapping("/help-shown")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void shown() {
        settings.put("help_shown_once", "1");
    }
}
