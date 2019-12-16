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
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    PeopleAdapter peopleAdapter;
    TextView textView;

    ArrayList<People> people_list;
    ArrayList<People> selectedPeople;

    MenuItem search;
    MenuItem edit;
    MenuItem itemSelectedMode;

    MenuItem remove;
    MenuItem rename;



    Toolbar toolbar;

    Button floatingActionButton;

    DBHelper helper;

    String GroupName;

    //boolean editMode;
    boolean selectMode = false;
    boolean selectAll = false;

    boolean editMode = false;

    String year;
    String month;
    String day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        Intent intent = getIntent();

        editMode = intent.getBooleanExtra("edit", editMode);

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
//                if (!selectMode && !editMode ) {
//
//                    selectMode = true;
//                    peopleAdapter.setCheckBoxVisible(selectMode);
//                    peopleAdapter.setCheckBox(selectAll);
//
//                    peopleAdapter.setCheckBox(item);
//                    selectedPeople.add(item);
//
//                    floatingActionButton.setVisibility(Button.VISIBLE);
//
//                    search.setVisible(!selectMode);
//
//                    itemSelectedMode.setVisible(selectMode);
//
//                    if (selectedPeople.size() == people_list.size()) {
//                        selectAll = true;
//                    }
//                    updateIconSelectAllBox();
//                }
            }

            @Override
            public void onItemClick(People item) {
//                if (selectMode) {
//                    if (Contains(item)) {
//                      //  Log.e("Keliz",item.Name + " - contains - true");
//
//                        selectedPeople.remove(item);
//                        selectAll = false;
//
//                    } else {
//                       // Log.e("Keliz",item.Name + " - contains - false");
//
//                        selectedPeople.add(item);
//
//                        if (selectedPeople.size() == people_list.size())
//                            selectAll = true;
//
//                    }
//                    for (People people: selectedPeople)
//                        Log.e("Keliz",people.Name + " - selectAll - " + selectAll);
//
//                    updateIconSelectAllBox();
//                }
                 if(editMode){
                    Intent intent = new Intent(GroupActivity.this, PeopleInformation.class);
                    intent.putExtra("NAME", item.Name);
                    intent.putExtra("YEAR", year);
                    intent.putExtra("MONTH", month);
                    startActivity(intent);
                }
                //else if (editMode){

               // }
                else {
                    if(!editMode) {
                        if (!helper.containsDataPeople(item, year, month, day)) {
                            helper.SetDataInDataTable(item.Name, year, month, day);
                            Toast.makeText(GroupActivity.this,
                                    item.Name + " " + getResources().getString(R.string.AddData) + " " + day + "." + month + "." + year,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(GroupActivity
                                            .this,
                                    item.Name + " " + getResources().getString(R.string.PeopleAlreadyHaveThisDate) + " " + day + "." + month + "." + year,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        };

        PeopleAdapter.RemoveListener removeListener = item -> {
            helper.removePeopleFromGroup(GroupName, item.Name);
            update();

        };


        peopleAdapter = new PeopleAdapter(this, clickListener, removeListener, people_list);

        peopleAdapter.setRemoveVisible(editMode);

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

                floatingActionButton.setVisibility(Button.INVISIBLE);

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

        if(editMode){
            floatingActionButton.setVisibility(Button.VISIBLE);
        }else {
            floatingActionButton.setVisibility(Button.INVISIBLE);

        }

        update();
    }

    public void update() {
        people_list.clear();
        people_list.addAll(helper.getGroupMembers(GroupName));
        peopleAdapter.notifyDataSetChanged();
        textView.setText(people_list.size() + "");
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
    private void RenameGroup(String name) {

        if (!name.equals("")) {

            if (!helper.containsGroup(new Group(name))) {

                helper.RenameGroup(GroupName,name);
                GroupName = name;

            } else {

                int counter = 2;

                while (helper.containsGroup(new Group(name + counter))) {
                    counter++;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setCancelable(true);

                String newName = name + counter;

                builder.setMessage(getResources().getString(R.string.RepeatAlert) + " " + '"' + newName + '"' + " ?");
                builder.setPositiveButton("Да", (dialogInterface, i) -> {
                    helper.RenameGroup(GroupName, newName);
                    GroupName = newName;

                });
                builder.setNegativeButton("Нет", (dialogInterface, i) -> {
                    dialogInterface.cancel();
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
            getSupportActionBar().setTitle(GroupName);
        } else {
            Toast.makeText(this,
                    getResources().getString(R.string.AlertEmptyGroupName),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.groups_activity_menu, menu);

        search = menu.findItem(R.id.app_bar_search);
        edit = menu.findItem(R.id.editMod);
        itemSelectedMode = menu.findItem(R.id.itemCheckBox);
        remove = menu.findItem(R.id.removeItem);
        rename = menu.findItem(R.id.renameItem);

        menu.findItem(R.id.save_data).setVisible(false);
        menu.findItem(R.id.open_data).setVisible(false);

        edit.setVisible(false);
        itemSelectedMode.setVisible(false);


        remove.setVisible(editMode);
        rename.setVisible(editMode);


        remove.setOnMenuItemClickListener(menuItem -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setCancelable(true);
            builder.setMessage("Удалить группу " +'"' + GroupName + '"' + " ?");
            builder.setPositiveButton("Да", (dialogInterface, i) -> {
                helper.removeGroup(GroupName);
                finish();
            });
            builder.setNegativeButton("Нет", (dialogInterface, i) -> {

                dialogInterface.cancel();
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return false;
        });

        rename.setOnMenuItemClickListener(menuItem -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setCancelable(true);

            View view1 = LayoutInflater.from(GroupActivity.this).inflate(R.layout.add_people_alert, null);

            builder.setView(view1);

            builder.setPositiveButton(R.string.Add, (dialogInterface, i) -> {

                EditText editText = view1.findViewById(R.id.text_edit_alertview);
                RenameGroup(editText.getText().toString());

            });
            AlertDialog alertDialog = builder.create();

            alertDialog.show();

        return false;
        });



//
//        edit.setOnMenuItemClickListener(menuItem -> {
////            if (editMode) {
////                peopleAdapter.setRemoveVisible(false);
////                floatingActionButton.hide();
////                editMode = !editMode;
////            } else {
////                edit.setVisible(false);
////                peopleAdapter.setRemoveVisible(true);
////                floatingActionButton.show();
////                editMode = !editMode;
////            }
//            return false;
//        });



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
                selectedPeople.clear();
                updateIconSelectAllBox();

                peopleAdapter.setCheckBox(selectMode);
                peopleAdapter.setCheckBoxVisible(selectMode);
                itemSelectedMode.setVisible(selectMode);

                floatingActionButton.setVisibility(Button.INVISIBLE);
            } else {
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean Contains(People people){
        for (People item : selectedPeople ) {
            if(item.Name == people.Name)
                return true;
        }
        return false;
    }

}
