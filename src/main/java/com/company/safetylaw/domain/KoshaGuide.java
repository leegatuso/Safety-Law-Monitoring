package com.company.safetylaw.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KoshaGuide {
    private Long id;
    private String code;
    private String field;
    private String title;
    private String issuedDate;
    private String fileLink;
    private String content;
    private Boolean contentStale;
    private String note;
    private String createdAt;
    private String updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String value) {
        this.code = value;
    }

    public String getField() {
        return field;
    }

    public void setField(String value) {
        this.field = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String value) {
        this.title = value;
    }

    public String getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(String value) {
        this.issuedDate = value;
    }

    public String getFileLink() {
        return fileLink;
    }

    public void setFileLink(String value) {
        this.fileLink = value;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String value) {
        this.content = value;
    }

    public Boolean getContentStale() {
        return contentStale;
    }

    public void setContentStale(Boolean value) {
        this.contentStale = value;
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

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String value) {
        this.updatedAt = value;
    }
}
