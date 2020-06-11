package com.project.segunfrancis.popularmovies;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;

import static com.project.segunfrancis.popularmovies.util.AppConstants.NUMBER_OF_THREADS;

/**
 * Created by SegunFrancis
 */
public class MovieExecutors {

    // For singleton instantiation
    private final static Object LOCK = new Object();
    private static MovieExecutors sInstance;
    private final ExecutorService diskIO;
    private final Executor mainThread;
    private final ExecutorService networkIO;

    private MovieExecutors(ExecutorService diskIO, ExecutorService networkIO, Executor mainThread) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainThread = mainThread;
    }

    public static MovieExecutors getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new MovieExecutors(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(NUMBER_OF_THREADS), new MainThreadExecutor());
            }
        }
        return sInstance;
    }

    public Executor diskIO() {
        return diskIO;
    }

    public Executor mainThread() {
        return mainThread;
    }

    public Executor networkIO() {
        return networkIO;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable runnable) {
            mainThreadHandler.post(runnable);
        }
    }
}
