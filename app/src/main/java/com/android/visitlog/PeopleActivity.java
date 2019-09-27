package com.android.visitlog;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class PeopleActivity extends AppCompatActivity {


    FloatingActionButton floatingActionButton;
    public static ArrayList<People> people_list;
    public static ArrayList<Groups> groups_list;


    TabLayout tabLayout;
    MenuItem search;
    Toolbar toolbar;
    ViewPager viewPager;
    PageAdapter pageAdapter;

    PeopleFragment peopleFragment;
    GroupsFragment groupsFragment;

    DBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);
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




        PeopleAdapter.ClickListener clickItemPeople = new PeopleAdapter.ClickListener(){

            @Override
            public void onLongItemClick(People item) {
                helper.removePeople(item.Name);
                updatePeople();
            }

            @Override
            public void onItemClick(People item) {

            }

        };


        GroupsAdapter.ClickListener clickItemGroups = new GroupsAdapter.ClickListener(){

            @Override
            public void onLongItemClick(Groups item) {

            }

            @Override
            public void onItemClick(Groups item) {

            }


        };


        peopleFragment = new PeopleFragment(clickItemPeople, people_list);
        groupsFragment = new GroupsFragment(clickItemGroups,groups_list);

        pageAdapter.AddFragment(peopleFragment, getResources().getString(R.string.People));
        pageAdapter.AddFragment(groupsFragment, getResources().getString(R.string.Groups));

        viewPager.setAdapter(pageAdapter);
        tabLayout.setupWithViewPager(viewPager);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.add_float_button_all_people);

        floatingActionButton.setOnClickListener(view -> {


            AlertDialog.Builder builder = new AlertDialog.Builder(PeopleActivity.this);

            builder.setCancelable(true);

            View view1 = LayoutInflater.from(PeopleActivity.this).inflate(R.layout.add_people_alert, null);

            builder.setView(view1);


            builder.setPositiveButton(R.string.Add, (dialogInterface, i) -> {

                EditText editText = view1.findViewById(R.id.text_edit_alertview);

                addNewPeople(editText.getText().toString());



            });
            AlertDialog alertDialog = builder.create();

            alertDialog.show();
        });


        toolbar = findViewById(R.id.toolbar_all_people);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(null);

    }

    private void updateGroups() {
        for (int i = 0; i < 10; i++) {
            groups_list.add(new Groups(("ewf"+groups_list.size())));
        }
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



            AlertDialog.Builder builder = new AlertDialog.Builder(PeopleActivity.this);

            builder.setCancelable(true);

            String newName = name + counter;

            builder.setMessage(R.string.RepeatAlert+'"'+ newName +'"'+ " ?");
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
    }

    private void updatePeople() {

        people_list.clear();
        people_list.addAll(helper.getAllPeople());
        if(peopleFragment!=null)
            peopleFragment.update();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.all_people_menu, menu);

        search = menu.findItem(R.id.app_bar_search);

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
                peopleFragment.update();
                return false;
            }
        });

        return true;
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



}
