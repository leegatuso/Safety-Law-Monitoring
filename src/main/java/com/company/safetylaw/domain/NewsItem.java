package com.company.safetylaw.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class NewsItem {
    private Long id;
    private String category;
    private String sourceName;
    private String title;
    private String link;
    private String guid;
    private String publishedAt;
    private String fetchedAt;
    private Boolean isDemo;
    private Boolean isArchived;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String value) {
        this.category = value;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String value) {
        this.sourceName = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String value) {
        this.title = value;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String value) {
        this.link = value;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String value) {
        this.guid = value;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String value) {
        this.publishedAt = value;
    }

    public String getFetchedAt() {
        return fetchedAt;
    }

    public void setFetchedAt(String value) {
        this.fetchedAt = value;
    }

    public Boolean getIsDemo() {
        return isDemo;
    }

    public void setIsDemo(Boolean value) {
        this.isDemo = value;
    }

    public Boolean getIsArchived() {
        return isArchived;
    }

    public void setIsArchived(Boolean value) {
        this.isArchived = value;
    }
}
