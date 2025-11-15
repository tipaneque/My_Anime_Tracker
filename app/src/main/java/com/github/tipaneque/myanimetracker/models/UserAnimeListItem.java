package com.github.tipaneque.myanimetracker.models;

import com.google.gson.annotations.SerializedName;

public class UserAnimeListItem {
    @SerializedName("node")
    private Anime node;

    @SerializedName("list_status")
    private ListStatus listStatus;

    // Getters and Setters
    public Anime getNode() {
        return node;
    }

    public void setNode(Anime node) {
        this.node = node;
    }

    public ListStatus getListStatus() {
        return listStatus;
    }

    public void setListStatus(ListStatus listStatus) {
        this.listStatus = listStatus;
    }
}
