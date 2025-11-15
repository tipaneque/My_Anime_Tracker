package com.github.tipaneque.myanimetracker.models;

import com.google.gson.annotations.SerializedName;

public class UpdateAnimeStatusResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("score")
    private int score;

    @SerializedName("num_episodes_watched")
    private int numEpisodesWatched;

    @SerializedName("is_rewatching")
    private boolean isRewatching;

    @SerializedName("updated_at")
    private String updatedAt;

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getNumEpisodesWatched() {
        return numEpisodesWatched;
    }

    public void setNumEpisodesWatched(int numEpisodesWatched) {
        this.numEpisodesWatched = numEpisodesWatched;
    }

    public boolean isRewatching() {
        return isRewatching;
    }

    public void setRewatching(boolean rewatching) {
        isRewatching = rewatching;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}