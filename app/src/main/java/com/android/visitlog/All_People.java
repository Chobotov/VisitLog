package com.android.visitlog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.Resource;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class All_People extends AppCompatActivity {


    public FloatingActionButton floatingActionButton;
    public ArrayList<People> people_list;
    public ArrayList<String> groups_list;

    public TabLayout tabLayout;
    public MenuItem search;
    private Toolbar toolbar;

    public RecyclerView recyclerView;
    public RecyclerView.Adapter peopleAdapter;
    public RecyclerView.LayoutManager manager;

    DBHelper dbHelper;


    public int AddAllPeopleActivityKey = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all__people);

        //---------------------Опаснаязона, не трож пока работает ---------------------------------------------

        final NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.Scroll_all_people);
        nestedScrollView.setNestedScrollingEnabled(true);

        //---------------------------Опасная зона, кончилась ---------------------------------------------------


        dbHelper = new DBHelper(this);
        people_list = setAllPeople();
        //groups_list = setAllGroups();


        PeopleAdapter.OnLongItemClickListener onLongItemClickListener = new PeopleAdapter.OnLongItemClickListener() {
            @Override
            public void onLongItemClick(People item) {
                Toast.makeText(All_People.this, "И в чём прикол ?", Toast.LENGTH_SHORT).show();
            }
        };

        recyclerView = findViewById(R.id.all_people_recyclerView);
        recyclerView.setHasFixedSize(true);
        manager = new LinearLayoutManager(this);
        peopleAdapter = new PeopleAdapter(this, onLongItemClickListener, people_list);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(peopleAdapter);


        tabLayout = findViewById(R.id.tabs);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.add_float_button_all_people);

        floatingActionButton.setOnClickListener(view -> {


            AlertDialog.Builder builder = new AlertDialog.Builder(All_People.this);

            builder.setCancelable(true);

            View view1 = LayoutInflater.from(All_People.this).inflate(R.layout.add_custom_alert_view, null);

            builder.setView(view1);



            builder.setPositiveButton(R.string.Add, (dialogInterface, i) ->{

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
        return people;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AddAllPeopleActivityKey) {
            if (resultCode == RESULT_OK) {
                people_list.add(new People(people_list.size(), data.getStringExtra("name")));
                peopleAdapter.notifyDataSetChanged();
            }

        }
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
