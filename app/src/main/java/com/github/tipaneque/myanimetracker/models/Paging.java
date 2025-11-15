package com.github.tipaneque.myanimetracker.models;

import com.google.gson.annotations.SerializedName;

public class Paging {
    @SerializedName("next")
    private String next;

    // Getters and Setters
    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }
}
