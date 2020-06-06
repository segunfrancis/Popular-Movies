package com.project.segunfrancis.popularmovies.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.project.segunfrancis.popularmovies.R;
import com.project.segunfrancis.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.project.segunfrancis.popularmovies.util.AppConstants.POSTER_BASE_URL;

/**
 * Created by SegunFrancis
 */
public class MoviePosterAdapter extends
        RecyclerView.Adapter<MoviePosterAdapter.ViewHolder> {

    private List<Movie> moviesList;
    private OnItemClickListener onItemClickListener;

    public MoviePosterAdapter(List<Movie> moviesList, OnItemClickListener onItemClickListener) {
        this.moviesList = moviesList;
        this.onItemClickListener = onItemClickListener;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
        }

        void bind(final Movie result, final OnItemClickListener listener) {
            ImageView posterImage = itemView.findViewById(R.id.posterImageView);
            String posterPath = POSTER_BASE_URL + result.getPosterPath();
            Picasso.get()
                    .load(posterPath)
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image_24dp)
                    .into(posterImage);
            itemView.setOnClickListener(v -> listener.onItemClick(result, getLayoutPosition()));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_movie_list, parent, false));
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Movie item = moviesList.get(position);
        holder.bind(item, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Movie result, int position);
    }
}