package com.android.visitlog;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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
    PeopleActivityFragment peopleActivityFragment;
    GroupsActivityFragment groupsActivityFragment;
    FragmentManager fragmentManager;

    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        bottomNavigationView.getMenu().getItem(1).setChecked(true);

        calendar = new CalendarFragment();
        peopleActivityFragment = new PeopleActivityFragment();
        groupsActivityFragment = new GroupsActivityFragment();

        fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().add(R.id.main_container, calendar, "calendar").commit();
        fragmentManager.beginTransaction().add(R.id.main_container,peopleActivityFragment,"people").hide(peopleActivityFragment).commit();
        fragmentManager.beginTransaction().add(R.id.main_container, groupsActivityFragment, "groups").hide(groupsActivityFragment).commit();

        active = calendar;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navCalendar:
                    fragmentManager.beginTransaction().hide(active).show(calendar).commit();
                    calendar.update();
                    calendar.updateDecorator();
                    active = calendar;

                    return true;
                case R.id.navPeoples:
                    fragmentManager.beginTransaction().hide(active).show(peopleActivityFragment).commit();
                    peopleActivityFragment.GetData(calendar.YEAR,calendar.MONTH,calendar.DAY);
                    peopleActivityFragment.updatePeople();
                    active = peopleActivityFragment;

                    setSupportActionBar(peopleActivityFragment.toolbar);
                    getSupportActionBar().setTitle(getResources().getString(R.string.People));
                    return true;
                case R.id.navGroups:
                    fragmentManager.beginTransaction().hide(active).show(groupsActivityFragment).commit();
                    active = groupsActivityFragment;

                    setSupportActionBar(groupsActivityFragment.toolbar);
                    getSupportActionBar().setTitle(getResources().getString(R.string.Groups));

                    return true;
            }
            return false;
        }
    };




}
