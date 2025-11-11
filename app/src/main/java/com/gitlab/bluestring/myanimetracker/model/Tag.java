package com.gitlab.bluestring.myanimetracker.model;

public class Tag {
    private String name;
    private int post_count;

    public Tag() {}

    public Tag(String name, int post_count) {
        this.name = name;
        this.post_count = post_count;
    }

    // Getters e Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getPostCount() { return post_count; }
    public void setPostCount(int post_count) { this.post_count = post_count; }
}