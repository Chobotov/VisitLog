package com.android.visitlog;

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
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class PeopleActivity extends AppCompatActivity {


    FloatingActionButton floatingActionButton;
    public static ArrayList<People> people_list;

    TabLayout tabLayout;
    MenuItem search;
    Toolbar toolbar;

    PeopleAdapter peopleAdapter;

    ViewPager viewPager;
    PageAdapter pageAdapter;

    public int AddAllPeopleActivityKey = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

        if (people_list == null)
            people_list = setAllPeople();

        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.pageView);
        pageAdapter = new PageAdapter(getSupportFragmentManager());

        PeopleAdapter.OnLongItemClickListener onLongItemClickListener = item -> {

            Toast.makeText(PeopleActivity.this, "И в чём прикол ?", Toast.LENGTH_SHORT).show();
        };


        pageAdapter.AddFragment(new PeopleFragment(onLongItemClickListener, people_list), getResources().getString(R.string.People));
        pageAdapter.AddFragment(new PeopleFragment(), getResources().getString(R.string.Groups));

        viewPager.setAdapter(pageAdapter);
        tabLayout.setupWithViewPager(viewPager);

//        peopleFragment = (PeopleFragment) pageAdapter.getItem(tabLayout.getSelectedTabPosition());
//        peopleAdapter = (PeopleAdapter)peopleFragment.peopleAdapter;
//
//        peopleAdapter.setPeoples(people_list);
//        peopleAdapter.notifyDataSetChanged();
//

//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager.setCurrentItem(tab.getPosition());
//                int position = tab.getPosition();
//                if(position == 0){
//
//                    Toast.makeText(PeopleActivity.this, "И в чём прикол ?" + position, Toast.LENGTH_SHORT).show();
//                }
//                else if(position ==1){
//
//                }
//            }
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
//
//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        floatingActionButton = (FloatingActionButton) findViewById(R.id.add_float_button_all_people);

        floatingActionButton.setOnClickListener(view -> {


            AlertDialog.Builder builder = new AlertDialog.Builder(PeopleActivity.this);

            builder.setCancelable(true);

            View view1 = LayoutInflater.from(PeopleActivity.this).inflate(R.layout.add_people_alert, null);

            builder.setView(view1);


            builder.setPositiveButton(R.string.Add, (dialogInterface, i) -> {

                EditText editText = view1.findViewById(R.id.text_edit_alertview);
                people_list.add(new People(people_list.size(), editText.getText().toString()));

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


    private ArrayList<People> setAllPeople() {
        ArrayList<People> people = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            people.add(new People(people.size(), "Name" + people.size()));
        }
        return people;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.all_people_menu, menu);

        search = menu.findItem(R.id.app_bar_search);

        SearchView mSearchView = (SearchView) search.getActionView();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                people_list.clear();
                people_list.add(new People(1,"ewfwef"));
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
