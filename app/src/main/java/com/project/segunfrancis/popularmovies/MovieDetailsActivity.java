package com.project.segunfrancis.popularmovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.project.segunfrancis.popularmovies.adapter.MarginItemDecoration;
import com.project.segunfrancis.popularmovies.adapter.MovieReviewAdapter;
import com.project.segunfrancis.popularmovies.adapter.TrailerRecyclerAdapter;
import com.project.segunfrancis.popularmovies.api.ApiService;
import com.project.segunfrancis.popularmovies.api.RetrofitClient;
import com.project.segunfrancis.popularmovies.local_data.MovieViewModel;
import com.project.segunfrancis.popularmovies.model.Movie;
import com.project.segunfrancis.popularmovies.model.ReviewResponse;
import com.project.segunfrancis.popularmovies.model.ReviewResult;
import com.project.segunfrancis.popularmovies.model.TrailerResponse;
import com.project.segunfrancis.popularmovies.model.TrailerResult;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.project.segunfrancis.popularmovies.util.AppConstants.INTENT_KEY;
import static com.project.segunfrancis.popularmovies.util.AppConstants.BACKDROP_BASE_URL;
import static com.project.segunfrancis.popularmovies.util.ApiKey.API_KEY;
import static com.project.segunfrancis.popularmovies.util.AppConstants.YOUTUBE_BASE_URL;

public class MovieDetailsActivity extends AppCompatActivity implements TrailerRecyclerAdapter.OnItemClickListener,
        MovieReviewAdapter.OnItemClickListener {

    private RecyclerView mTrailerRecyclerView, mReviewRecyclerView;
    private ApiService mService;
    private ExtendedFloatingActionButton fab;
    private MovieViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        mViewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        ImageView movieBackDrop = findViewById(R.id.backdrop_imageView);
        TextView movieTitle = findViewById(R.id.title_textView);
        TextView moviePlot = findViewById(R.id.plot_synopsis_textView);
        TextView movieReleaseDate = findViewById(R.id.release_date_textView);
        TextView movieRating = findViewById(R.id.vote_average_textView);
        mTrailerRecyclerView = findViewById(R.id.trailers_recyclerView);
        mReviewRecyclerView = findViewById(R.id.reviews_recyclerView);
        fab = findViewById(R.id.extended_fab);

        Intent intent = getIntent();
        Movie movie = intent.getParcelableExtra(INTENT_KEY);

        getSupportActionBar().setTitle(movie.getTitle());

        String backdropPath = BACKDROP_BASE_URL + movie.getBackdropPath();
        Picasso.get()
                .load(backdropPath)
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image_24dp)
                .into(movieBackDrop);
        movieTitle.setText(movie.getTitle());
        moviePlot.setText(movie.getOverview());
        movieReleaseDate.setText(movie.getReleaseDate());
        movieRating.setText(String.valueOf(movie.getVoteAverage()));
        Log.d("Favourite", "Favorite: " + movie.getFavorite());
        if (movie.getFavorite()) {
            fab.setIcon(getResources().getDrawable(R.drawable.ic_star_24dp));
        } else {
            fab.setIcon(getResources().getDrawable(R.drawable.ic_star_border_24dp));
        }

        fab.setOnClickListener(view -> {
            addMovieToFavorite(movie);
            addMovieToDatabase(movie);
            Log.d("Favourite", "Favorite at click: " + movie.getFavorite());
        });

        /*mViewModel.checkFavoriteMovie(movie.getId()).observe(MovieDetailsActivity.this, new Observer<Movie>() {
            @Override
            public void onChanged(Movie singleMovie) {
                if (singleMovie.getId() == movie.getId()) {
                    // Movie is already a favorite
                    fab.setIcon(getResources().getDrawable(R.drawable.ic_star_24dp));
                } else {
                    fab.setIcon(getResources().getDrawable(R.drawable.ic_star_border_24dp));
                }
                fab.setOnClickListener(view -> {
                    if (singleMovie.getId() == movie.getId()) {
                        removeMovieFromDatabase(movie.getId());
                        fab.setIcon(getResources().getDrawable(R.drawable.ic_star_border_24dp));
                        Snackbar.make(mReviewRecyclerView, movie.getTitle() + " removed from favorites", Snackbar.LENGTH_LONG).show();
                    } else {
                        addMovieToDatabase(movie);
                        fab.setIcon(getResources().getDrawable(R.drawable.ic_star_24dp));
                        Snackbar.make(mReviewRecyclerView, movie.getTitle() + " added to favorites", Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        });*/

        // Create Retrofit client
        mService = RetrofitClient.getClient().create(ApiService.class);

        loadMovieTrailers(movie);
        loadMovieReviews(movie);
    }

    private void loadMovieTrailers(Movie movie) {
        Call<TrailerResponse> trailerResponseCall = mService.getMovieTrailers(movie.getId(), API_KEY);
        trailerResponseCall.enqueue(new Callback<TrailerResponse>() {
            @Override
            public void onResponse(@NonNull Call<TrailerResponse> call, @NonNull Response<TrailerResponse> response) {
                List<TrailerResult> trailers = response.body().getTrailerResults();
                TrailerRecyclerAdapter adapter = new TrailerRecyclerAdapter(trailers, MovieDetailsActivity.this);
                mTrailerRecyclerView.setAdapter(adapter);
                mTrailerRecyclerView.setHasFixedSize(true);
                mTrailerRecyclerView.addItemDecoration(new MarginItemDecoration(16));
            }

            @Override
            public void onFailure(@NonNull Call<TrailerResponse> call, @NonNull Throwable t) {
                Snackbar.make(mTrailerRecyclerView, "Could not load Trailers", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void loadMovieReviews(Movie movie) {
        Call<ReviewResponse> reviewResponseCall = mService.getMovieReviews(movie.getId(), API_KEY);
        reviewResponseCall.enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(@NonNull Call<ReviewResponse> call, @NonNull Response<ReviewResponse> response) {
                List<ReviewResult> reviews = response.body().getResults();
                MovieReviewAdapter adapter = new MovieReviewAdapter(reviews, MovieDetailsActivity.this);
                mReviewRecyclerView.setAdapter(adapter);
                mReviewRecyclerView.addItemDecoration(new MarginItemDecoration(16));
            }

            @Override
            public void onFailure(@NonNull Call<ReviewResponse> call, @NonNull Throwable t) {
                Snackbar.make(mTrailerRecyclerView, "Could not load Reviews", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void addMovieToDatabase(Movie movie) {
        mViewModel.insertFavoriteMovie(movie);
    }

    private void removeMovieFromDatabase(Movie movie) {
        mViewModel.deleteFavoriteMovie(movie);
    }

    private void addMovieToFavorite(Movie movie) {
        movie.setFavorite(true);
    }

    @Override
    public void onTrailerItemClick(String key) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        String url = YOUTUBE_BASE_URL + key;
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public void onReviewItemClick(String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}
