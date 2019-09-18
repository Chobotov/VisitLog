package com.android.visitlog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PeopleAdapter extends  RecyclerView.Adapter<PeopleAdapter.ViewHolder> {

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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_people_all_peopleactivity, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        People people = peoples.get(position);

        holder.itemView.setOnLongClickListener(view -> {
            onLongClickListener.onLongItemClick(peoples.get(holder.getAdapterPosition()));
            return false;
        });

        holder.name.setText(people.Name);

    }

    @Override
    public int getItemCount() {
        return peoples == null ? 0 : peoples.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;

        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.textView_item_people_peopleActivity);
        }

        public TextView getTextView() {
            return name;
        }
    }
    interface OnLongItemClickListener {
        void onLongItemClick(People item);
    }
}
