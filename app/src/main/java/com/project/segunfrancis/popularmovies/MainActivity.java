package com.project.segunfrancis.popularmovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.project.segunfrancis.popularmovies.adapter.MoviePosterAdapter;
import com.project.segunfrancis.popularmovies.local_data.MovieViewModel;
import com.project.segunfrancis.popularmovies.model.Movie;
import com.project.segunfrancis.popularmovies.util.State;

import static com.project.segunfrancis.popularmovies.util.AppConstants.INTENT_KEY;
import static com.project.segunfrancis.popularmovies.util.AppConstants.observeOnce;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MoviePosterAdapter.OnItemClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private ProgressBar mProgressBar;
    private Group mNoInternetGroup, mEmptyFavoriteGroup;
    private SharedPreferences mPreferences;
    private MovieViewModel mViewModel;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        mProgressBar = findViewById(R.id.progressBar);
        mRecyclerView = findViewById(R.id.movies_listRecyclerVIew);
        mNoInternetGroup = findViewById(R.id.no_internet_group);
        mEmptyFavoriteGroup = findViewById(R.id.empty_favorite_list_group);
        TextView retryText = findViewById(R.id.retry);

        GridLayoutManager layoutManager = new GridLayoutManager(this, calculateNoOfColumns(this));
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setLayoutManager(layoutManager);

        loadMovies();

        retryText.setOnClickListener(v -> loadMovies());

        mViewModel.mStateMutableLiveData.observe(this, state -> {
            switch (state) {
                case LOADING: {
                    mEmptyFavoriteGroup.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    mNoInternetGroup.setVisibility(View.GONE);
                    break;
                }
                case SUCCESS: {
                    mEmptyFavoriteGroup.setVisibility(View.GONE);
                    mNoInternetGroup.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);
                    break;
                }
                case EMPTY: {
                    mEmptyFavoriteGroup.setVisibility(View.VISIBLE);
                    mNoInternetGroup.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);
                    break;
                }
                case ERROR: {
                    mEmptyFavoriteGroup.setVisibility(View.GONE);
                    mNoInternetGroup.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                    break;
                }
                default: {
                    mEmptyFavoriteGroup.setVisibility(View.GONE);
                    mNoInternetGroup.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);
                    break;
                }
            }
        });

        mViewModel.message.observe(this, this::displaySnackBar);
        mPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onItemClick(Movie result, int position) {
        startActivity(
                new Intent(MainActivity.this, MovieDetailsActivity.class)
                        .putExtra(INTENT_KEY, result)
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        loadMovies();
    }

    private void displaySnackBar(String message) {
        Snackbar.make(findViewById(R.id.movies_list_constraintLayout), message, Snackbar.LENGTH_LONG).show();
    }

    private boolean isConnectionAvailable() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process process = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = process.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void loadMovies() {
        String[] prefValues = getResources().getStringArray(R.array.sort_order_values);
        String[] pref = getResources().getStringArray(R.array.sort_order);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String listPrefValue = mPreferences.getString(getResources().getString(R.string.list_pref_key), prefValues[0]);
        if (listPrefValue.equals(prefValues[0])) {
            if (isConnectionAvailable()) {
                mViewModel.loadPopularMovies();
                observeMoviesList();
            } else {
                mNoInternetGroup.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            }
        } else if (listPrefValue.equals(prefValues[1])) {
            if (isConnectionAvailable()) {
                mViewModel.loadTopRatedMovies();
                observeMoviesList();
            } else {
                mNoInternetGroup.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            }
        } else {
            mViewModel.loadFavoriteMovies();
            observeFavList();
        }
    }

    private void observeMoviesList() {
        mViewModel.movieList.observe(MainActivity.this, movies -> {
            MoviePosterAdapter adapter = new MoviePosterAdapter(movies, MainActivity.this);
            mRecyclerView.setAdapter(adapter);
        });
    }

    private void observeFavList() {
        observeOnce(mViewModel.favMovieList, movies -> {
            MoviePosterAdapter adapter = new MoviePosterAdapter(movies, MainActivity.this);
            mRecyclerView.setAdapter(adapter);
        });
    }

    public int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 200;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        if (noOfColumns < 2)
            noOfColumns = 2;
        return noOfColumns;
    }
}
