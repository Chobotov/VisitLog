package com.android.visitlog;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class GroupActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    PeopleAdapter peopleAdapter;
    TextView textView;

    ArrayList<People> people_list;
    ArrayList<People> selectedPeople;

    MenuItem search;
    MenuItem edit;
    MenuItem itemSelectedMode;

    Toolbar toolbar;

    FloatingActionButton floatingActionButton;

    DBHelper helper;

    String GroupName;

    //boolean editMode;
    boolean selectMode = false;
    boolean selectAll = false;

    String year;
    String month;
    String day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        Intent intent = getIntent();
        GroupName = intent.getStringExtra("name");
        year = intent.getStringExtra("year");
        month = intent.getStringExtra("month");
        day = intent.getStringExtra("day");



        recyclerView = findViewById(R.id.alert_people_recyclerView);
        textView = findViewById(R.id.countPeople);

        helper = new DBHelper(this);

        people_list = helper.getGroupMembers(GroupName);
        selectedPeople = new ArrayList<>();


        PeopleAdapter.ClickListener clickListener = new PeopleAdapter.ClickListener() {
            @Override
            public void onLongItemClick(People item) {
                //&& !editMode
                if (!selectMode ) {

                    selectMode = true;
                    peopleAdapter.setCheckBoxVisible(selectMode);
                    peopleAdapter.setCheckBox(selectAll);

                    peopleAdapter.setCheckBox(item);
                    selectedPeople.add(item);

                    floatingActionButton.show();

                    search.setVisible(!selectMode);

                    edit.setVisible(!selectMode);

                    itemSelectedMode.setVisible(selectMode);

                    if (selectedPeople.size() == people_list.size()) {
                        selectAll = true;
                    }
                    updateIconSelectAllBox();


                }
            }

            @Override
            public void onItemClick(People item) {
                if (selectMode) {
                    if (!selectedPeople.contains(item)) {
                        selectedPeople.add(item);

                        if (selectedPeople.size() == people_list.size()) {
                            selectAll = true;
                        }
                        updateIconSelectAllBox();

                    } else {

                        selectedPeople.remove(item);
                        selectAll = false;
                        updateIconSelectAllBox();
                    }
                }
                //else if (editMode){

               // }
                else {
                    if (!helper.containsDataPeople(item, year, month, day)) {
                        helper.SetDataInDataTable(item.Name, year, month, day);
                        Toast.makeText(GroupActivity.this,
                                item.Name + " " + getResources().getString(R.string.AddData) + " " + day + "." + month + "." + year,
                                Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(GroupActivity
                                        .this,
                                item.Name + " " + getResources().getString(R.string.PeopleAlreadyHaveThisDate) + " " + day + "." + month + "." + year,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        PeopleAdapter.RemoveListener removeListener = item -> {
            helper.removePeopleFromGroup(GroupName, item.Name);
            update();

        };


        peopleAdapter = new PeopleAdapter(this, clickListener, removeListener, people_list);
        peopleAdapter.setRemoveVisible(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(peopleAdapter);
        recyclerView.setFocusable(false);
        findViewById(R.id.temp).requestFocus();

        toolbar = findViewById(R.id.toolbar_all_people);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(GroupName);


        floatingActionButton = findViewById(R.id.floatingActionButton);

        floatingActionButton.setOnClickListener(item -> {

            if (selectMode) {
                selectMode = !selectMode;
                selectAll = false;

                search.setVisible(true);

                peopleAdapter.setCheckBox(selectMode);
                peopleAdapter.setCheckBoxVisible(selectMode);
                itemSelectedMode.setVisible(selectMode);

                floatingActionButton.hide();
                edit.setVisible(true);


                for (People i: selectedPeople) {
                    if(!helper.containsDataPeople(i,year,month,day)){
                        helper.SetDataInDataTable(i.Name, year, month, day);
                    }
                }
                updateIconSelectAllBox();

                Toast.makeText(GroupActivity.this,"Добавлены на"+ " " + day + "." + month + "." + year,Toast.LENGTH_SHORT).show();

            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(GroupActivity.this);
                ArrayList<People> newPeople = new ArrayList<>();
                builder.setCancelable(true);
                View view1 = LayoutInflater.from(GroupActivity.this).inflate(R.layout.add_group_people, null);
                builder.setView(view1);

                RecyclerView alertRecycler = view1.findViewById(R.id.alert_people_recyclerView);
                alertRecycler.setBackgroundColor(getResources().getColor(R.color.bg));


                PeopleAdapter.ClickListener alertListener = new PeopleAdapter.ClickListener() {
                    @Override
                    public void onLongItemClick(People item) {

                    }

                    @Override
                    public void onItemClick(People item) {
                        if (!newPeople.contains(item)) {
                            newPeople.add(item);

                            //Toast.makeText(view1.getContext(), item.Name + " " + getResources().getString(R.string.willHasAdd), Toast.LENGTH_SHORT).show();
                        } else {


                            newPeople.remove(item);
                            //Toast.makeText(view1.getContext(), item.Name + " " + getResources().getString(R.string.hasRemoved), Toast.LENGTH_SHORT).show();

                        }
                    }
                };

                PeopleAdapter alertAdapter = new PeopleAdapter(this, alertListener, helper.getAllPeopleNotInGroup(GroupName));

                builder.setPositiveButton(getResources().getString(R.string.Add), (dialogInterface, a) -> {
                    for (People i : newPeople) {
                        helper.addPeopleInGroup(i.Name, GroupName);
                        Log.e("tag0", i.Name);
                    }
                    update();
                });
                alertAdapter.setCheckBoxVisible(true);


                alertRecycler.setLayoutManager(new LinearLayoutManager(this));
                alertRecycler.setAdapter(alertAdapter);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                update();

            }
        });

       // floatingActionButton.show();
        update();
    }

    public void update() {
        people_list.clear();
        people_list.addAll(helper.getGroupMembers(GroupName));
        peopleAdapter.notifyDataSetChanged();
        textView.setText(people_list.size() + " " + getResources().getString(R.string.People) + " "
                + getResources().getString(R.string.in) + " " + GroupName);
    }


    @Override
    protected void onResume() {
        super.onResume();
        update();

    }

    void updateIconSelectAllBox() {

        if(selectAll) {
            itemSelectedMode.setIcon(R.drawable.ic_check_box_black_24dp);

        }
        else{
            itemSelectedMode.setIcon(R.drawable.ic_check_box_outline_blank_black_24dp);

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.people_activity_menu, menu);
        search = menu.findItem(R.id.app_bar_search);
        edit = menu.findItem(R.id.editMod);
        itemSelectedMode = menu.findItem(R.id.itemCheckBox);
        itemSelectedMode.setVisible(selectMode);
        edit.setVisible(false);
        itemSelectedMode.setOnMenuItemClickListener(menuItem -> {

            if(selectAll) {
                selectAll = !selectAll;
                selectedPeople.clear();
                peopleAdapter.setCheckBox(selectAll);

            }
            else{

                selectAll = !selectAll;
                selectedPeople.clear();
                selectedPeople.addAll(people_list);
                peopleAdapter.setCheckBox(selectAll);

            }
            updateIconSelectAllBox();
            update();

            return false;
        });

        edit.setOnMenuItemClickListener(menuItem -> {
//            if (editMode) {
//                peopleAdapter.setRemoveVisible(false);
//                floatingActionButton.hide();
//                editMode = !editMode;
//            } else {
//                edit.setVisible(false);
//                peopleAdapter.setRemoveVisible(true);
//                floatingActionButton.show();
//                editMode = !editMode;
//            }
            return false;
        });

        SearchView mSearchView = (SearchView) search.getActionView();

        mSearchView.setOnSearchClickListener(view -> {
//
//            if (!editMode) {
//                edit.setVisible(false);
//            }
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);


        });

        mSearchView.setOnCloseListener(() -> {
            update();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

//            if (!editMode) {
//                edit.setVisible(true);
//                edit.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//            }

            return false;
        });

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                people_list.clear();
                people_list.addAll(helper.getFilterGroupPeople(newText, GroupName));
                peopleAdapter.notifyDataSetChanged();

                textView.setText(people_list.size() + " " + getResources().getString(R.string.People) + " "
                        + getResources().getString(R.string.in) + " " + GroupName);

                return false;
            }
        });

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

//            if (editMode) {
//                peopleAdapter.setRemoveVisible(false);
//                floatingActionButton.hide();
//                editMode = !editMode;
//                edit.setVisible(true);
//
//            } else
            if (selectMode) {
                selectMode = !selectMode;
                selectAll = false;
                search.setVisible(true);
                updateIconSelectAllBox();

                peopleAdapter.setCheckBox(selectMode);
                peopleAdapter.setCheckBoxVisible(selectMode);
                itemSelectedMode.setVisible(selectMode);

                floatingActionButton.hide();
                edit.setVisible(true);
            } else {
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
