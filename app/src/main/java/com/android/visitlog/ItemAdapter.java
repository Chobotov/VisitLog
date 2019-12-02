package com.android.visitlog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private ComeLeaveRemove clr;
    private LayoutInflater inflater;
    private ArrayList<People> peopleList;
    private DBHelper helper;

    public ItemAdapter(Context context, ArrayList<People> people,ComeLeaveRemove clr) {

        this.peopleList = people;
        this.clr = clr;
        this.inflater = LayoutInflater.from(context);
        helper = new DBHelper(context);
    }


    @NonNull
    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final People people = peopleList.get(position);
        clr.CheckTime(people);
        holder.nameView.setText(people.Name);
        holder.groupView.setText(people.Group);
        holder.setTime.setText(people.CameTime + " - " +  people.LeaveTime);
        holder.comment.setText(people.commit);


        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.getAdapterPosition()>=0)
                {
                    clr.RemovePeopleData(peopleList.get(holder.getAdapterPosition()),holder.getAdapterPosition());
                }
            }
        });

        holder.time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clr.InsertTimeInData(peopleList.get(holder.getAdapterPosition()));
            }
        });

        holder.setComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clr.InsertCommentInData(peopleList.get(holder.getAdapterPosition()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return peopleList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameView, groupView, setTime,comment;
        ImageButton time,setComment;
        Button remove;
        ViewHolder(final View view){
            super(view);
            setTime = (TextView) view.findViewById(R.id.setTime);
            nameView = (TextView) view.findViewById(R.id.name);
            groupView = (TextView) view.findViewById(R.id.group);
            comment = (TextView) view.findViewById(R.id.comment);
            time = (ImageButton) view.findViewById(R.id.time);
            setComment = (ImageButton) view.findViewById(R.id.setComment);
            remove = (Button) view.findViewById(R.id.remove);
        }
    }
    interface ComeLeaveRemove{
        void RemovePeopleData(People people,int position);
        void InsertTimeInData(People people);
        void InsertCommentInData(People people);
        void CheckTime(People people);
    }
}

