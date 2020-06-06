package com.project.segunfrancis.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Movie implements Parcelable {

    @SerializedName("adult")
    private Boolean mAdult;
    @SerializedName("backdrop_path")
    private String mBackdropPath;
    @SerializedName("genre_ids")
    private List<Integer> mGenreIds;
    @SerializedName("id")
    private int mId;
    @SerializedName("original_language")
    private String mOriginalLanguage;
    @SerializedName("original_title")
    private String mOriginalTitle;
    @SerializedName("overview")
    private String mOverview;
    @SerializedName("popularity")
    private double mPopularity;
    @SerializedName("poster_path")
    private String mPosterPath;
    @SerializedName("release_date")
    private String mReleaseDate;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("video")
    private Boolean mVideo;
    @SerializedName("vote_average")
    private Double mVoteAverage;
    @SerializedName("vote_count")
    private int mVoteCount;

    protected Movie(Parcel in) {
        byte tmpMAdult = in.readByte();
        mAdult = tmpMAdult == 0 ? null : tmpMAdult == 1;
        mBackdropPath = in.readString();
        mId = in.readInt();
        mOriginalLanguage = in.readString();
        mOriginalTitle = in.readString();
        mOverview = in.readString();
        mPopularity = in.readDouble();
        mPosterPath = in.readString();
        mReleaseDate = in.readString();
        mTitle = in.readString();
        byte tmpMVideo = in.readByte();
        mVideo = tmpMVideo == 0 ? null : tmpMVideo == 1;
        if (in.readByte() == 0) {
            mVoteAverage = null;
        } else {
            mVoteAverage = in.readDouble();
        }
        mVoteCount = in.readInt();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public Boolean getAdult() {
        return mAdult;
    }

    public String getBackdropPath() {
        return mBackdropPath;
    }

    public List<Integer> getGenreIds() {
        return mGenreIds;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getOriginalLanguage() {
        return mOriginalLanguage;
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        mOriginalTitle = originalTitle;
    }

    public String getOverview() {
        return mOverview;
    }

    public Double getPopularity() {
        return mPopularity;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getTitle() {
        return mTitle;
    }

    public Boolean getVideo() {
        return mVideo;
    }

    public double getVoteAverage() {
        return mVoteAverage;
    }

    public int getVoteCount() {
        return mVoteCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (mAdult == null ? 0 : mAdult ? 1 : 2));
        parcel.writeString(mBackdropPath);
        parcel.writeInt(mId);
        parcel.writeString(mOriginalLanguage);
        parcel.writeString(mOriginalTitle);
        parcel.writeString(mOverview);
        parcel.writeDouble(mPopularity);
        parcel.writeString(mPosterPath);
        parcel.writeString(mReleaseDate);
        parcel.writeString(mTitle);
        parcel.writeByte((byte) (mVideo == null ? 0 : mVideo ? 1 : 2));
        if (mVoteAverage == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(mVoteAverage);
        }
        parcel.writeInt(mVoteCount);
    }
}
