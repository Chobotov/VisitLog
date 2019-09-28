package com.android.visitlog;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import com.google.android.material.datepicker.MaterialCalendar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    MaterialCalendarView mcv;
    EventDecorator eventDecorator;
    FloatingActionButton fab;
    ItemAdapter adapter;
    public String YEAR, MONTH, DAY;
    public ArrayList<People> peopleList;

    final String LOG_TAG = "myLogs";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        YEAR = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        MONTH = String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1);
        DAY = String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        fab = findViewById(R.id.floatingActionButton);

        eventDecorator = new EventDecorator(Color.BLUE);
        eventDecorator.setDates(SelectNotEmptyDays());
        mcv = findViewById(R.id.calendarView);

        mcv.setDateSelected(CalendarDay.today(),true);

        peopleList = new ArrayList<People>();
        FindIDPeopleByData();

        RecyclerView recyclerView = findViewById(R.id.list);

        mcv.addDecorator(eventDecorator);
        mcv.invalidateDecorators();

        adapter = new ItemAdapter(this, peopleList, new ItemAdapter.ComeLeaveRemove() {
            @Override
            public void RemovePeopleData(People people, int position) {
                DBHelper dbHelper = new DBHelper(MainActivity.this);
                dbHelper.DeleteDataFromDataTable(people.Name,YEAR,MONTH,DAY);
                peopleList.remove(position);
                adapter.notifyDataSetChanged();
                eventDecorator.setDates(SelectNotEmptyDays());
                mcv.invalidateDecorators();
                Log.d("delete", people.Name);
            }

            @Override
            public void InsertComeTimeInData(People people) {
                DBHelper dbHelper = new DBHelper(MainActivity.this);
                DateFormat df = new SimpleDateFormat("HH:mm");
                String time = df.format(Calendar.getInstance().getTime());
                people.CameTime = time;
                dbHelper.InsertComeTime(people.Name, time, YEAR, MONTH, DAY);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void InsertLeaveTimeInData(People people) {
                DBHelper dbHelper = new DBHelper(MainActivity.this);
                DateFormat df = new SimpleDateFormat("HH:mm");
                String time = df.format(Calendar.getInstance().getTime());
                people.LeaveTime = time;
                dbHelper.InsertLeaveTime(people.Name, time, YEAR, MONTH, DAY);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void CheckTime(People people) {
                DBHelper dbHelper = new DBHelper(MainActivity.this);
                SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
                String id = dbHelper.GetIdByName(people.Name);
                Cursor c = sqLiteDatabase.rawQuery("SELECT " + dbHelper.CAME_TIME
                        + " FROM " + dbHelper.DATA_PEOPLE
                        + " WHERE "
                        + dbHelper.ID_PEOPLE + "=? "
                        + "AND " + dbHelper.YEAR + "=? "
                        + "AND " + dbHelper.MONTH + "=? "
                        + "AND " + dbHelper.DAY + "=? ", new String[]{id, YEAR, MONTH, DAY});
                if (c.moveToFirst()) {
                    int index = c.getColumnIndex(DBHelper.CAME_TIME);
                    do {
                        people.CameTime = (c.getString(index));
                        //Log.d("people:", "Came " + people.CameTime + " Leave " + people.LeaveTime);
                    } while (c.moveToNext());
                }

                c = sqLiteDatabase.rawQuery("SELECT " + dbHelper.LEAVE_TIME
                        + " FROM " + dbHelper.DATA_PEOPLE
                        + " WHERE "
                        + dbHelper.ID_PEOPLE + "=? "
                        + "AND " + dbHelper.YEAR + "=? "
                        + "AND " + dbHelper.MONTH + "=? "
                        + "AND " + dbHelper.DAY + "=? ", new String[]{id, YEAR, MONTH, DAY});
                if (c.moveToFirst()) {
                    int index = c.getColumnIndex(DBHelper.LEAVE_TIME);
                    do {
                        people.LeaveTime = (c.getString(index));
                        //Log.d("people:", "Came " + people.CameTime + " Leave " + people.LeaveTime);
                    } while (c.moveToNext());
                }
            }
        });


        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddPeopleActivity.class);
                intent.putExtra("year", String.valueOf(YEAR));
                intent.putExtra("month", String.valueOf(MONTH));
                intent.putExtra("day", String.valueOf(DAY));
                startActivity(intent);
                //ReloadActivity();
                update();
            }
        });

        mcv.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                MONTH = String.valueOf(date.getMonth());
                eventDecorator.setDates(SelectNotEmptyDays());
                mcv.invalidateDecorators();
            }
        });

        mcv.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                YEAR = String.valueOf(date.getYear());
                MONTH = String.valueOf(date.getMonth());
                DAY = String.valueOf(date.getDay());
                FindIDPeopleByData();
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        update();
        eventDecorator.setDates(SelectNotEmptyDays());
        mcv.invalidateDecorators();
    }

    private void update() {
        FindIDPeopleByData();
        if (adapter != null)
            adapter.notifyDataSetChanged();

    }


    private void FindIDPeopleByData() {
        peopleList.clear();
        ArrayList<String> id = new ArrayList<>();
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor c = sqLiteDatabase.rawQuery("SELECT " + dbHelper.ID_PEOPLE
                        + " FROM " + dbHelper.DATA_PEOPLE +
                        " WHERE " + dbHelper.YEAR + " = ?" +
                        " AND " + dbHelper.MONTH + " = ?" +
                        " AND " + dbHelper.DAY + " = ?"
                , new String[]{YEAR, MONTH, DAY});
        if (c.moveToFirst()) {
            do {
                String index = c.getString(c.getColumnIndex(dbHelper.ID_PEOPLE));
                id.add(index);
            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG, "Cursor is null");
        c.close();
        FindNamePeopleById(id);
        dbHelper.close();
    }

    private void FindNamePeopleById(ArrayList<String> id) {
        DBHelper dbHelper = new DBHelper(this);

        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();

        for (String s : id) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + dbHelper.FULL_NAME
                    + " FROM " + dbHelper.PEOPLE
                    + " WHERE " + dbHelper.KEY_ID + " = ?", new String[]{s});
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex(dbHelper.FULL_NAME));
                    peopleList.add(new People(name));
                } while (cursor.moveToNext());
            } else
                Log.d(LOG_TAG, "Cursor is null");
        }
        sqLiteDatabase.close();
    }

    private ArrayList<CalendarDay> SelectNotEmptyDays(){
        DBHelper dbHelper = new DBHelper(this);
        ArrayList<Integer> days = new ArrayList<>();
        days = dbHelper.getDaysOfMonth(YEAR,MONTH);

        ArrayList<CalendarDay>cd = new ArrayList<>();
        for (int day : days) {
            cd.add(CalendarDay.from(Integer.valueOf(YEAR),Integer.valueOf(MONTH), day));
        }
        dbHelper.close();
        return cd;
    }
    public class EventDecorator implements DayViewDecorator {

        private final int color;
        private HashSet<CalendarDay> dates;

        public EventDecorator(int color) {
            this.color = color;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new DotSpan(5, color));
        }

        public void setDates(Collection<CalendarDay> dates){
            this.dates = new HashSet<>(dates);
        }
    }

}
