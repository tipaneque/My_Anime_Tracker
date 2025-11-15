package com.github.tipaneque.myanimetracker.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserAnimeListResponse {
    @SerializedName("data")
    private List<UserAnimeListItem> data;

    @SerializedName("paging")
    private Paging paging;

    // Getters and Setters
    public List<UserAnimeListItem> getData() {
        return data;
    }

    public void setData(List<UserAnimeListItem> data) {
        this.data = data;
    }

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }
}
