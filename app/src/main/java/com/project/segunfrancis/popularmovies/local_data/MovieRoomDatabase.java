package com.project.segunfrancis.popularmovies.local_data;

import android.content.Context;

import com.project.segunfrancis.popularmovies.model.Movie;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import static com.project.segunfrancis.popularmovies.util.AppConstants.NUMBER_OF_THREADS;

/**
 * Created by SegunFrancis
 */
@Database(entities = {Movie.class}, exportSchema = false, version = 1)
public abstract class MovieRoomDatabase extends RoomDatabase {
    public abstract MovieDao mMovieDao();

    private static volatile MovieRoomDatabase INSTANCE;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static MovieRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MovieRoomDatabase.class) {
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
