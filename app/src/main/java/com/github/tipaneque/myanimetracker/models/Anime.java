package com.github.tipaneque.myanimetracker.models;



import com.google.gson.annotations.SerializedName;

public class Anime {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("main_picture")
    private MainPicture mainPicture;

    @SerializedName("mean")
    private Float mean;

    @SerializedName("media_type")
    private String mediaType;

    @SerializedName("episodes")
    private Integer episodes;

    @SerializedName("status")
    private String status;

    @SerializedName("synopsis")
    private String synopsis;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public MainPicture getMain_picture() { return mainPicture; }
    public void setMain_picture(MainPicture mainPicture) { this.mainPicture = mainPicture; }

    public Float getMean() { return mean; }
    public void setMean(Float mean) { this.mean = mean; }

    public String getMedia_type() { return mediaType; }
    public void setMedia_type(String mediaType) { this.mediaType = mediaType; }

    public Integer getEpisodes() { return episodes; }
    public void setEpisodes(Integer episodes) { this.episodes = episodes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSynopsis() { return synopsis; }
    public void setSynopsis(String synopsis) { this.synopsis = synopsis; }
}

