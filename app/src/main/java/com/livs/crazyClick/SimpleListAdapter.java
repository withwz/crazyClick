package com.livs.crazyClick;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class SimpleListAdapter extends RecyclerView.Adapter<SimpleListAdapter.ViewHolder> {

    private final List<String> names;
    private final List<String> statuses;
    private final List<Integer> icons;
    private final OnItemClickListener listener;

    public SimpleListAdapter(List<String> names, List<String> statuses, List<Integer> icons, OnItemClickListener listener) {
        this.names = names;
        this.statuses = statuses;
        this.icons = icons;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = names.get(position);
        String status = statuses.get(position);
        int icon = icons.get(position);

        holder.nameTextView.setText(name);
        holder.statusTextView.setText(status);
        holder.iconImageView.setImageResource(icon);

        holder.bind(position, name, status, icon, listener);
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView statusTextView;
        public ImageView iconImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            iconImageView = itemView.findViewById(R.id.iconImageView);
        }

        public void bind(int position, String name, String status, int icon, OnItemClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(position, name, status, icon));
        }
    }
}

