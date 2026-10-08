package com.company.safetylaw.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Law {
    private Long id;
    private String sourceType;
    private String externalId;
    private String masterId;
    private String name;
    private String category;
    private String department;
    private String currentPromulgationNo;
    private String currentPromulgationDate;
    private String currentEnforcementDate;
    private String detailLink;
    private Boolean isActive;
    private String lastSyncedAt;
    private String createdAt;
    private String updatedAt;
    private Integer mappedDocumentCount = 0;
    private Integer unreviewedRevisionCount = 0;
    private String syncWarning;

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

    public String getCurrentPromulgationNo() {
        return currentPromulgationNo;
    }

    public void setCurrentPromulgationNo(String value) {
        this.currentPromulgationNo = value;
    }

    public String getCurrentPromulgationDate() {
        return currentPromulgationDate;
    }

    public void setCurrentPromulgationDate(String value) {
        this.currentPromulgationDate = value;
    }

    public String getCurrentEnforcementDate() {
        return currentEnforcementDate;
    }

    public void setCurrentEnforcementDate(String value) {
        this.currentEnforcementDate = value;
    }

    public String getDetailLink() {
        return detailLink;
    }

    public void setDetailLink(String value) {
        this.detailLink = value;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean value) {
        this.isActive = value;
    }

    public String getLastSyncedAt() {
        return lastSyncedAt;
    }

    public void setLastSyncedAt(String value) {
        this.lastSyncedAt = value;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String value) {
        this.createdAt = value;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String value) {
        this.updatedAt = value;
    }

    public Integer getMappedDocumentCount() {
        return mappedDocumentCount;
    }

    public void setMappedDocumentCount(Integer value) {
        this.mappedDocumentCount = value;
    }

    public Integer getUnreviewedRevisionCount() {
        return unreviewedRevisionCount;
    }

    public void setUnreviewedRevisionCount(Integer value) {
        this.unreviewedRevisionCount = value;
    }

    public String getSyncWarning() {
        return syncWarning;
    }

    public void setSyncWarning(String value) {
        this.syncWarning = value;
    }
}
