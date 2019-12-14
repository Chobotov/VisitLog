package com.android.visitlog;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class ItemCommentAdapter extends  RecyclerView.Adapter<ItemCommentAdapter.ViewHolder>{

    public static String Year,Month;
    private People people;
    private ArrayList<Integer> datas;
    private LayoutInflater inflater;
    private DBHelper helper;

    public ItemCommentAdapter(Context context,String PeopleName,String Year,String Month,ArrayList<Integer> datas) {
        this.inflater = LayoutInflater.from(context);
        this.datas = datas;
        this.Year = Year;
        this.Month = Month;
        people = new People(PeopleName);
        helper = new DBHelper(context);
    }

    @NonNull
    @Override
    public ItemCommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.fragment_item_comment,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemCommentAdapter.ViewHolder holder, int position) {
        holder.commit.setText(helper.GetCommentToPeople(people,Year,Month,String.valueOf(datas.get(position))));
        holder.data.setText(datas.get(position)+"/"+Month+"/"+Year);
    }

    @Override
    public int getItemCount() {
        return datas.size();
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
