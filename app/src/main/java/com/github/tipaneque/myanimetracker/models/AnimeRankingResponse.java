package com.github.tipaneque.myanimetracker.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AnimeRankingResponse {
    @SerializedName("data")
    private List<RankingAnimeNode> data;

    @SerializedName("paging")
    private Paging paging;

    // Getters and Setters
    public List<RankingAnimeNode> getData() {
        return data;
    }

    public void setData(List<RankingAnimeNode> data) {
        this.data = data;
    }

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }
}