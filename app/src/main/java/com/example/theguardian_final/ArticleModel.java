package com.example.theguardian_final;

public class ArticleModel {
    private int id;
    private String title;
    private String url;
    private String sectionName;
    private boolean favorite; // New field for favorite status

    public ArticleModel(String title, String url, String sectionName) {
        this.title = title;
        this.url = url;
        this.sectionName = sectionName;
        this.favorite = false; // Set default favorite status to false
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getSectionName() {
        return sectionName;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
