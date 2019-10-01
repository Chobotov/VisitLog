package com.android.visitlog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GroupsFragment extends Fragment {


    public View v;

    private RecyclerView recyclerView;
    private GroupsAdapter adapter;
    private ArrayList<Group> groups;
    private GroupsAdapter.ClickListener clickListener;
    private TextView countGroup;

    public GroupsFragment(GroupsAdapter.ClickListener clickListener, ArrayList<Group> groups) {
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
        countGroup = v.findViewById(R.id.countGroup);
        if (clickListener != null)
            adapter = new GroupsAdapter(getContext(), clickListener, groups);
        else
            adapter = new GroupsAdapter(getContext(), null, groups);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recyclerView.setAdapter(adapter);

        setCounterText(groups.size());
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (groups == null)
            groups = new ArrayList<>();

    }

    public void update(){
        if(adapter!=null)
            adapter.notifyDataSetChanged();
    }


    public void setCounterText(int text){
        if(countGroup != null)
            countGroup.setText(text + " "+ getResources().getString(R.string.Groups));
    }
}
