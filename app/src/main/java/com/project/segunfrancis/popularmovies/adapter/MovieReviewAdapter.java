package com.project.segunfrancis.popularmovies.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.segunfrancis.popularmovies.R;
import com.project.segunfrancis.popularmovies.model.ReviewResult;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by SegunFrancis
 */
public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.ViewHolder> {

    private List<ReviewResult> reviews;
    private OnItemClickListener mClickListener;

    public MovieReviewAdapter(List<ReviewResult> reviews, OnItemClickListener clickListener) {
        this.reviews = reviews;
        this.mClickListener = clickListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
        }

        void bind(final ReviewResult review, OnItemClickListener listener) {
            TextView reviewAuthor = itemView.findViewById(R.id.review_author_textView);
            TextView reviewContent = itemView.findViewById(R.id.review_content_textView);
            reviewAuthor.setText(review.getAuthor());
            reviewContent.setText(review.getContent());
            itemView.setOnClickListener(v -> listener.onReviewItemClick(review.getUrl()));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_review_list, parent, false));
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ReviewResult item = reviews.get(position);
        holder.bind(item, mClickListener);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public interface OnItemClickListener {
        void onReviewItemClick(String url);
    }
}