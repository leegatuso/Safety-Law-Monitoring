package com.company.safetylaw.service;

import static com.company.safetylaw.support.Rows.row;

import com.company.safetylaw.mapper.*;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DashboardService {
    private final DashboardMapper dashboard;
    private final RevisionService revisions;
    private final DocumentService documents;
    private final CandidateService candidates;

    public DashboardService(
            DashboardMapper dashboard,
            RevisionService revisions,
            DocumentService documents,
            CandidateService candidates) {
        this.dashboard = dashboard;
        this.revisions = revisions;
        this.documents = documents;
        this.candidates = candidates;
    }

    public com.company.safetylaw.dto.DashboardCounts counts() {
        return dashboard.counts();
    }

    public Map<String, Object> summary() {
        var c = dashboard.counts();
        return row(
                "tracked_law_count",
                c.getTrackedLawCount(),
                "document_count",
                c.getDocumentCount(),
                "unmapped_law_count",
                c.getUnmappedLawCount(),
                "last_sync_at",
                c.getLastSyncAt(),
                "unreviewed_count",
                c.getUnreviewedCount(),
                "in_review_count",
                c.getInReviewCount(),
                "reflected_count",
                c.getReflectedCount(),
                "recent_revisions",
                revisions.pending(200),
                "recent_document_impacts",
                documents.impacts(null, true),
                "new_admrul_candidates",
                candidates.list("신규"));
    }
}
