package com.android.visitlog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity{

    CalendarView calendarView;
    FloatingActionButton fab;
    ItemAdapter adapter;
    public String YEAR,MONTH,DAY;
    public static ArrayList<People> peopleList=new ArrayList<People>();

    final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        YEAR = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        MONTH = String.valueOf(Calendar.getInstance().get(Calendar.MONTH)+1);
        DAY = String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        fab = findViewById(R.id.floatingActionButton);
        calendarView = findViewById(R.id.calendarView);

        FindIDPeopleByData();

        RecyclerView recyclerView = findViewById(R.id.list);

        adapter = new ItemAdapter(this, peopleList, new ItemAdapter.ComeLeaveRemove() {
            @Override
            public void RemovePeopleData(People people,int position) {
                DBHelper dbHelper = new DBHelper(MainActivity.this);
                dbHelper.DeleteDataFromDataTable(people.Name);
                peopleList.remove(position);
                adapter.notifyDataSetChanged();
                Log.d("delete",people.Name);
            }

            @Override
            public void InsertComeTimeInData(People people) {
                DBHelper dbHelper = new DBHelper(MainActivity.this);
                DateFormat df = new SimpleDateFormat("HH:mm");
                String time = df.format(Calendar.getInstance().getTime());
                people.CameTime = time;
                dbHelper.InsertComeTime(people.Name,time,YEAR,MONTH,DAY);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void InsertLeaveTimeInData(People people) {
                DBHelper dbHelper = new DBHelper(MainActivity.this);
                DateFormat df = new SimpleDateFormat("HH:mm");
                String time = df.format(Calendar.getInstance().getTime());
                people.LeaveTime = time;
                dbHelper.InsertLeaveTime(people.Name,time,YEAR,MONTH,DAY);
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
                        + "AND " +  dbHelper.MONTH + "=? "
                        + "AND " +  dbHelper.DAY + "=? ",new String[]{id,YEAR,MONTH,DAY});
                if(c.moveToFirst()){
                    int index = c.getColumnIndex(DBHelper.CAME_TIME);
                    do {
                        people.CameTime=(c.getString(index));
                        Log.d("people:","Came " + people.CameTime +" Leave "+people.LeaveTime);
                    }while (c.moveToNext());
                }

                c = sqLiteDatabase.rawQuery("SELECT " + dbHelper.LEAVE_TIME
                        + " FROM " + dbHelper.DATA_PEOPLE
                        + " WHERE "
                        + dbHelper.ID_PEOPLE + "=? "
                        + "AND " + dbHelper.YEAR + "=? "
                        + "AND " +  dbHelper.MONTH + "=? "
                        + "AND " +  dbHelper.DAY + "=? ",new String[]{id,YEAR,MONTH,DAY});
                if(c.moveToFirst()){
                    int index = c.getColumnIndex(DBHelper.LEAVE_TIME);
                    do {
                        people.LeaveTime=(c.getString(index));
                        Log.d("people:","Came " + people.CameTime +" Leave "+people.LeaveTime);
                    }while (c.moveToNext());
                }
            }
        });


        recyclerView.setAdapter(adapter);



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AddPeopleActivity.class);
                intent.putExtra("year",String.valueOf(YEAR));
                intent.putExtra("month",String.valueOf(MONTH));
                intent.putExtra("day",String.valueOf(DAY));
                ReloadActivity();
                startActivity(intent);
            }
        });
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                YEAR = String.valueOf(year);
                MONTH = String.valueOf(month+1);
                DAY = String.valueOf(dayOfMonth);
                FindIDPeopleByData();
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void onStart() {

        super.onStart();
        FindIDPeopleByData();
    }

    private void ReloadActivity(){
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private void FindIDPeopleByData(){
        peopleList.clear();
        ArrayList<String> id = new ArrayList<>();
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor c = sqLiteDatabase.rawQuery("SELECT " + dbHelper.ID_PEOPLE
                +" FROM " + dbHelper.DATA_PEOPLE +
                " WHERE " + dbHelper.YEAR + " = ?" +
                " AND " + dbHelper.MONTH + " = ?" +
                " AND " + dbHelper.DAY + " = ?"
                ,new String[]{YEAR,MONTH,DAY});
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
        DBHelper dbHelper =  new DBHelper(this);

        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();

        for(String s : id)
        {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + dbHelper.FULL_NAME
                    + " FROM " + dbHelper.PEOPLE
                    + " WHERE " + dbHelper.KEY_ID + " = ?",new String[]{s});
            if(cursor.moveToFirst())
            {
                do{
                    String name = cursor.getString(cursor.getColumnIndex(dbHelper.FULL_NAME));
                    peopleList.add(new People(name));
                }while(cursor.moveToNext());
            }
            else
                Log.d(LOG_TAG,"Cursor is null");
        }
        sqLiteDatabase.close();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null)
            adapter.notifyDataSetChanged();
    }
}
