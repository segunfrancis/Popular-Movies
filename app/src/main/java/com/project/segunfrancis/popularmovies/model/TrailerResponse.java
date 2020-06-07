package com.project.segunfrancis.popularmovies.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class TrailerResponse {

    @SerializedName("id")
    private Long mId;
    @SerializedName("results")
    private List<TrailerResult> mTrailerResults;

    public Long getId() {
        return mId;
    }

    public List<TrailerResult> getTrailerResults() {
        return mTrailerResults;
    }
}
