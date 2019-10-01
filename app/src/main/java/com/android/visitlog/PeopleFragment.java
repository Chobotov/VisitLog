package com.android.visitlog;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
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
    private NestedScrollView nestedScrollView;

    private TextView countPeople;

    public PeopleFragment(PeopleAdapter.ClickListener clickListener, ArrayList<People> peoples) {
        this.clickListener = clickListener;
        this.peoples = peoples;
    }

    public PeopleFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_people, container, false);
        recyclerView = v.findViewById(R.id.people_recyclerView);
        countPeople = v.findViewById(R.id.countPeople);
        nestedScrollView =  v.findViewById(R.id.Scroll_all_people);

        if (clickListener != null)
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
            countPeople.setText(text + " "+ getResources().getString(R.string.People));
    }

}
