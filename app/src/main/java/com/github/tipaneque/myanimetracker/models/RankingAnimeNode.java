package com.github.tipaneque.myanimetracker.models;

import com.google.gson.annotations.SerializedName;

public class RankingAnimeNode {
    @SerializedName("node")
    private Anime node;

    @SerializedName("ranking")
    private Ranking ranking;

    // Getters and Setters
    public Anime getNode() {
        return node;
    }

    public void setNode(Anime node) {
        this.node = node;
    }

    public Ranking getRanking() {
        return ranking;
    }

    public void setRanking(Ranking ranking) {
        this.ranking = ranking;
    }
}
