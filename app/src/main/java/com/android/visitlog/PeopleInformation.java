package com.android.visitlog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class PeopleInformation extends AppCompatActivity {

    TextView days,hours;
    DBHelper helper;
    String Year,Month,Name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_information);

        helper = new DBHelper(this);

        days = findViewById(R.id.valueOfDays);
        hours = findViewById(R.id.valueOfHours);

        Intent intent = getIntent();

        Year = intent.getStringExtra("YEAR");
        Month = intent.getStringExtra("MONTH");
        Name = intent.getStringExtra("NAME");

        Log.d("year",Year);
        Log.d("month",Month);
        Log.d("name",Name);


        UpdateData();
    }

    private void UpdateData(){
        days.setText(helper.CameDaysInMonth(0,Name,Year,Month));
        hours.setText(helper.AvgHours(0,Name,Year,Month));
    }
}
