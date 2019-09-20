package com.android.visitlog;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ItemAdapter extends ArrayAdapter<People> {

    private LayoutInflater inflater;
    private int layout;
    private ArrayList<People> peopleList;

    public ItemAdapter(Context context, int resource, ArrayList<People> people) {
        super(context, resource, people);
        this.peopleList = people;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    public View getView(final int position, View convertView, final ViewGroup parent) {

        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final People people = peopleList.get(position);

        viewHolder.name.setText(people.Name);
        viewHolder.group.setText(people.Group);

        viewHolder.cameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateFormat df = new SimpleDateFormat("HH:mm");
                String date = df.format(Calendar.getInstance().getTime());
                viewHolder.cameButton.setText(date);
            }
        });

        viewHolder.leaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateFormat df = new SimpleDateFormat("HH:mm");
                String date = df.format(Calendar.getInstance().getTime());
                viewHolder.leaveButton.setText(date);
            }
        });

        viewHolder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("delete","delete");
                //peopleList.remove();
            }
        });

        return convertView;
    }

    private class ViewHolder {
        final Button cameButton, leaveButton, removeButton;
        final TextView name, group;

        ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.name);
            group = (TextView) view.findViewById(R.id.group);
            cameButton = (Button) view.findViewById(R.id.came);
            leaveButton = (Button) view.findViewById(R.id.leave);
            removeButton = (Button) view.findViewById(R.id.remove);
        }
    }
}
