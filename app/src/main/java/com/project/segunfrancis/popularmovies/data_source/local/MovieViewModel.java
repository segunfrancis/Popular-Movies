package com.project.segunfrancis.popularmovies.data_source.local;

import android.app.Application;

import com.project.segunfrancis.popularmovies.R;
import com.project.segunfrancis.popularmovies.data_source.MovieRepository;
import com.project.segunfrancis.popularmovies.model.Movie;
import com.project.segunfrancis.popularmovies.model.MoviesResponse;
import com.project.segunfrancis.popularmovies.util.LoadState;
import com.project.segunfrancis.popularmovies.util.SingleLiveEvent;
import com.project.segunfrancis.popularmovies.util.State;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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
    public SingleLiveEvent<String> message = new SingleLiveEvent<>();

    public MutableLiveData<LoadState> loadState = new MutableLiveData<>(LoadState.HAS_NOT_LOADED);

    public MovieViewModel(@NonNull Application application) {
        super(application);
        mRepository = new MovieRepository(application);
    }

    public void insertFavoriteMovie(Movie movie) {
        message.setValue(getApplication().getResources().getString(R.string.added_to_favorite));
        mRepository.insertFavoriteMovie(movie);
    }

    public void deleteFavoriteMovie(Movie movie) {
        message.setValue(getApplication().getResources().getString(R.string.removed_from_favorite));
        mRepository.deleteFavoriteMovie(movie);
    }

    public void loadPopularMovies() {
        mStateMutableLiveData.setValue(State.LOADING);
        mRepository.loadPopularMovies(API_KEY).enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(@NonNull Call<MoviesResponse> call, @NonNull Response<MoviesResponse> response) {
                movieList.setValue(response.body().getResults());
                mStateMutableLiveData.setValue(State.SUCCESS);
                message.setValue(getApplication().getResources().getString(R.string.popular_message));
                loadState.setValue(LoadState.HAS_LOADED);
            }

            @Override
            public void onFailure(@NonNull Call<MoviesResponse> call, @NonNull Throwable t) {
                mStateMutableLiveData.setValue(State.ERROR);
                message.setValue(t.getLocalizedMessage());
                loadState.setValue(LoadState.HAS_NOT_LOADED);
            }
        });
    }

    public void loadTopRatedMovies() {
        mStateMutableLiveData.setValue(State.LOADING);
        mRepository.loadTopRatedMovies(API_KEY).enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(@NonNull Call<MoviesResponse> call, @NonNull Response<MoviesResponse> response) {
                movieList.setValue(response.body().getResults());
                mStateMutableLiveData.setValue(State.SUCCESS);
                message.setValue(getApplication().getResources().getString(R.string.top_rated_message));
                loadState.setValue(LoadState.HAS_LOADED);
            }

            @Override
            public void onFailure(@NonNull Call<MoviesResponse> call, @NonNull Throwable t) {
                mStateMutableLiveData.setValue(State.ERROR);
                message.setValue(t.getLocalizedMessage());
                loadState.setValue(LoadState.HAS_NOT_LOADED);
            }
        });
    }

    public void loadFavoriteMovies() {
        favMovieList = mRepository.getFavoriteMovies();
        message.setValue(getApplication().getResources().getString(R.string.favorite_message));
        mStateMutableLiveData.setValue(State.SUCCESS);
        loadState.setValue(LoadState.HAS_LOADED_FAVORITES);
    }
}
