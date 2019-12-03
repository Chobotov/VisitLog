package com.android.visitlog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GroupsAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private GroupsAdapter.ClickListener listener;
    private LayoutInflater inflater;
    private ArrayList<Group> groups;

    public boolean EmptyItemEnable = false,
                    MoreButtonEnable = false;




    public GroupsAdapter(Context context, GroupsAdapter.ClickListener onLongClickListener, ArrayList<Group> arrayList) {
        groups = arrayList;
        this.listener = onLongClickListener;
        this.inflater = LayoutInflater.from(context);

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View item = inflater.inflate(R.layout.item_groups_cardview, parent, false);
        View empty =  inflater.inflate(R.layout.empty_groups_cardview, parent, false);
        RecyclerView.ViewHolder viewHolder = viewType == 0 ? new ItemViewHolder(item) : new EmptyViewHolder(empty);


        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(getItemViewType(position) == 1){
            if(EmptyItemEnable) {
                GroupsAdapter.EmptyViewHolder empty = (GroupsAdapter.EmptyViewHolder) holder;

                empty.v.setOnClickListener(view -> {
                    if (listener != null) {
                        listener.onAddItemClick();
                        notifyDataSetChanged();
                    }
                });
            }

        }else {

            Group group = groups.get(position);
            GroupsAdapter.ItemViewHolder item = (GroupsAdapter.ItemViewHolder)holder;


            item.itemView.setOnLongClickListener(view -> {
                if (listener != null)
                    listener.onLongItemClick(groups.get(holder.getAdapterPosition()));
                return true;
            });

            item.itemView.setOnClickListener(view -> {
                if (listener != null)
                    listener.onItemClick(groups.get(holder.getAdapterPosition()));
            });

            item.more.setOnClickListener( view ->{
                if(listener != null)
                    listener.onMoreItemClick(groups.get(holder.getAdapterPosition()));

            });

            item.name.setText(group.Name);
            item.members.setText(inflater.getContext().getResources().getString(R.string.Members) + " " + group.Count);

            item.setMoreButtonVisible(MoreButtonEnable);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int itemCount = EmptyItemEnable ? (getItemCount() - 1) : getItemCount();


        if(position == itemCount)
            return 1;
        else{
            return 0;
        }
    }

    @Override
    public int getItemCount() {

        return groups == null ?
                ((EmptyItemEnable) ? 1 : 0)
                :
                groups.size() + ((EmptyItemEnable) ? 1 : 0);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView members;
        private Button more;

        ItemViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.item_group_text);
            members = view.findViewById(R.id.item_group_members);
            more = view.findViewById(R.id.More);
        }
        public void setMoreButtonVisible(boolean enable){
            if(enable)
                more.setVisibility(View.VISIBLE);
            else
                more.setVisibility(View.INVISIBLE);

        }
        public TextView getTextView() {
            return name;
        }
    }

    public static class EmptyViewHolder extends RecyclerView.ViewHolder {
        View v;
        EmptyViewHolder (View view) {
            super(view);
            v = view;
        }

    }


    interface ClickListener {
        void onLongItemClick(Group item);
        void onItemClick(Group item);
        void onMoreItemClick(Group item);
        void onAddItemClick();

    }
}
