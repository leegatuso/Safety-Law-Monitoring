package com.company.safetylaw.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompanyDocument {
    private Long id;
    private String docType;
    private String docNumber;
    private String title;
    private String revisionNo;
    private String revisionDate;
    private String owner;
    private String fileLink;
    private String note;
    private String tags;
    private String createdAt;
    private String updatedAt;
    private Integer mappedLawCount = 0;
    private List<String> mappedLaws = List.of();

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String value) {
        this.docType = value;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String value) {
        this.docNumber = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String value) {
        this.title = value;
    }

    public String getRevisionNo() {
        return revisionNo;
    }

    public void setRevisionNo(String value) {
        this.revisionNo = value;
    }

    public String getRevisionDate() {
        return revisionDate;
    }

    public void setRevisionDate(String value) {
        this.revisionDate = value;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String value) {
        this.owner = value;
    }

    public String getFileLink() {
        return fileLink;
    }

    public void setFileLink(String value) {
        this.fileLink = value;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String value) {
        this.note = value;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String value) {
        this.tags = value;
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

    public Integer getMappedLawCount() {
        return mappedLawCount;
    }

    public void setMappedLawCount(Integer value) {
        this.mappedLawCount = value;
    }

    public List<String> getMappedLaws() {
        return mappedLaws;
    }

    public void setMappedLaws(List<String> value) {
        this.mappedLaws = value;
    }
}
