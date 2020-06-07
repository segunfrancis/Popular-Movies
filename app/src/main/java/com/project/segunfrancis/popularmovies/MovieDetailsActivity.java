package com.project.segunfrancis.popularmovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.project.segunfrancis.popularmovies.adapter.MarginItemDecoration;
import com.project.segunfrancis.popularmovies.adapter.TrailerRecyclerAdapter;
import com.project.segunfrancis.popularmovies.api.ApiService;
import com.project.segunfrancis.popularmovies.api.RetrofitClient;
import com.project.segunfrancis.popularmovies.model.Movie;
import com.project.segunfrancis.popularmovies.model.TrailerResponse;
import com.project.segunfrancis.popularmovies.model.TrailerResult;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.project.segunfrancis.popularmovies.util.AppConstants.INTENT_KEY;
import static com.project.segunfrancis.popularmovies.util.AppConstants.BACKDROP_BASE_URL;
import static com.project.segunfrancis.popularmovies.util.ApiKey.API_KEY;

public class MovieDetailsActivity extends AppCompatActivity implements TrailerRecyclerAdapter.OnItemClickListener {

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ImageView movieBackDrop = findViewById(R.id.backdrop_imageView);
        TextView movieTitle = findViewById(R.id.title_textView);
        TextView moviePlot = findViewById(R.id.plot_synopsis_textView);
        TextView movieReleaseDate = findViewById(R.id.release_date_textView);
        TextView movieRating = findViewById(R.id.vote_average_textView);
        mRecyclerView = findViewById(R.id.trailers_recyclerView);

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

        ApiService service = RetrofitClient.getClient().create(ApiService.class);
        Call<TrailerResponse> trailerResponseCall = service.getMovieTrailers(movie.getId(), API_KEY);
        trailerResponseCall.enqueue(new Callback<TrailerResponse>() {
            @Override
            public void onResponse(@NonNull Call<TrailerResponse> call, @NonNull Response<TrailerResponse> response) {
                List<TrailerResult> trailers = response.body().getTrailerResults();
                TrailerRecyclerAdapter adapter = new TrailerRecyclerAdapter(trailers, MovieDetailsActivity.this);
                mRecyclerView.setAdapter(adapter);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.addItemDecoration(new MarginItemDecoration(16));
            }

            @Override
            public void onFailure(@NonNull Call<TrailerResponse> call, @NonNull Throwable t) {

            }
        });
    }

    @Override
    public void onItemClick(String key) {
        Snackbar.make(mRecyclerView, key, Snackbar.LENGTH_LONG).show();
    }
}
