package com.github.tipaneque.myanimetracker.models;

import com.google.gson.annotations.SerializedName;

public class MainPicture {
    @SerializedName("medium")
    private String medium;

    @SerializedName("large")
    private String large;

    // Constructors
    public MainPicture() {}

    public MainPicture(String medium, String large) {
        this.medium = medium;
        this.large = large;
    }

    // Getters and Setters
    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getLarge() {
        return large;
    }

    public void setLarge(String large) {
        this.large = large;
    }
}
