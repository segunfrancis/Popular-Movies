package com.project.segunfrancis.popularmovies.local_data;

import android.app.Application;

import com.project.segunfrancis.popularmovies.api.ApiService;
import com.project.segunfrancis.popularmovies.api.RetrofitClient;
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
    private MutableLiveData<List<Movie>> mMovieList;
    private ApiService mService;

    MovieRepository(Application application) {
        MovieRoomDatabase database = MovieRoomDatabase.getDatabase(application);
        mMovieDao = database.mMovieDao();
        mMovieList = mMovieDao.getFavoriteMovies();
        mService = RetrofitClient.getClient().create(ApiService.class);
    }

    MutableLiveData<List<Movie>> getFavoriteMovies() {
        return mMovieList;
    }

    void insertFavoriteMovie(Movie movie) {
        MovieRoomDatabase.databaseWriteExecutor.execute(() -> mMovieDao.insertFavoriteMovie(movie));
    }

    void deleteFavoriteMovie(int movieId) {
        mMovieDao.deleteFavoriteMovie(movieId);
    }

    boolean checkFavoriteMovie(int movieId) {
        LiveData<Movie> singleMovie = mMovieDao.checkFavoriteMovie(movieId);
        return singleMovie != null;
    }

    Call<MoviesResponse> loadPopularMovies(String apiKey) {
        return mService.getPopularMovies(apiKey);
    }

    Call<MoviesResponse> loadTopRatedMovies(String apiKey) {
        return mService.getTopRatedMovies(apiKey);
    }
}
