package com.android.visitlog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener{

    CalendarView calendarView;
    FloatingActionButton fab;
    public static ListView productList;
    public static ArrayList<People> people = new ArrayList<People>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        calendarView = (CalendarView) findViewById(R.id.calendarView);
        fab.setOnClickListener(this);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                Log.d("Date",dayOfMonth + " " + month + " " + year );
            }
        });

        productList = (ListView) findViewById(R.id.peopleList);
        ItemAdapter adapter = new ItemAdapter(this, R.layout.list_item, people);
        productList.setAdapter(adapter);

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(Main2Activity.this,All_People.class);
        startActivity(intent);
    }
}
