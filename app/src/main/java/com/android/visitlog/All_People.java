package com.android.visitlog;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class All_People extends AppCompatActivity {


    public FloatingActionButton floatingActionButton;
    public ArrayList<People> people_list;
    public ArrayList<String> groups_list;

    public TabLayout tableLayout;
    public MenuItem search;
    private Toolbar toolbar;

    public DBHelper dbHelper;

    public RecyclerView recyclerView;
    public PeopleAdapter peopleAdapter;

    public int AddAllpeopleActivityKey = 1;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all__people);

        //---------------------Опаснаязона, не трож пока работает ---------------------------------------------

        final NestedScrollView nestedScrollView = (NestedScrollView)findViewById(R.id.Scroll_all_people);
        nestedScrollView.setNestedScrollingEnabled(true);

        //---------------------------Опасная зона, кончилась ---------------------------------------------------

        people_list = new ArrayList<>();
        groups_list = new ArrayList<>();



        recyclerView = findViewById(R.id.all_people_recyclerView);

        peopleAdapter = new PeopleAdapter(this,view -> new PeopleAdapter.OnLongItemClickListener(){
            @Override
            public void onLongItemClick(People item) {

            }
        }, people_list );

        recyclerView.setAdapter(peopleAdapter);

        tableLayout = findViewById(R.id.tabs);

        floatingActionButton = (FloatingActionButton)findViewById(R.id.add_float_button_all_people);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String add = people_list.size()+"";
                Toast.makeText(All_People.this, add, Toast.LENGTH_SHORT).show();

                Intent questionIntent = new Intent(All_People.this,
                        AddPeopleActivity.class);
                startActivityForResult(questionIntent, AddAllpeopleActivityKey);
            }

        });




        toolbar = findViewById(R.id.toolbar_all_people);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(null);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AddAllpeopleActivityKey) {
            if (resultCode == RESULT_OK) {
                people_list.add(new People(people_list.size(),data.getStringExtra("name")));

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
