package com.android.visitlog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;


public class GroupsActivityFragment extends Fragment {

    private View v;

    private RecyclerView recyclerView;
    private GroupsAdapter adapter;
    private ArrayList<Group> groups;
    private GroupsAdapter.ClickListener clickListener;
    private TextView countGroup;

    private AppBarLayout appBarLayout;
    private MenuItem search;
    private MenuItem edit;
    private MenuItem itemCheckBox;

    private Toolbar toolbar;

    private boolean editMode = false;

    private DBHelper helper;

    private String[] data;// day, month, year

    public GroupsActivityFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_groups_activity, container, false);

        appBarLayout = v.findViewById(R.id.appbar);
        recyclerView = v.findViewById(R.id.groups_recyclerView);
        countGroup = v.findViewById(R.id.countGroup);
        toolbar = v.findViewById(R.id.toolbar_all_people);


        helper = new DBHelper(v.getContext());

        data = GetData();

        if (groups == null) {
            groups = new ArrayList<>();
            updateGroups();
        }



        clickListener = new GroupsAdapter.ClickListener() {
            @Override
            public void onLongItemClick(Group item) {
                if (editMode) {
                    helper.removeGroup(item.Name);
                    groups.remove(item);
                    update();
                    setCounterText(groups.size());

                } else {
                    ArrayList<People> peopleInGroup = helper.getGroupMembers(item.Name);

                    for (People i : peopleInGroup) {
                        if (!helper.containsDataPeople(i, data[0], data[1], data[2])) {
                            helper.SetDataInDataTable(i.Name,  data[0], data[1], data[2]);
                        }
                    }



                    Toast.makeText(v.getContext(),
                            getResources().getString(R.string.addGroupDate1) + " "
                                    + item.Name + " "
                                    + getResources().getString(R.string.addGroupDate2) + " "
                                    + data[0] + "." + data[1] + "." + data[2]
                            ,
                            Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onItemClick(Group item) {
                if (!editMode) {
                    Intent intent = new Intent(v.getContext(), GroupActivity.class);
                    intent.putExtra("name", String.valueOf(item.Name));
                    intent.putExtra("year", String.valueOf(data[0]));
                    intent.putExtra("month", String.valueOf(data[1]));
                    intent.putExtra("day", String.valueOf(data[2]));
                    startActivity(intent);
                }
            }
        };

        if (clickListener != null)
            adapter = new GroupsAdapter(getContext(), clickListener, groups);
        else
            adapter = new GroupsAdapter(getContext(), null, groups);

        recyclerView.setLayoutManager(new GridLayoutManager(v.getContext(),2));
        recyclerView.setAdapter(adapter);

        recyclerView.setFocusable(false);

        v.findViewById(R.id.temp).requestFocus();
        setCounterText(groups.size());

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(null);

        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

        return v;
    }

    //затычка
    private String[] GetData() {
        return new String[]{"00","00","0000"};
    }


    public void update(){
        if(adapter!=null)
            adapter.notifyDataSetChanged();
    }


    public void setCounterText(int text){
        if(countGroup != null)
            countGroup.setText(text + " "+ getResources().getString(R.string.Groups));
    }

    private void updateGroups() {
        groups.clear();
        groups.addAll(helper.getAllGroups());

        for (Group group : groups) {
            group.Count = helper.getGroupMembers(group.Name).size();
        }
        if (groups != null) {
            update();
            setCounterText(groups.size());
        }
    }




    public void onResume() {
        updateGroups();
        super.onResume();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {

        menuInflater.inflate(R.menu.people_activity_menu, menu);
        super.onCreateOptionsMenu(menu,menuInflater);

        search = menu.findItem(R.id.app_bar_search);
        edit = menu.findItem(R.id.editMod);
        itemCheckBox = menu.findItem(R.id.itemCheckBox);
        itemCheckBox.setVisible(false);

        itemCheckBox.setOnMenuItemClickListener(item -> {


            return false;
        });

        edit.setOnMenuItemClickListener(menuItem -> {
            if (editMode) {
                this.setRemoveVisible(false);
                //floatingActionButton.hide();
                editMode = !editMode;
            } else {
                edit.setVisible(false);
                this.setRemoveVisible(true);
               // floatingActionButton.show();
                editMode = !editMode;
            }
            return false;
        });

        SearchView mSearchView = (SearchView) search.getActionView();

        mSearchView.setOnSearchClickListener(view -> {

            if (!editMode) {
                edit.setVisible(false);
            }
            ((AppCompatActivity)(getActivity())).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            ((AppCompatActivity)(getActivity())).getSupportActionBar().setDisplayShowHomeEnabled(false);


        });

        mSearchView.setOnCloseListener(() -> {
            updateGroups();

            ((AppCompatActivity)(getActivity())).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity)(getActivity())).getSupportActionBar().setDisplayShowHomeEnabled(true);


            if (!editMode) {
                edit.setVisible(true);
                edit.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            }
            return false;
        });

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.equals("")) {
                        updateGroups();
                }
                else {
                        groups.clear();
                        groups.addAll(helper.getGroupsFilter(newText));
                        update();
                }
                setCounterText(groups.size());
                return false;
            }
        });
    }

    private void setRemoveVisible(boolean b) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            if (editMode) {

                setRemoveVisible(false);
                editMode = !editMode;
                edit.setVisible(true);
                ((AppCompatActivity)(getActivity())).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                ((AppCompatActivity)(getActivity())).getSupportActionBar().setDisplayShowHomeEnabled(false);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void showPopup(View v){
        PopupMenu popupMenu = new PopupMenu(getContext(),v);
        popupMenu.inflate(R.menu.item_group_menu);
        popupMenu.setOnMenuItemClickListener(menuItem -> {
        return false;
        });

        popupMenu.show();
    }
}
