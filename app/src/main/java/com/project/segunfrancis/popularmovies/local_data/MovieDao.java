package com.project.segunfrancis.popularmovies.local_data;

import com.project.segunfrancis.popularmovies.model.Movie;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
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

    @Delete
    void deleteFavoriteMovie(Movie movie);
}
