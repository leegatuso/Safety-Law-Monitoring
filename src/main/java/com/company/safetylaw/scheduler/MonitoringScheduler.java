package com.company.safetylaw.scheduler;

import com.company.safetylaw.service.*;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MonitoringScheduler {
    private final SyncService sync;
    private final NewsService news;
    private final ContentService contents;
    private final SettingsService settings;

    public MonitoringScheduler(
            SyncService sync, NewsService news, ContentService contents, SettingsService settings) {
        this.sync = sync;
        this.news = news;
        this.contents = contents;
        this.settings = settings;
    }

    @Scheduled(
            fixedDelayString = "${app.sync-delay-ms}",
            initialDelayString = "${app.sync-delay-ms}")
    public void laws() {
        if (!settings.get("law_api_oc").isBlank()) sync.run();
    }

    @Scheduled(
            fixedDelayString = "${app.news-delay-ms}",
            initialDelayString = "${app.news-delay-ms}")
    public void news() {
        news.sync();
    }

    @Scheduled(cron = "0 0 1 * * *", zone = "Asia/Seoul")
    public void contents() {
        if (Boolean.parseBoolean(settings.get("full_law_cache_enabled"))
                && !Boolean.TRUE.equals(contents.progress().get("running"))) contents.start();
    }
}
