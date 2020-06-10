package com.project.segunfrancis.popularmovies.local_data;

import android.app.Application;
import android.util.Log;

import com.project.segunfrancis.popularmovies.model.Movie;
import com.project.segunfrancis.popularmovies.model.MoviesResponse;
import com.project.segunfrancis.popularmovies.util.State;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.project.segunfrancis.popularmovies.util.ApiKey.API_KEY;

/**
 * Created by SegunFrancis
 */
public class MovieViewModel extends AndroidViewModel {
    private MovieRepository mRepository;
    public MutableLiveData<List<Movie>> movieList = new MutableLiveData<>();
    public LiveData<List<Movie>> favMovieList = new MutableLiveData<>();

    public MutableLiveData<State> mStateMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<String> message = new MutableLiveData<>();

    public MovieViewModel(@NonNull Application application) {
        super(application);
        mRepository = new MovieRepository(application);
    }

    public void insertFavoriteMovie(Movie movie) {
        mRepository.insertFavoriteMovie(movie);
    }

    public void deleteFavoriteMovie(Movie movie) {
        mRepository.deleteFavoriteMovie(movie);
    }

    public void loadPopularMovies() {
        if (API_KEY.isEmpty()) {
            mStateMutableLiveData.setValue(State.ERROR);
            message.setValue("Obtain your API key");
            return;
        }
        mStateMutableLiveData.setValue(State.LOADING);
        mRepository.loadPopularMovies(API_KEY).enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(@NonNull Call<MoviesResponse> call, @NonNull Response<MoviesResponse> response) {
                movieList.setValue(response.body().getResults());
                mStateMutableLiveData.setValue(State.SUCCESS);
                message.setValue("You are viewing Popular Movies");
            }

            @Override
            public void onFailure(@NonNull Call<MoviesResponse> call, @NonNull Throwable t) {
                mStateMutableLiveData.setValue(State.ERROR);
                message.setValue(t.getLocalizedMessage());
            }
        });
    }

    public void loadTopRatedMovies() {
        if (API_KEY.isEmpty()) {
            mStateMutableLiveData.setValue(State.ERROR);
            message.setValue("Obtain your API key");
            return;
        }
        mStateMutableLiveData.setValue(State.LOADING);
        mRepository.loadTopRatedMovies(API_KEY).enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(@NonNull Call<MoviesResponse> call, @NonNull Response<MoviesResponse> response) {
                movieList.setValue(response.body().getResults());
                mStateMutableLiveData.setValue(State.SUCCESS);
                message.setValue("You are viewing Top Rated Movies");
            }

            @Override
            public void onFailure(@NonNull Call<MoviesResponse> call, @NonNull Throwable t) {
                mStateMutableLiveData.setValue(State.ERROR);
                message.setValue(t.getLocalizedMessage());
            }
        });
    }

    public void loadFavoriteMovies() {
        favMovieList = mRepository.getFavoriteMovies();
        message.setValue("You are viewing your Favorite Movies");
    }
}
