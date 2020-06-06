
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

    public void setPage(int page) {
        mPage = page;
    }

    public List<Movie> getResults() {
        return mResults;
    }

    public void setResults(List<Movie> results) {
        mResults = results;
    }

    public int getTotalPages() {
        return mTotalPages;
    }

    public void setTotalPages(int totalPages) {
        mTotalPages = totalPages;
    }

    public int getTotalResults() {
        return mTotalResults;
    }

    public void setTotalResults(int totalResults) {
        mTotalResults = totalResults;
    }

}
