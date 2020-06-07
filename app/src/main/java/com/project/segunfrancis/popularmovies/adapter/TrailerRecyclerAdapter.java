package com.project.segunfrancis.popularmovies.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.segunfrancis.popularmovies.R;
import com.project.segunfrancis.popularmovies.model.TrailerResult;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by SegunFrancis
 */
public class TrailerRecyclerAdapter extends
        RecyclerView.Adapter<TrailerRecyclerAdapter.ViewHolder> {


    private List<TrailerResult> list;
    private OnItemClickListener onItemClickListener;

    public TrailerRecyclerAdapter(List<TrailerResult> list, OnItemClickListener onItemClickListener) {
        this.list = list;
        this.onItemClickListener = onItemClickListener;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
        }

        void bind(final TrailerResult trailer,
                  final OnItemClickListener listener) {
            TextView title = itemView.findViewById(R.id.trailer_title_textView);
            title.setText(trailer.getName());
            itemView.setOnClickListener(v -> listener.onItemClick(trailer.getId()));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_trailer_list, parent, false));
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TrailerResult item = list.get(position);
        holder.bind(item, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener {
        void onItemClick(String key);
    }
}