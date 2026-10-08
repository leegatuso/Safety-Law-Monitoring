package com.company.safetylaw.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DocumentRevision {
    private Long id;
    private Long documentId;
    private String title;
    private String docType;
    private String docNumber;
    private String revisionNo;
    private String revisionDate;
    private String owner;
    private String fileLink;
    private String note;
    private String tags;
    private String changeReason;
    private String changedBy;
    private String changedAt;
    private String mappedLawIds;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String value) {
        this.title = value;
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

    public String getChangeReason() {
        return changeReason;
    }

    public void setChangeReason(String value) {
        this.changeReason = value;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String value) {
        this.changedBy = value;
    }

    public String getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(String value) {
        this.changedAt = value;
    }

    public String getMappedLawIds() {
        return mappedLawIds;
    }

    public void setMappedLawIds(String value) {
        this.mappedLawIds = value;
    }
}
