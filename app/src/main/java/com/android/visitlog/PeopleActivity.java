package com.android.visitlog;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionMenuView;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class PeopleActivity extends AppCompatActivity {


    FloatingActionButton floatingActionButton;
    public static ArrayList<People> people_list;
    public static ArrayList<People> groups_list;


    TabLayout tabLayout;
    MenuItem search;
    MenuItem edit;
    Toolbar toolbar;
    ViewPager viewPager;
    PageAdapter pageAdapter;

    PeopleFragment peopleFragment;

    DBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);
        this.setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        helper = new DBHelper(this);


        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.pageView);
        pageAdapter = new PageAdapter(getSupportFragmentManager());





        if (people_list == null) {
            people_list = new ArrayList<>();
            updatePeople();
        }
        PeopleAdapter.OnLongItemClickListener onLongItemClickListener = new PeopleAdapter.OnLongItemClickListener(){


            @Override
            public void onLongItemClick(People item) {
                helper.removePeople(item.Name);
                updatePeople();
            }

            @Override
            public void onItemClick(People item) {

            }


        };


        peopleFragment = new PeopleFragment(onLongItemClickListener, people_list);

        pageAdapter.AddFragment(peopleFragment, getResources().getString(R.string.People));
        pageAdapter.AddFragment(new PeopleFragment(), getResources().getString(R.string.Groups));

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

            builder.setMessage("Такой наименование уже есть. Создать новый контакт под названием "+'"'+ newName +'"'+ " ?");
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
        edit = menu.findItem(R.id.editMod);

        SearchView mSearchView = (SearchView) search.getActionView();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                people_list.clear();

                ArrayList<People> people = helper.getAllPeople(),
                        newPeople = new ArrayList<>();
                for (int i = 0; i < people.size(); i++) {
                    if(people.get(i).Name.equals(newText)){
                        newPeople.add(people.get(i));
                    }
                }
                people_list.addAll(newPeople);
                peopleFragment.update();
                return false;
            }
        });

        return true;
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
