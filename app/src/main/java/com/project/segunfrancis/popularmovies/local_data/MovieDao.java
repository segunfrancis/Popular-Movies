package com.project.segunfrancis.popularmovies.local_data;

import com.project.segunfrancis.popularmovies.model.Movie;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

/**
 * Created by SegunFrancis
 */
@Dao
public interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavoriteMovie(Movie movie);

    @Query("SELECT * from movie_table")
    LiveData<List<Movie>> getFavoriteMovies();

    @Query("DELETE from movie_table where id is :movieId")
    void deleteFavoriteMovie(int movieId);

    @Query("SELECT * from movie_table where id is :movieId")
    LiveData<Movie> checkFavoriteMovie(int movieId);
}
