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

    private RecyclerView recyclerView;
    private MaterialCalendarView mcv;
    private EventDecorator eventDecorator;
    private FloatingActionButton fab;
    private ItemAdapter adapter;
    private String YEAR, MONTH, DAY;
    private ArrayList<People> peopleList;
    private DBHelper dbHelper;

    final String LOG_TAG = "myLogs";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DBHelper(this);
        YEAR = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        MONTH = String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1);
        DAY = String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        fab = findViewById(R.id.floatingActionButton);
        mcv = findViewById(R.id.calendarView);
        recyclerView = findViewById(R.id.list);
        eventDecorator = new EventDecorator(Color.BLUE);
        eventDecorator.setDates(SelectNotEmptyDays());

        peopleList = new ArrayList<People>();

        FindIDPeopleByData();

        mcv.setDateSelected(CalendarDay.today(),true);
        mcv.addDecorator(eventDecorator);
        mcv.invalidateDecorators();
        
        adapter = new ItemAdapter(this, peopleList, new ItemAdapter.ComeLeaveRemove() {
            @Override
            public void RemovePeopleData(People people, int position) {
                dbHelper.DeleteDataFromDataTable(people.Name,YEAR,MONTH,DAY);
                peopleList.remove(position);
                eventDecorator.setDates(SelectNotEmptyDays());
                mcv.invalidateDecorators();
                adapter.notifyDataSetChanged();
                //Log.d("delete", people.Name);
            }

            @Override
            public void InsertCameTimeInData(People people) {
                DateFormat df = new SimpleDateFormat("HH:mm");
                String time = df.format(Calendar.getInstance().getTime());
                people.CameTime = time;
                dbHelper.InsertCameTime(people.Name, time, YEAR, MONTH, DAY);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void InsertLeaveTimeInData(People people) {
                DateFormat df = new SimpleDateFormat("HH:mm");
                String time = df.format(Calendar.getInstance().getTime());
                people.LeaveTime = time;
                dbHelper.InsertLeaveTime(people.Name, time, YEAR, MONTH, DAY);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void CheckTime(People people) {
                SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
                String id = dbHelper.GetIdByName(people.Name);

                SetTimeToPeople(id,people);

                SetTimeToPeople(id,people);

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
                //update();
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
                update();
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
        {
            adapter.notifyDataSetChanged();
        }
    }


    private void FindIDPeopleByData() {
        peopleList.clear();
        ArrayList<String> id = new ArrayList<>();
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
        sqLiteDatabase.close();
    }

    private void FindNamePeopleById(ArrayList<String> id) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor;

        for (String s : id) {
            cursor = sqLiteDatabase.rawQuery("SELECT " + dbHelper.FULL_NAME
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
        ArrayList<Integer> days = new ArrayList<>();
        days = dbHelper.getDaysOfMonth(YEAR,MONTH);

        ArrayList<CalendarDay> cd = new ArrayList<>();
        for (int day : days) {
            cd.add(CalendarDay.from(Integer.valueOf(YEAR),Integer.valueOf(MONTH), day));
        }
        return cd;
    }

    private void SetTimeToPeople(String id,People people){
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor;

        cursor = sqLiteDatabase.rawQuery("SELECT " + dbHelper.CAME_TIME
                + " FROM " + dbHelper.DATA_PEOPLE
                + " WHERE "
                + dbHelper.ID_PEOPLE + "=? "
                + "AND " + dbHelper.YEAR + "=? "
                + "AND " + dbHelper.MONTH + "=? "
                + "AND " + dbHelper.DAY + "=? ", new String[]{id, YEAR, MONTH, DAY});
        if (cursor.moveToFirst()) {
            int index = cursor.getColumnIndex(dbHelper.CAME_TIME);
            do {
                people.CameTime = (cursor.getString(index));
                //Log.d("people:", "Came " + people.CameTime + " Leave " + people.LeaveTime);
            } while (cursor.moveToNext());
        }

        cursor = sqLiteDatabase.rawQuery("SELECT " + dbHelper.LEAVE_TIME
                + " FROM " + dbHelper.DATA_PEOPLE
                + " WHERE "
                + dbHelper.ID_PEOPLE + "=? "
                + "AND " + dbHelper.YEAR + "=? "
                + "AND " + dbHelper.MONTH + "=? "
                + "AND " + dbHelper.DAY + "=? ", new String[]{id, YEAR, MONTH, DAY});
        if (cursor.moveToFirst()) {
            int index = cursor.getColumnIndex(dbHelper.LEAVE_TIME);
            do {
                people.LeaveTime = (cursor.getString(index));
                //Log.d("people:", "Came " + people.CameTime + " Leave " + people.LeaveTime);
            } while (cursor.moveToNext());
        }
            cursor.close();
            sqLiteDatabase.close();
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
