package com.example.nexs;

public class NewCard {
    private int imgResourceId;
    private String newsHeadLine;

    public NewCard(String newsHeadLine, int imgId) {
        this.newsHeadLine = newsHeadLine;
        this.imgResourceId = imgId;
    }

    public int getImgResourceId() {
        return imgResourceId;
    }

    public String getNewsHeadLine() {
        return newsHeadLine;
    }
}
