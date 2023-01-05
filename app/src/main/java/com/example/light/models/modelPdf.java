package com.example.light.models;

public class modelPdf {
    String uid, id, title, description, categoryId, url ;
    String timestamp;

    public modelPdf() {
    }

    public modelPdf(String uid, String id, String title, String description, String categoryId, String url, String timestamp) {
        this.uid = uid;
        this.id = id;
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
        this.url = url;
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getUrl() {
        return url;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
