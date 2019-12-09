package com.android.visitlog;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class ItemComment extends  RecyclerView.Adapter<ItemComment.ViewHolder>{

    private ArrayList<People> peopleList;
    private LayoutInflater inflater;

    public ItemComment(Context context, ArrayList<People> people) {
        this.inflater = LayoutInflater.from(context);
        this.peopleList = people;
    }

    @NonNull
    @Override
    public ItemComment.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.fragment_item_comment,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemComment.ViewHolder holder, int position) {
        final People people = peopleList.get(position);
        holder.commit.setText(people.commit);
        holder.data.setText("");
    }

    @Override
    public int getItemCount() {
        return peopleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView data,commit;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            data = itemView.findViewById(R.id.inputData);
            commit = itemView.findViewById(R.id.inputCommit);
        }
    }
}
