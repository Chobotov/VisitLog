package com.android.visitlog;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class PeopleFragment extends Fragment {

    public View v;

    private RecyclerView recyclerView;

    private PeopleAdapter adapter;
    private ArrayList<People> peoples;
    private PeopleAdapter.ClickListener clickListener;
    private PeopleAdapter.RemoveListener removeListener;


    private TextView countPeople;

    public PeopleFragment(PeopleAdapter.ClickListener clickListener,PeopleAdapter.RemoveListener removeListener , ArrayList<People> peoples) {
        this.removeListener = removeListener;
        this.clickListener = clickListener;
        this.peoples = peoples;
    }

    public PeopleFragment(PeopleAdapter.ClickListener clickListener, ArrayList<People> peoples) {
        this.clickListener = clickListener;
        this.peoples = peoples;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_people, container, false);
        recyclerView = v.findViewById(R.id.alert_people_recyclerView);
        countPeople = v.findViewById(R.id.countPeople);

        if (clickListener != null && removeListener!= null) {
            adapter = new PeopleAdapter(getContext(), clickListener, removeListener, peoples);
            adapter.setRemoveVisible(false);
        }
        else if (clickListener != null)
            adapter = new PeopleAdapter(getContext(), clickListener, peoples);
        else
            adapter = new PeopleAdapter(getContext(), null, peoples);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        recyclerView.setFocusable(false);
        v.findViewById(R.id.temp).requestFocus();

        setCounterText(peoples.size());

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (peoples == null)
            peoples = new ArrayList<>();
    }

    public void update(){
        if(adapter!=null && v!=null) {
            adapter.notifyDataSetChanged();

        }
    }

    public void setCounterText(int text){
        if(countPeople!=null)
            countPeople.setText(text+"");
    }


    public void setRemoveVisible(boolean enable){
        if(adapter != null){
            adapter.setRemoveVisible(enable);
        }
    }
    public boolean getRemoveVisible(){
        if(adapter!= null){
           return adapter.isRemoveVisible();
        }
        return false;
    }



}
