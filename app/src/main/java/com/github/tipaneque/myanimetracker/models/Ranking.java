package com.github.tipaneque.myanimetracker.models;

import com.google.gson.annotations.SerializedName;

public class Ranking {
    @SerializedName("rank")
    private int rank;

    // Getters and Setters
    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
