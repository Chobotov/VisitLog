package com.android.visitlog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

public class PeopleInformation extends AppCompatActivity {

    WeekInfoFragment weekInfo;
    FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_information);

        weekInfo = new WeekInfoFragment();

        fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().add(R.id.info_contanier,weekInfo,"week").show(weekInfo).commit();


    }
}
