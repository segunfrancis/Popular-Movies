package com.project.segunfrancis.popularmovies.util;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

/**
 * Created by SegunFrancis
 */
public class AppConstants {
    public final static String BASE_URL = "https://api.themoviedb.org/3/";
    public final static String POSTER_BASE_URL = "https://image.tmdb.org/t/p/w342";
    public final static String BACKDROP_BASE_URL = "https://image.tmdb.org/t/p/w780";
    public final static String INTENT_KEY = "main_activity_to_second_activity_intent_key";
    public final static String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";
    public final static int NUMBER_OF_THREADS = 4;

    /**
     * [LiveData] observer that observes once and removes observer
     *
     * @param liveData is observed
     * @param observer is an observer object
     */
    public static <T> void observeOnce(final LiveData<T> liveData, final Observer<T> observer) {
        liveData.observeForever(new Observer<T>() {
            @Override
            public void onChanged(T t) {
                liveData.removeObserver(this);
                observer.onChanged(t);
            }
        });
    }
}
