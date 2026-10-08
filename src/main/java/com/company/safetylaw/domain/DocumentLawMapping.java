package com.company.safetylaw.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DocumentLawMapping {
    private Long id;
    private Long documentId;
    private Long trackedLawId;
    private String note;
    private String createdAt;
    private String documentTitle;
    private String trackedLawName;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long value) {
        this.documentId = value;
    }

    public Long getTrackedLawId() {
        return trackedLawId;
    }

    public void setTrackedLawId(Long value) {
        this.trackedLawId = value;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String value) {
        this.note = value;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String value) {
        this.createdAt = value;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String value) {
        this.documentTitle = value;
    }

    public String getTrackedLawName() {
        return trackedLawName;
    }

    public void setTrackedLawName(String value) {
        this.trackedLawName = value;
    }
}
