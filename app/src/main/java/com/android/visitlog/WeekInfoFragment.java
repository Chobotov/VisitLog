package com.android.visitlog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class WeekInfoFragment extends Fragment {

    View v;
    TextView days,hours;
    DBHelper helper;
    String Year,Month,Name;


    public WeekInfoFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_week_info, container, false);

        helper = new DBHelper(getContext());

        days = v.findViewById(R.id.valueOfDays);
        hours = v.findViewById(R.id.valueOfHours);

        Intent intent = getActivity().getIntent();

        Year = intent.getStringExtra("YEAR");
        Month = intent.getStringExtra("MONTH");
        Name = intent.getStringExtra("NAME");

        Log.d("year",Year);
        Log.d("month",Month);
        Log.d("name",Name);


        UpdateData();


        return  v;
    }

    private void UpdateData(){
        days.setText(helper.CameDaysInMonth(0,Name,Year,Month));
        hours.setText(helper.AvgHours(0,Name,Year,Month));
    }
}
