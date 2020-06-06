package com.project.segunfrancis.popularmovies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.segunfrancis.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import static com.project.segunfrancis.popularmovies.util.AppConstants.INTENT_KEY;
import static com.project.segunfrancis.popularmovies.util.AppConstants.BACKDROP_BASE_URL;

public class MovieDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ImageView movieBackDrop = findViewById(R.id.backdrop_imageView);
        TextView movieTitle = findViewById(R.id.title_textView);
        TextView moviePlot = findViewById(R.id.plot_synopsis_textView);
        TextView movieReleaseDate = findViewById(R.id.release_date_textView);
        TextView movieRating = findViewById(R.id.vote_average_textView);

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
    }
}
