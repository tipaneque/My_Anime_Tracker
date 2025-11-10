package com.gitlab.bluestring.myanimetracker.model;
import java.util.List;
import java.util.ArrayList;

public class Post {
    private int id;
    private String file_url;
    private String tag_string;
    private String rating;
    private int score;
    private int favorite_count;
    private Boolean is_favorited;
    private List<Note> notes;

    // Construtores
    public Post() {}

    public Post(int id, String file_url, String tag_string, String rating, int score) {
        this.id = id;
        this.file_url = file_url;
        this.tag_string = tag_string;
        this.rating = rating;
        this.score = score;
        this.notes = new ArrayList<>();
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFileUrl() { return file_url; }
    public void setFileUrl(String file_url) { this.file_url = file_url; }

    public String getTagString() { return tag_string; }
    public void setTagString(String tag_string) { this.tag_string = tag_string; }

    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getFavoriteCount() { return favorite_count; }
    public void setFavoriteCount(int favorite_count) { this.favorite_count = favorite_count; }

    public Boolean getIsFavorited() { return is_favorited; }
    public void setIsFavorited(Boolean is_favorited) { this.is_favorited = is_favorited; }

    public List<Note> getNotes() { return notes; }
    public void setNotes(List<Note> notes) { this.notes = notes; }
}