package com.company.safetylaw.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LawCandidate {
    private Long id;
    private String sourceType;
    private String externalId;
    private String masterId;
    private String name;
    private String category;
    private String department;
    private String promulgationNo;
    private String promulgationDate;
    private String enforcementDate;
    private String detailLink;
    private String matchedKeyword;
    private String status;
    private String firstSeenAt;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String value) {
        this.sourceType = value;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String value) {
        this.externalId = value;
    }

    public String getMasterId() {
        return masterId;
    }

    public void setMasterId(String value) {
        this.masterId = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String value) {
        this.category = value;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String value) {
        this.department = value;
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

    public String getDetailLink() {
        return detailLink;
    }

    public void setDetailLink(String value) {
        this.detailLink = value;
    }

    public String getMatchedKeyword() {
        return matchedKeyword;
    }

    public void setMatchedKeyword(String value) {
        this.matchedKeyword = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String value) {
        this.status = value;
    }

    public String getFirstSeenAt() {
        return firstSeenAt;
    }

    public void setFirstSeenAt(String value) {
        this.firstSeenAt = value;
    }
}
