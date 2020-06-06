package com.project.segunfrancis.popularmovies.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class MoviesResponse {

    @SerializedName("page")
    private int mPage;
    @SerializedName("results")
    private List<Movie> mResults;
    @SerializedName("total_pages")
    private int mTotalPages;
    @SerializedName("total_results")
    private int mTotalResults;

    public int getPage() {
        return mPage;
    }

    public List<Movie> getResults() {
        return mResults;
    }

    public int getTotalPages() {
        return mTotalPages;
    }

    public int getTotalResults() {
        return mTotalResults;
    }
}
