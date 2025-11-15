package com.github.tipaneque.myanimetracker.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AnimeSearchResponse {
    @SerializedName("data")
    private List<AnimeNode> data;

    @SerializedName("paging")
    private Paging paging;

    // Getters and Setters
    public List<AnimeNode> getData() {
        return data;
    }

    public void setData(List<AnimeNode> data) {
        this.data = data;
    }

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }
}
