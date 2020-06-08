package com.project.segunfrancis.popularmovies.local_data;

import android.app.Application;

import com.project.segunfrancis.popularmovies.api.ApiService;
import com.project.segunfrancis.popularmovies.api.RetrofitClient;
import com.project.segunfrancis.popularmovies.model.Movie;
import com.project.segunfrancis.popularmovies.model.MoviesResponse;
import com.project.segunfrancis.popularmovies.util.State;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by SegunFrancis
 */
public class MovieViewModel extends AndroidViewModel {
    private MovieRepository mRepository;
    public MutableLiveData<List<Movie>> mMovieList;
    public MutableLiveData<State> mStateMutableLiveData;

    public MovieViewModel(@NonNull Application application) {
        super(application);
        mRepository = new MovieRepository(application);
    }

    public void insertFavoriteMovie(Movie movie) {
        mRepository.insertFavoriteMovie(movie);
    }

    public void deleteFavoriteMovie(int movieId) {
        mRepository.deleteFavoriteMovie(movieId);
    }

    public boolean checkFavoriteMovie(int movieId) {
        return mRepository.checkFavoriteMovie(movieId);
    }

    public void loadPopularMovies(String key) {
        mStateMutableLiveData.setValue(State.LOADING);
        mRepository.loadPopularMovies(key).enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(@NonNull Call<MoviesResponse> call, @NonNull Response<MoviesResponse> response) {
                mMovieList.setValue(response.body().getResults());
                mStateMutableLiveData.setValue(State.SUCCESS);
            }

            @Override
            public void onFailure(@NonNull Call<MoviesResponse> call, @NonNull Throwable t) {
                mStateMutableLiveData.setValue(State.ERROR);
            }
        });
    }

    public void loadTopRatedMovies(String key) {
        mStateMutableLiveData.setValue(State.LOADING);
        mRepository.loadTopRatedMovies(key).enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(@NonNull Call<MoviesResponse> call, @NonNull Response<MoviesResponse> response) {
                mMovieList.setValue(response.body().getResults());
                mStateMutableLiveData.setValue(State.SUCCESS);
            }

            @Override
            public void onFailure(@NonNull Call<MoviesResponse> call, @NonNull Throwable t) {
                mStateMutableLiveData.setValue(State.ERROR);
            }
        });
    }

    public void loadFavoriteMovies() {
        if (mRepository.getFavoriteMovies().getValue().isEmpty()) {
            mStateMutableLiveData.setValue(State.EMPTY);
        } else
            mMovieList = mRepository.getFavoriteMovies();
    }
}
