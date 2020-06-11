package com.project.segunfrancis.popularmovies.data_source.local;

import android.content.Context;

import com.project.segunfrancis.popularmovies.model.Movie;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Created by SegunFrancis
 */
@Database(entities = {Movie.class}, exportSchema = false, version = 1)
public abstract class MovieRoomDatabase extends RoomDatabase {
    public abstract MovieDao mMovieDao();

    private static final Object LOCK = new Object();
    private static volatile MovieRoomDatabase INSTANCE;

    public static MovieRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (LOCK) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            MovieRoomDatabase.class,
                            "movie_database"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}
