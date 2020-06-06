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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import static com.project.segunfrancis.popularmovies.util.AppConstants.KEY_PREF_LIST;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MoviePosterAdapter.OnItemClickListener {

    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private List<Movie> moviesList;
    private MoviePosterAdapter mAdapter;
    private GridLayoutManager mLayoutManager;
    private Group mNoInternetGroup;
    private String mListPrefValue;
    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = findViewById(R.id.progressBar);
        mRecyclerView = findViewById(R.id.movies_listRecyclerVIew);
        mNoInternetGroup = findViewById(R.id.no_internet_group);
        TextView retryButton = findViewById(R.id.retry);
        mLayoutManager = new GridLayoutManager(this, 2);
        mProgressBar.setVisibility(View.VISIBLE);

        // Get preference value
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mListPrefValue = mPreferences.getString(KEY_PREF_LIST, "");
        displaySnackBar("You are viewing " + mListPrefValue);

        retryButton.setOnClickListener(v -> {
            if (isConnectionAvailable()) {
                loadMovies();
            } else {
                mNoInternetGroup.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            }
        });
        if (isConnectionAvailable()) {
            loadMovies();
        } else {
            mNoInternetGroup.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String listPrefValue = mPreferences.getString(KEY_PREF_LIST, "");
        if (!mListPrefValue.equals(listPrefValue)) {
            // Preferences has changed, reload data
            displaySnackBar("You are viewing " + listPrefValue);
            mListPrefValue = mPreferences.getString(KEY_PREF_LIST, "");
            loadMovies();
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
                moviesList = new ArrayList<>();
                moviesList = response.body().getResults();
                mAdapter = new MoviePosterAdapter(moviesList, MainActivity.this);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setLayoutManager(mLayoutManager);
                hideDisplays();
            }

            @Override
            public void onFailure(@NonNull Call<MoviesResponse> call, @NonNull Throwable t) {
                displaySnackBar(t.getLocalizedMessage());
                hideDisplays();
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
                moviesList = new ArrayList<>();
                moviesList = response.body().getResults();
                mAdapter = new MoviePosterAdapter(moviesList, MainActivity.this);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setLayoutManager(mLayoutManager);
                hideDisplays();
            }

            @Override
            public void onFailure(@NonNull Call<MoviesResponse> call, @NonNull Throwable t) {
                displaySnackBar(t.getLocalizedMessage());
                hideDisplays();
            }
        });
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

    private void loadMovies() {
        String[] values = getResources().getStringArray(R.array.sort_order);
        if (mListPrefValue.equals(values[0])) {
            // Popular movies
            loadPopularMovies();
        } else {
            loadTopRatedMovies();
        }
    }
}
