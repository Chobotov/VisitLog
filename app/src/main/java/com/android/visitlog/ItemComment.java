package com.android.visitlog;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.ViewGroup;



public class ItemComment extends  RecyclerView.Adapter<ItemComment.ViewHolder>{

    public ItemComment() {

    }

    @NonNull
    @Override
    public ItemComment.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemComment.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
