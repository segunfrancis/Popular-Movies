package com.project.segunfrancis.popularmovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.project.segunfrancis.popularmovies.adapter.MoviePosterAdapter;
import com.project.segunfrancis.popularmovies.api.ApiService;
import com.project.segunfrancis.popularmovies.api.RetrofitClient;
import com.project.segunfrancis.popularmovies.model.MoviesResponse;
import com.project.segunfrancis.popularmovies.model.Movie;

import static com.project.segunfrancis.popularmovies.util.ApiKey.API_KEY;
import static com.project.segunfrancis.popularmovies.util.AppConstants.INTENT_KEY;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MoviePosterAdapter.OnItemClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private List<Movie> moviesList;
    private MoviePosterAdapter mAdapter;
    private GridLayoutManager mLayoutManager;
    private Group mNoInternetGroup;
    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = findViewById(R.id.progressBar);
        mRecyclerView = findViewById(R.id.movies_listRecyclerVIew);
        mNoInternetGroup = findViewById(R.id.no_internet_group);
        TextView retryButton = findViewById(R.id.retry);
        mLayoutManager = new GridLayoutManager(this, calculateNoOfColumns(this));
        moviesList = new ArrayList<>();
        mProgressBar.setVisibility(View.VISIBLE);

        // Get preference value
        String[] prefValues = getResources().getStringArray(R.array.sort_order_values);
        String[] pref = getResources().getStringArray(R.array.sort_order);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String listPrefValue = mPreferences.getString(getResources().getString(R.string.list_pref_key), prefValues[0]);
        displaySnackBar("You are viewing " + pref[Integer.parseInt(listPrefValue)]);

        retryButton.setOnClickListener(v -> {
            if (isConnectionAvailable()) {
                loadMovies(listPrefValue);
            } else {
                mNoInternetGroup.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            }
        });
        if (isConnectionAvailable()) {
            loadMovies(listPrefValue);
        } else {
            mNoInternetGroup.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
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
        String[] values = getResources().getStringArray(R.array.sort_order_values);
        String[] order = getResources().getStringArray(R.array.sort_order);
        if (key.equals(getResources().getString(R.string.list_pref_key))) {
            String prefValue = sharedPreferences.getString(key, values[0]);
            if (prefValue.equals(values[0])) {
                loadPopularMovies();
                displaySnackBar("You are viewing " + order[Integer.parseInt(values[0])]);
            } else {
                loadTopRatedMovies();
                displaySnackBar("You are viewing " + order[Integer.parseInt(values[1])]);
            }
        }
    }

    private void loadPopularMovies() {
        if (API_KEY.isEmpty()) {
            Toast.makeText(this, "Obtain your api key", Toast.LENGTH_LONG).show();
            hideDisplays();
            return;
        }
        ApiService service = RetrofitClient.getClient().create(ApiService.class);
        Call<MoviesResponse> call = service.getPopularMovies(API_KEY);
        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(@NonNull Call<MoviesResponse> call, @NonNull Response<MoviesResponse> response) {
                moviesList = response.body().getResults();
                mAdapter = new MoviePosterAdapter(moviesList, MainActivity.this);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setLayoutManager(mLayoutManager);
                hideDisplays();
            }

            @Override
            public void onFailure(@NonNull Call<MoviesResponse> call, @NonNull Throwable t) {
                displaySnackBar(t.getLocalizedMessage());
                mProgressBar.setVisibility(View.GONE);
                if (moviesList.isEmpty())
                    mNoInternetGroup.setVisibility(View.VISIBLE);
            }
        });
    }

    private void loadTopRatedMovies() {
        if (API_KEY.isEmpty()) {
            Toast.makeText(this, "Obtain your api key", Toast.LENGTH_LONG).show();
            hideDisplays();
            return;
        }
        ApiService service = RetrofitClient.getClient().create(ApiService.class);
        Call<MoviesResponse> call = service.getTopRatedMovies(API_KEY);
        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(@NonNull Call<MoviesResponse> call, @NonNull Response<MoviesResponse> response) {
                moviesList = response.body().getResults();
                mAdapter = new MoviePosterAdapter(moviesList, MainActivity.this);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setLayoutManager(mLayoutManager);
                hideDisplays();
            }

            @Override
            public void onFailure(@NonNull Call<MoviesResponse> call, @NonNull Throwable t) {
                displaySnackBar(t.getLocalizedMessage());
                mProgressBar.setVisibility(View.GONE);
                if (moviesList.isEmpty())
                    mNoInternetGroup.setVisibility(View.VISIBLE);
            }
        });
    }

    private void displaySnackBar(String message) {
        Snackbar.make(findViewById(R.id.movies_list_constraintLayout), message, Snackbar.LENGTH_LONG).show();
    }

    private void hideDisplays() {
        mNoInternetGroup.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
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

    private void loadMovies(String prefValue) {
        String[] values = getResources().getStringArray(R.array.sort_order_values);
        if (prefValue.equals(values[0])) {
            // Popular movies
            loadPopularMovies();
        } else {
            loadTopRatedMovies();
        }
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
