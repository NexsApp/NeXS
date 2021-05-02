package com.example.nexs.models;

public class NewCard {
    private String id;
    private String imgResource;
    private String newsHeadLine;

    public NewCard(String id, String newsHeadLine, String imgResource) {
        this.id = id;
        this.newsHeadLine = newsHeadLine;
        this.imgResource = imgResource;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgResource() {
        return imgResource;
    }

    public String getNewsHeadLine() {
        return newsHeadLine;
    }
}
