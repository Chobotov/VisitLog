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

    MenuItem search;
    MenuItem edit;

    Toolbar toolbar;

    FloatingActionButton floatingActionButton;

    DBHelper helper;

    String GroupName;

    boolean editMode;
    boolean selectMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        Intent intent = getIntent();
        GroupName = intent.getStringExtra("name");

        recyclerView = findViewById(R.id.alert_people_recyclerView);
        textView = findViewById(R.id.countPeople);

        helper = new DBHelper(this);

        people_list = helper.getGroupMembers(GroupName);

        PeopleAdapter.ClickListener clickListener = new PeopleAdapter.ClickListener() {
            @Override
            public void onLongItemClick(People item) {
                if(!selectMode && !editMode){
                    selectMode = true;
                }
            }

            @Override
            public void onItemClick(People item) {

            }
        };

        PeopleAdapter.RemoveListener removeListener = item -> {
            helper.removePeopleFromGroup(GroupName,item.Name);
            update();

        };


        peopleAdapter = new PeopleAdapter(this,clickListener,removeListener ,people_list);
        peopleAdapter.setRemoveVisible(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(peopleAdapter);
        recyclerView.setFocusable(false);
        findViewById(R.id.temp).requestFocus();

        toolbar = findViewById(R.id.toolbar_all_people);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(null);


        floatingActionButton = findViewById(R.id.floatingActionButton);

        floatingActionButton.setOnClickListener(item->{

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
                   if(!newPeople.contains(item)){
                       newPeople.add(item);

                       Toast.makeText(view1.getContext(),item.Name + " " +getResources().getString(R.string.willHasAdd),Toast.LENGTH_SHORT).show();
                   }
                   else {



                       newPeople.remove(item);
                       Toast.makeText(view1.getContext(),item.Name + " " + getResources().getString(R.string.hasRemoved),Toast.LENGTH_SHORT).show();

                   }
               }
           };

           PeopleAdapter alertAdapter = new PeopleAdapter(this,alertListener,helper.getAllPeopleNotInGroup(GroupName));

           alertAdapter.setCheckBoxVisible(true);

           alertRecycler.setLayoutManager(new LinearLayoutManager(this));
           alertRecycler.setAdapter(alertAdapter);


           builder.setPositiveButton(getResources().getString(R.string.Add), (dialogInterface, a) -> {
               for (int i = 0; i < newPeople.size(); i++) {
                   helper.addPeopleInGroup(newPeople.get(i).Name,GroupName);
                   Log.e("tag0",newPeople.get(i).Name);
               }

               update();
           });

           AlertDialog alertDialog = builder.create();
           alertDialog.show();

       });
        floatingActionButton.hide();
        update();
    }

    public void update(){
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.people_activity_menu, menu);
        search = menu.findItem(R.id.app_bar_search);
        edit = menu.findItem(R.id.editMod);

        edit.setOnMenuItemClickListener(menuItem -> {
            if (editMode) {
                peopleAdapter.setRemoveVisible(false);
                floatingActionButton.hide();
                editMode = !editMode;
            } else {
                edit.setVisible(false);
                peopleAdapter.setRemoveVisible(true);
                floatingActionButton.show();
                editMode = !editMode;
            }
            return false;
        });

        SearchView mSearchView = (SearchView) search.getActionView();

        mSearchView.setOnSearchClickListener(view -> {

            if (!editMode) {
                edit.setVisible(false);
            }
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);


        });

        mSearchView.setOnCloseListener(() -> {
            update();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

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

                people_list.clear();
                people_list.addAll(helper.getFilterGroupPeople(newText,GroupName));

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
            if (editMode) {
                peopleAdapter.setRemoveVisible(false);
                floatingActionButton.hide();
                editMode = !editMode;
                edit.setVisible(true);
            } else {
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
