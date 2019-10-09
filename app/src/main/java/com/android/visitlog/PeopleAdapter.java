package com.android.visitlog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.ViewHolder> {

    private ClickListener clickListener;
    private RemoveListener removeListener;
    private LayoutInflater inflater;
    private ArrayList<People> peoples;

    private People selectPeople;

    private boolean removeVisible;
    private boolean checkBoxVisible;
    private boolean checkBox;


    public PeopleAdapter(Context context, ClickListener clickListener, RemoveListener removeListener, ArrayList<People> peoples) {

        this.inflater = LayoutInflater.from(context);
        this.peoples = peoples;
        this.removeListener = removeListener;
        this.clickListener = clickListener;
        removeVisible = true;
        checkBoxVisible = false;
        checkBox = false;
    }


    public PeopleAdapter(Context context, ClickListener clickListener, ArrayList<People> arrayList ) {
        peoples = arrayList;
        this.clickListener = clickListener;
        this.inflater = LayoutInflater.from(context);
        removeVisible = false;
        checkBoxVisible = false;
        checkBox = false;
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

        holder.setRemoveButtonVisible(removeVisible);

        holder.setCheckBoxVisible(checkBoxVisible);

        if(people.equals(selectPeople)){
            holder.setCheckBoxStatus(!holder.getCheckBoxStatus());
            selectPeople = null;

        }
        else{
            holder.setCheckBoxStatus(checkBox);
        }

        holder.name.setText(people.Name);



        if (clickListener != null) {

            holder.itemView.setOnLongClickListener(view -> {

                clickListener.onLongItemClick(peoples.get(holder.getAdapterPosition()));

                return true;
            });

            holder.itemView.setOnClickListener(view -> {
                clickListener.onItemClick(peoples.get(holder.getAdapterPosition()));
                if(checkBoxVisible)
                    holder.setCheckBoxStatus(!holder.getCheckBoxStatus());
            });

            holder.checkBox.setOnClickListener((item) -> {
                    clickListener.onItemClick(peoples.get(holder.getAdapterPosition()));
            });
        }

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
        public CheckBox checkBox;

        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.item_people_text);
            remove = view.findViewById(R.id.remove);
            checkBox = view.findViewById(R.id.checkBox);


        }

        public void setRemoveButtonVisible(boolean enable){
            if(enable)
                remove.setVisibility(View.VISIBLE);
            else
                remove.setVisibility(View.INVISIBLE);

        }
        public void setCheckBoxVisible(boolean enable){
            if(enable)
                checkBox.setVisibility(View.VISIBLE);
            else
                checkBox.setVisibility(View.INVISIBLE);

        }

        public void setCheckBoxStatus(boolean status){
           checkBox.setChecked(status);
        }

        public boolean getCheckBoxStatus(){
            return checkBox.isChecked();
        }
    }

    public void setCheckBox(boolean checkBox) {

        this.checkBox = checkBox;
        notifyDataSetChanged();
    }

    public void setCheckBox(People selectPeople) {

        this.selectPeople = selectPeople;
        notifyDataSetChanged();
    }


    public boolean isCheckBoxVisible() {
        return checkBoxVisible;
    }

    public void setCheckBoxVisible(boolean checkBoxVisible) {
        this.checkBoxVisible = checkBoxVisible;
        notifyDataSetChanged();
    }




    interface RemoveListener{
        void  onRemoveClick(People item);
    }

    interface ClickListener {
        void onLongItemClick(People item);
        void onItemClick(People item);
    }
}
