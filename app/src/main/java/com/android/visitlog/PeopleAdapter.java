package com.android.visitlog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.ViewHolder> {

    private ClickListener clickListener;
    private RemoveListener removeListener;
    private LayoutInflater inflater;
    private ArrayList<People> peoples;
    private boolean removeVisible;


    public PeopleAdapter(Context context, ClickListener clickListener, RemoveListener removeListener, ArrayList<People> peoples) {

        this.inflater = LayoutInflater.from(context);
        this.peoples = peoples;
        this.removeListener = removeListener;
        this.clickListener = clickListener;
        removeVisible = true;
    }


    public PeopleAdapter(Context context, ClickListener clickListener, ArrayList<People> arrayList ) {
        peoples = arrayList;
        this.clickListener = clickListener;
        this.inflater = LayoutInflater.from(context);
        removeVisible = false;
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
        if (clickListener != null) {
            holder.itemView.setOnLongClickListener(view -> {
                clickListener.onLongItemClick(peoples.get(holder.getAdapterPosition()));
                return false;
            });

            holder.itemView.setOnClickListener(view -> {
                clickListener.onItemClick(peoples.get(holder.getAdapterPosition()));
            });
        }

        holder.name.setText(people.Name);

        holder.setRemoveButtonVisible(removeVisible);

        if(removeListener!=null){
            holder.remove.setOnClickListener(view -> removeListener.onRemoveClick(people));
        }

    }

    @Override
    public int getItemCount() {
        return peoples == null ? 0 : peoples.size();
    }

    public boolean isRemoveVisible() {
        notifyDataSetChanged();
        return removeVisible;
    }

    public void setRemoveVisible(boolean removeVisible) {
        this.removeVisible = removeVisible;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        public Button remove;

        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.item_people_text);
            remove = view.findViewById(R.id.remove);
        }

        public void setRemoveButtonVisible(boolean enable){
            if(enable)
                remove.setVisibility(View.VISIBLE);
            else
                remove.setVisibility(View.INVISIBLE);

        }

    }

    interface RemoveListener{
        void  onRemoveClick(People item);
    }

    interface ClickListener {
        void onLongItemClick(People item);
        void onItemClick(People item);
    }
}
