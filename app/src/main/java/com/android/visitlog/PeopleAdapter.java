package com.android.visitlog;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import java.util.ArrayList;

public class PeopleAdapter extends Adapter {

    private OnLongItemClickListener onLongClickListener;
    private LayoutInflater inflater;
    private ArrayList<People> peoples;

    public PeopleAdapter(Context context, OnLongItemClickListener onLongClickListener, ArrayList<People> arrayList) {
        peoples = arrayList;
        this.onLongClickListener = onLongClickListener;
        this.inflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_people_all_peopleactivity, parent, false);
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final People people = peoples.get(position);

        holder.itemView.setOnLongClickListener(view -> {
            onLongClickListener.onLongItemClick(peoples.get(holder.getAdapterPosition()));
            return false;
        });

    }

    @Override
    public int getItemCount() {
        return peoples == null ? 0 : peoples.size();
    }

    public ArrayList<People> getPeoples() {
        return peoples;
    }

    public void setPeoples(ArrayList<People> peoples) {
        this.peoples = peoples;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name;

        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.textView_item_people_peopleActivity);
        }
    }
    interface OnLongItemClickListener {
        void onLongItemClick(People item);
    }
}
