package com.gitlab.bluestring.myanimetracker.model;

public class User {
    private int id;
    private String email;
    private String fullname;

    public User() {
        // Construtor vazio
    }

    public User(int id, String email, String fullname) {
        this.id = id;
        this.email = email;
        this.fullname = fullname;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }
}