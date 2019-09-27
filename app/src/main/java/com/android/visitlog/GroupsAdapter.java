package com.android.visitlog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GroupsAdapter  extends RecyclerView.Adapter<GroupsAdapter.ViewHolder>{

    private GroupsAdapter.ClickListener listener;
    private LayoutInflater inflater;
    private ArrayList<Groups> groups;

    public GroupsAdapter(Context context, GroupsAdapter.ClickListener onLongClickListener, ArrayList<Groups> arrayList) {
        groups = arrayList;
        this.listener = listener;
        this.inflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public GroupsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_groups_cardview, parent, false);
        return new GroupsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Groups people = groups.get(position);



        holder.itemView.setOnLongClickListener(view -> {
            if (listener != null)
                listener.onLongItemClick(groups.get(holder.getAdapterPosition()));
            return false;
        });

        holder.itemView.setOnClickListener(view -> {
            if (listener != null)
                listener.onItemClick(groups.get(holder.getAdapterPosition()));
        });

        holder.name.setText(people.Name);

    }



    @Override
    public int getItemCount() {

            return groups == null ? 0 : groups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;

        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.item_group_text);
        }

        public TextView getTextView() {
            return name;
        }
    }


    interface ClickListener {
        void onLongItemClick(Groups item);
        void onItemClick(Groups item);
    }
}
