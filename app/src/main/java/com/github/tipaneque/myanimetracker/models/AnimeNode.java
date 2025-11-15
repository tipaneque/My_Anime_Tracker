package com.github.tipaneque.myanimetracker.models;

import com.google.gson.annotations.SerializedName;

public class AnimeNode {
    @SerializedName("node")
    private Anime node;

    public Anime getNode() {
        return node;
    }

    public void setNode(Anime node) {
        this.node = node;
    }
}
