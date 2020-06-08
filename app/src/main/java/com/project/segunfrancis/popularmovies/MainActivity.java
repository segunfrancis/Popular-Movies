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

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MoviePosterAdapter.OnItemClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private ProgressBar mProgressBar;
    private Group mNoInternetGroup, mEmptyFavoriteGroup;
    private SharedPreferences mPreferences;
    private MovieViewModel mViewModel;
    private MoviePosterAdapter mAdapter;
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
        TextView retryButton = findViewById(R.id.retry);
        GridLayoutManager layoutManager = new GridLayoutManager(this, calculateNoOfColumns(this));
        mProgressBar.setVisibility(View.VISIBLE);

        // Get preference value
        mRecyclerView.setLayoutManager(layoutManager);
        String[] prefValues = getResources().getStringArray(R.array.sort_order_values);
        String[] pref = getResources().getStringArray(R.array.sort_order);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String listPrefValue = mPreferences.getString(getResources().getString(R.string.list_pref_key), prefValues[0]);
        if (listPrefValue.equals(prefValues[0])) {
            observePopularMovies();
            displaySnackBar("You are viewing " + pref[Integer.parseInt(prefValues[0])]);
        } else if (listPrefValue.equals(prefValues[1])) {
            observeTopRatedMovies();
            displaySnackBar("You are viewing " + pref[Integer.parseInt(prefValues[1])]);
        } else {
            observeFavMovies();
            displaySnackBar("You are viewing " + pref[Integer.parseInt(prefValues[2])]);
        }

        mViewModel.mStateMutableLiveData.observe(this, new Observer<State>() {
            @Override
            public void onChanged(State state) {
                switch (state) {
                    case LOADING: {
                        mProgressBar.setVisibility(View.VISIBLE);
                        break;
                    }
                    case SUCCESS: {
                        //displaySnackBar("You are viewing " + pref[Integer.parseInt(listPrefValue)]);
                        break;
                    }
                    case EMPTY: {
                        mEmptyFavoriteGroup.setVisibility(View.VISIBLE);
                        break;
                    }
                    case ERROR: {
                        mNoInternetGroup.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }
        });

        mViewModel.message.observe(this, this::displaySnackBar);
        /*retryButton.setOnClickListener(v -> {
            if (isConnectionAvailable()) {
                loadMovies(listPrefValue);
            } else {
                mNoInternetGroup.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            }
        });*/
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
                mViewModel.loadPopularMovies();
                observePopularMovies();
                //displaySnackBar("You are viewing " + order[Integer.parseInt(values[0])]);
            } else if (prefValue.equals(values[1])) {
                mViewModel.loadTopRatedMovies();
                observeTopRatedMovies();
                //displaySnackBar("You are viewing " + order[Integer.parseInt(values[1])]);
            } else {
                mViewModel.loadFavoriteMovies();
                observeFavMovies();
                //displaySnackBar("You are viewing " + order[Integer.parseInt(values[2])]);
            }
        }
    }

   /* private void loadPopularMovies() {
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
                mMovieList = response.body().getResults();
                mAdapter = new MoviePosterAdapter(mMovieList, MainActivity.this);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setLayoutManager(mLayoutManager);
                hideDisplays();
            }

            @Override
            public void onFailure(@NonNull Call<MoviesResponse> call, @NonNull Throwable t) {
                displaySnackBar(t.getLocalizedMessage());
                mProgressBar.setVisibility(View.GONE);
                if (mMovieList.isEmpty())
                    mNoInternetGroup.setVisibility(View.VISIBLE);
            }
        });
    }*/

   /* private void loadTopRatedMovies() {
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
                mMovieList = response.body().getResults();
                mAdapter = new MoviePosterAdapter(mMovieList, MainActivity.this);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setLayoutManager(mLayoutManager);
                hideDisplays();
            }

            @Override
            public void onFailure(@NonNull Call<MoviesResponse> call, @NonNull Throwable t) {
                displaySnackBar(t.getLocalizedMessage());
                mProgressBar.setVisibility(View.GONE);
                if (mMovieList.isEmpty())
                    mNoInternetGroup.setVisibility(View.VISIBLE);
            }
        });
    }*/

    /*private void displayFavoriteMovies() {
        mViewModel.getFavoriteMovies().observe(this, movieList -> {
            MoviePosterAdapter adapter = new MoviePosterAdapter(movieList, MainActivity.this);
            if (movieList.isEmpty()) {
                hideDisplays();
                mEmptyFavoriteGroup.setVisibility(View.VISIBLE);
            } else {
                mEmptyFavoriteGroup.setVisibility(View.GONE);
                hideDisplays();
            }
            mRecyclerView.setAdapter(adapter);
            mRecyclerView.setLayoutManager(mLayoutManager);
        });
    }*/

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
            mViewModel.loadPopularMovies();
        } else if (prefValue.equals(values[1])) {
            mViewModel.loadTopRatedMovies();
        } else {
            mViewModel.loadFavoriteMovies();
        }
    }

    private void observePopularMovies() {
        mViewModel.popularMovieList.observe(MainActivity.this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                mAdapter = new MoviePosterAdapter(movies, MainActivity.this);
                mRecyclerView.setAdapter(mAdapter);
            }
        });
    }

    private void observeTopRatedMovies() {
        mViewModel.topRatedMovieList.observe(MainActivity.this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                mAdapter = new MoviePosterAdapter(movies, MainActivity.this);
                mRecyclerView.setAdapter(mAdapter);
            }
        });
    }

    private void observeFavMovies() {
        mViewModel.loadFavoriteMovies().observe(MainActivity.this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                mAdapter = new MoviePosterAdapter(movies, MainActivity.this);
                mRecyclerView.setAdapter(mAdapter);
            }
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
