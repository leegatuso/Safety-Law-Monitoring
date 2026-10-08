package com.company.safetylaw.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LawRevision {
    private Long id;
    private Long trackedLawId;
    private String promulgationNo;
    private String promulgationDate;
    private String enforcementDate;
    private String previousPromulgationNo;
    private String previousPromulgationDate;
    private String previousEnforcementDate;
    private String detectedAt;
    private String reviewStatus;
    private String reviewer;
    private String reviewedAt;
    private String note;
    private String rawData;
    private String trackedLawName;
    private String trackedLawCategory;
    private String trackedLawSourceType;
    private String trackedLawExternalId;
    private String trackedLawDetailLink;
    private List<String> mappedDocuments = List.of();
    private String matchedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public Long getTrackedLawId() {
        return trackedLawId;
    }

    public void setTrackedLawId(Long value) {
        this.trackedLawId = value;
    }

    public String getPromulgationNo() {
        return promulgationNo;
    }

    public void setPromulgationNo(String value) {
        this.promulgationNo = value;
    }

    public String getPromulgationDate() {
        return promulgationDate;
    }

    public void setPromulgationDate(String value) {
        this.promulgationDate = value;
    }

    public String getEnforcementDate() {
        return enforcementDate;
    }

    public void setEnforcementDate(String value) {
        this.enforcementDate = value;
    }

    public String getPreviousPromulgationNo() {
        return previousPromulgationNo;
    }

    public void setPreviousPromulgationNo(String value) {
        this.previousPromulgationNo = value;
    }

    public String getPreviousPromulgationDate() {
        return previousPromulgationDate;
    }

    public void setPreviousPromulgationDate(String value) {
        this.previousPromulgationDate = value;
    }

    public String getPreviousEnforcementDate() {
        return previousEnforcementDate;
    }

    public void setPreviousEnforcementDate(String value) {
        this.previousEnforcementDate = value;
    }

    public String getDetectedAt() {
        return detectedAt;
    }

    public void setDetectedAt(String value) {
        this.detectedAt = value;
    }

    public String getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(String value) {
        this.reviewStatus = value;
    }

    public String getReviewer() {
        return reviewer;
    }

    public void setReviewer(String value) {
        this.reviewer = value;
    }

    public String getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(String value) {
        this.reviewedAt = value;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String value) {
        this.note = value;
    }

    public String getRawData() {
        return rawData;
    }

    public void setRawData(String value) {
        this.rawData = value;
    }

    public String getTrackedLawName() {
        return trackedLawName;
    }

    public void setTrackedLawName(String value) {
        this.trackedLawName = value;
    }

    public String getTrackedLawCategory() {
        return trackedLawCategory;
    }

    public void setTrackedLawCategory(String value) {
        this.trackedLawCategory = value;
    }

    public String getTrackedLawSourceType() {
        return trackedLawSourceType;
    }

    public void setTrackedLawSourceType(String value) {
        this.trackedLawSourceType = value;
    }

    public String getTrackedLawExternalId() {
        return trackedLawExternalId;
    }

    public void setTrackedLawExternalId(String value) {
        this.trackedLawExternalId = value;
    }

    public String getTrackedLawDetailLink() {
        return trackedLawDetailLink;
    }

    public void setTrackedLawDetailLink(String value) {
        this.trackedLawDetailLink = value;
    }

    public List<String> getMappedDocuments() {
        return mappedDocuments;
    }

    public void setMappedDocuments(List<String> value) {
        this.mappedDocuments = value;
    }

    public String getMatchedBy() {
        return matchedBy;
    }

    public void setMatchedBy(String value) {
        this.matchedBy = value;
    }
}
