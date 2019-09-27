package com.android.visitlog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GroupsFragment extends Fragment {


    public View v;

    private RecyclerView recyclerView;
    private GroupsAdapter adapter;
    private ArrayList<Groups> groups;
    private GroupsAdapter.ClickListener clickListener;

    public GroupsFragment(GroupsAdapter.ClickListener clickListener, ArrayList<Groups> groups) {
        this.clickListener = clickListener;
        this.groups = groups;
    }

    public GroupsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_groups, container, false);
        recyclerView = v.findViewById(R.id.groups_recyclerView);

        if (clickListener != null)
            adapter = new GroupsAdapter(getContext(), clickListener, groups);
        else
            adapter = new GroupsAdapter(getContext(), null, groups);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recyclerView.setAdapter(adapter);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (groups == null)
            groups = new ArrayList<Groups>();

    }

    public void update(){
        adapter.notifyDataSetChanged();
    }
}
