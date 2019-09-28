package com.android.visitlog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.ViewHolder> {

    private ClickListener clickListener;
    private LayoutInflater inflater;
    private ArrayList<People> peoples;

    public PeopleAdapter(Context context, ClickListener clickListener, ArrayList<People> arrayList) {
        peoples = arrayList;
        this.clickListener = clickListener;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_people_activity, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        People people = peoples.get(position);



        holder.itemView.setOnLongClickListener(view -> {
            if (clickListener != null)
                clickListener.onLongItemClick(peoples.get(holder.getAdapterPosition()));
            return false;
        });

        holder.itemView.setOnClickListener(view -> {
            if (clickListener != null)
                clickListener.onItemClick(peoples.get(holder.getAdapterPosition()));
        });

        holder.name.setText(people.Name);
    }

    @Override
    public int getItemCount() {
        return peoples == null ? 0 : peoples.size();
    }


    public void setPeoples(ArrayList<People> peoples) {
        this.peoples = peoples;
    }

    public ArrayList<People> getPeoples() {
        return peoples;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView countPeoples;

        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.item_people_text);
        }

        public TextView getTextView() {
            return name;
        }
    }

    interface ClickListener {
        void onLongItemClick(People item);
        void onItemClick(People item);
    }
}
