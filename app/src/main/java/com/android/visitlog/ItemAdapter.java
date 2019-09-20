package com.android.visitlog;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<People> peopleList;

    public ItemAdapter(Context context, List<People> people) {

        this.peopleList = people;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ViewHolder holder, int position) {
        People people = peopleList.get(position);
        holder.nameView.setText(people.Name);
        holder.groupView.setText(people.Group);
    }

    @Override
    public int getItemCount() {
        return peopleList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameView, groupView;
        final Button came,leave,remove;
        ViewHolder(final View view){
            super(view);

            nameView = (TextView) view.findViewById(R.id.name);
            groupView = (TextView) view.findViewById(R.id.group);
            came = (Button) view.findViewById(R.id.came);
            leave = (Button) view.findViewById(R.id.leave);
            remove = (Button) view.findViewById(R.id.remove);

            came.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DateFormat df = new SimpleDateFormat("HH:mm");
                    String date = df.format(Calendar.getInstance().getTime());
                    came.setText(date);
                }
            });

            leave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DateFormat df = new SimpleDateFormat("HH:mm");
                    String date = df.format(Calendar.getInstance().getTime());
                    leave.setText(date);
                }
            });

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DBHelper dbHelper = new DBHelper(v.getContext());
                    String name = peopleList.get(getAdapterPosition()).Name;
                    dbHelper.DeleteDataByName(dbHelper,name);
                    peopleList.remove(getAdapterPosition());
                    notifyDataSetChanged();
                    Log.d("delete",name);
                }
            });
        }
    }
}

