package com.android.visitlog;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment active;
    CalendarFragment calendar;
    PeopleFragment peopleFragment;
    GroupsFragment groupsFragment;
    FragmentManager fragmentManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        calendar = new CalendarFragment();
        //peopleFragment = new PeopleFragment();
        groupsFragment = new GroupsFragment();

        fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().add(R.id.main_container, calendar, "calendar").commit();
//      fragmentManager.beginTransaction().add(R.id.main_container,peopleFragment,"people").hide(peopleFragment).commit();
        fragmentManager.beginTransaction().add(R.id.main_container, groupsFragment, "groups").hide(groupsFragment).commit();

        active = calendar;

    }

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navCalendar:
                    fragmentManager.beginTransaction().hide(active).show(calendar).commit();
                    active = calendar;
                    return true;
                case R.id.navPeoples:
                    // fragmentManager.beginTransaction().hide(active).show(peopleFragment).commit();
                    // active = peopleFragment;
                    return true;
                case R.id.navGroups:
                    fragmentManager.beginTransaction().hide(active).show(groupsFragment).commit();
                    active = groupsFragment;
                    return true;
            }
            return false;
        }
    };
}
