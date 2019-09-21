package com.android.visitlog;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PeopleFragment extends Fragment {

    public View v;

    private RecyclerView recyclerView;
    private PeopleAdapter adapter;
    private ArrayList<Item> peoples;
    private PeopleAdapter.OnLongItemClickListener onLongItemClickListener;

    public PeopleFragment(PeopleAdapter.OnLongItemClickListener onLongItemClickListener, ArrayList<Item> peoples) {
        this.onLongItemClickListener = onLongItemClickListener;
        this.peoples = peoples;
    }

    public PeopleFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_people, container, false);
        recyclerView = v.findViewById(R.id.people_recyclerView);

        if (onLongItemClickListener != null)
            adapter = new PeopleAdapter(getContext(), onLongItemClickListener, peoples);
        else
            adapter = new PeopleAdapter(getContext(), null, peoples);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (peoples == null)
            peoples = new ArrayList<Item>();

    }
}
