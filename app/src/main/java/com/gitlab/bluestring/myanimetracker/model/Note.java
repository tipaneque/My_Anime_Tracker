package com.gitlab.bluestring.myanimetracker.model;

public class Note {
    private int id;
    private int post_id;
    private String body;
    private String created_at;

    public Note() {}

    public Note(int id, int post_id, String body) {
        this.id = id;
        this.post_id = post_id;
        this.body = body;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPostId() { return post_id; }
    public void setPostId(int post_id) { this.post_id = post_id; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getCreatedAt() { return created_at; }
    public void setCreatedAt(String created_at) { this.created_at = created_at; }
}