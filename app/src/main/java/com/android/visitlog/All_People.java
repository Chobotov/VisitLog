package com.android.visitlog;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabItem;

import java.util.ArrayList;

public class All_People extends AppCompatActivity {

    public ListView listView;
    public FloatingActionButton floatingActionButton;
    public ArrayList<String> people_list;
    public ArrayList<String> groups_list;

    public ArrayAdapter<String> arrayAdapter;

    public TabItem people_tabItem;
    public TabItem groups_tabItem;


    private Toolbar toolbar;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all__people);
        final NestedScrollView nestedScrollView = (NestedScrollView)findViewById(R.id.Scroll_all_people);
        nestedScrollView.setNestedScrollingEnabled(true);

        groups_tabItem = findViewById(R.id.groups_tabItem);
        people_tabItem = findViewById(R.id.people_tabItem);
        floatingActionButton = (FloatingActionButton)findViewById(R.id.add_float_button_all_people);
        listView = (ListView)findViewById(R.id.listView);
        people_list = new ArrayList<>();
        groups_list = new ArrayList<>();

        

        groups_tabItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayAdapter = new ArrayAdapter<String>(All_People.this,android.R.layout.simple_list_item_1, groups_list);
            }
        });

        people_tabItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayAdapter = new ArrayAdapter<String>(All_People.this,android.R.layout.simple_list_item_1, people_list);
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String add = people_list.size()+"";
                Toast.makeText(All_People.this, add, Toast.LENGTH_SHORT).show();
                arrayAdapter.add(add);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(All_People.this,i+ " твоё число ?", Toast.LENGTH_SHORT).show();
            }

        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                Toast.makeText(All_People.this,i+ " твоё долгое число ?", Toast.LENGTH_SHORT).show();

                return false;
            }
        });


        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, people_list);

        listView.setAdapter(arrayAdapter);

        toolbar = findViewById(R.id.toolbar_all_people);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.all_people_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
