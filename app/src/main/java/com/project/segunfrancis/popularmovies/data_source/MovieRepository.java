package com.project.segunfrancis.popularmovies.data_source;

import android.app.Application;

import com.project.segunfrancis.popularmovies.MovieExecutors;
import com.project.segunfrancis.popularmovies.data_source.local.MovieDao;
import com.project.segunfrancis.popularmovies.data_source.local.MovieRoomDatabase;
import com.project.segunfrancis.popularmovies.data_source.remote.ApiService;
import com.project.segunfrancis.popularmovies.data_source.remote.RetrofitClient;
import com.project.segunfrancis.popularmovies.model.Movie;
import com.project.segunfrancis.popularmovies.model.MoviesResponse;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;

/**
 * Created by SegunFrancis
 */
public class MovieRepository {
    private MovieDao mMovieDao;
    private ApiService mService;

    public MovieRepository(Application application) {
        MovieRoomDatabase database = MovieRoomDatabase.getDatabase(application);
        mMovieDao = database.mMovieDao();
        mService = RetrofitClient.getClient().create(ApiService.class);
    }

    public LiveData<List<Movie>> getFavoriteMovies() {
        return mMovieDao.getFavoriteMovies();
    }

    public void insertFavoriteMovie(Movie movie) {
        MovieExecutors.getInstance().diskIO().execute(() -> mMovieDao.insertFavoriteMovie(movie));
    }

    public void deleteFavoriteMovie(Movie movie) {
        MovieExecutors.getInstance().diskIO().execute(() -> mMovieDao.deleteFavoriteMovie(movie));
    }

    public Call<MoviesResponse> loadPopularMovies(String apiKey) {
        return mService.getPopularMovies(apiKey);
    }

    public Call<MoviesResponse> loadTopRatedMovies(String apiKey) {
        return mService.getTopRatedMovies(apiKey);
    }
}
