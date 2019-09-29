package com.android.visitlog;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class AddPeopleActivity extends AppCompatActivity {

    public static ArrayList<People> people_list;
    public static ArrayList<Groups> groups_list;


    TabLayout tabLayout;
    MenuItem search;
    MenuItem edit;
    Toolbar toolbar;
    ViewPager viewPager;
    PageAdapter pageAdapter;
    FloatingActionButton floatingActionButton;

    PeopleFragment peopleFragment;
    GroupsFragment groupsFragment;

    DBHelper helper;

    String year;
    String month;
    String day;

    Boolean editMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_people);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setEnabled(false);
        floatingActionButton.hide();
        helper = new DBHelper(this);

        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.pageView);
        pageAdapter = new PageAdapter(getSupportFragmentManager());

        if (groups_list == null) {
            groups_list = new ArrayList<>();
            updateGroups();
        }

        if (people_list == null) {
            people_list = new ArrayList<>();
            updatePeople();
        }


        PeopleAdapter.ClickListener clickItemPeople = new PeopleAdapter.ClickListener() {

            @Override
            public void onLongItemClick(People item) {
                if(!editMode){
                    helper.DeleteAllDataFromDataTable(item.Name);
                    helper.removePeople(item.Name);
                    updatePeople();
                }
            }

            @Override
            public void onItemClick(People item) {
                if(editMode){
                    helper.SetDataInDataTable(item.Name, year, month, day);
                    updatePeople();

                    Toast.makeText(AddPeopleActivity.this,
                            item.Name + " " + getResources().getString(R.string.AddData) + " " + day + "/" + month + "/" + year + ".",
                            Toast.LENGTH_SHORT).show();
                }
            }

        };

        GroupsAdapter.ClickListener clickItemGroups = new GroupsAdapter.ClickListener() {

            @Override
            public void onLongItemClick(Groups item) {
                return;
            }

            @Override
            public void onItemClick(Groups item) {

            }


        };


        peopleFragment = new PeopleFragment(clickItemPeople, people_list);
        groupsFragment = new GroupsFragment(clickItemGroups, groups_list);

        pageAdapter.AddFragment(peopleFragment, getResources().getString(R.string.People));
        pageAdapter.AddFragment(groupsFragment, getResources().getString(R.string.Groups));

        viewPager.setAdapter(pageAdapter);
        tabLayout.setupWithViewPager(viewPager);


        toolbar = findViewById(R.id.toolbar_all_people);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle(null);


        Intent intent = getIntent();

        year = intent.getStringExtra("year");
        month = intent.getStringExtra("month");
        day = intent.getStringExtra("day");

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddPeopleActivity.this);

                builder.setCancelable(true);

                View view1 = LayoutInflater.from(AddPeopleActivity.this).inflate(R.layout.add_people_alert, null);

                builder.setView(view1);

                builder.setPositiveButton(R.string.Add, (dialogInterface, i) -> {

                    EditText editText = view1.findViewById(R.id.text_edit_alertview);

                    addNewPeople(editText.getText().toString());

                });
                AlertDialog alertDialog = builder.create();

                alertDialog.show();
            }
        });

    }

    private void updateGroups () {
        for (int i = 0; i < 10; i++) {
            groups_list.add(new Groups(("ewf" + groups_list.size())));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.add_people_menu, menu);

        search = menu.findItem(R.id.app_bar_search);
        edit = menu.findItem(R.id.editMod);

        edit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(editMode){
                    floatingActionButton.setEnabled(true);
                    floatingActionButton.show();
                    editMode=!editMode;
                }
                else{
                    floatingActionButton.setEnabled(false);
                    floatingActionButton.hide();
                    editMode=!editMode;
                }
                return false;
            }
        });

        SearchView mSearchView = (SearchView) search.getActionView();

        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setDisplayShowHomeEnabled(false);
            }
        });

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                return false;
            }
        });

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(newText.equals(""))
                {
                    updatePeople();
                }
                else {

                    ArrayList<People> people = new ArrayList<>(people_list);
                    ArrayList<People> newPeople = new ArrayList<>();

                    for (int i = 0; i < people_list.size(); i++) {
                        if (filtr(people.get(i).Name,newText)) {

                            newPeople.add(people.get(i));
                        }
                    }
                    people_list.clear();
                    people_list.addAll(newPeople);
                    peopleFragment.update();
                }

                peopleFragment.setCounterText(people_list.size());

                return false;
            }
        });

        return true;
    }

    @Override
    protected void onResume() {
        Log.e("tag","onResume");
        updatePeople();
        super.onResume();

    }

    boolean filtr(String name, String text){

        boolean a = true;
        if(text.length() <= name.length())
            for (int i = 0; i < text.length(); i++) {
                if(text.charAt(i) != name.charAt(i)){
                    a = false;
                }
            }
        else{
            a = false;
        }
        return  a;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addNewPeople(String name) {
        if(!helper.containsPeople(new People(name))) {
            helper.addPeople(name);
        }
        else{
            int counter = 2;

            while (helper.containsPeople(new People(name + counter)))
            {
                counter++;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(AddPeopleActivity.this);

            builder.setCancelable(true);

            String newName = name + counter;


            builder.setMessage(getResources().getString(R.string.RepeatAlert) + " " + '"' + newName + '"' + " ?");
            builder.setPositiveButton("Да", (dialogInterface, i) -> {
                helper.addPeople(newName);
                updatePeople();
            });
            builder.setNegativeButton("Нет", (dialogInterface, i) -> {
                dialogInterface.cancel();
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        updatePeople();
        updateGroups();
    }

    private void updatePeople() {

        people_list.clear();
        people_list.addAll(helper.getAllPeople());

        if(peopleFragment != null) {
            peopleFragment.update();
            peopleFragment.setCounterText(people_list.size());
        }

    }

}
