package com.company.safetylaw.dto;

public class DashboardCounts {
    private long trackedLawCount,
            documentCount,
            unmappedLawCount,
            unreviewedCount,
            inReviewCount,
            reflectedCount;
    private String lastSyncAt;
    private Long latestUnreviewedId;

    public Long getLatestUnreviewedId() {
        return latestUnreviewedId;
    }

    public void setLatestUnreviewedId(Long value) {
        latestUnreviewedId = value;
    }

    public long getTrackedLawCount() {
        return trackedLawCount;
    }

    public void setTrackedLawCount(long v) {
        trackedLawCount = v;
    }

    public long getDocumentCount() {
        return documentCount;
    }

    public void setDocumentCount(long v) {
        documentCount = v;
    }

    public long getUnmappedLawCount() {
        return unmappedLawCount;
    }

    public void setUnmappedLawCount(long v) {
        unmappedLawCount = v;
    }

    public long getUnreviewedCount() {
        return unreviewedCount;
    }

    public void setUnreviewedCount(long v) {
        unreviewedCount = v;
    }

    public long getInReviewCount() {
        return inReviewCount;
    }

    public void setInReviewCount(long v) {
        inReviewCount = v;
    }

    public long getReflectedCount() {
        return reflectedCount;
    }

    public void setReflectedCount(long v) {
        reflectedCount = v;
    }

    public String getLastSyncAt() {
        return lastSyncAt;
    }

    public void setLastSyncAt(String v) {
        lastSyncAt = v;
    }
}
