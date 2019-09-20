package com.android.visitlog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;

public class Main2Activity extends AppCompatActivity{

    CalendarView calendarView;
    FloatingActionButton fab;
    public int YEAR,MONTH,DAY;
    public static ListView productList;
    public static ArrayList<People> people = new ArrayList<People>();

    final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        YEAR = Calendar.getInstance().get(Calendar.YEAR);
        MONTH = Calendar.getInstance().get(Calendar.MONTH)+1;
        DAY = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        Log.d("Date",YEAR + " " + MONTH + " " + DAY );
        fab = findViewById(R.id.floatingActionButton);
        calendarView = findViewById(R.id.calendarView);

        FindIDPeopleByData();
        productList = findViewById(R.id.peopleList);
        final ItemAdapter adapter = new ItemAdapter(this, R.layout.list_item, people);
        productList.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main2Activity.this,All_People.class);
                intent.putExtra("year",String.valueOf(YEAR));
                intent.putExtra("month",String.valueOf(MONTH));
                intent.putExtra("day",String.valueOf(DAY));
                startActivity(intent);
            }
        });
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                YEAR = year;
                MONTH = month+1;
                DAY = dayOfMonth;
                FindIDPeopleByData();
                adapter.notifyDataSetChanged();
                //Log.d("Date",dayOfMonth + " " + month + " " + year );
            }
        });

    }

    private void FindIDPeopleByData(){
        people.clear();
        ArrayList<String> id = new ArrayList<>();
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor c = sqLiteDatabase.rawQuery("SELECT " + dbHelper.ID_PEOPLE
                +" FROM " + dbHelper.DATA_PEOPLE +
                " WHERE " + dbHelper.YEAR + " = ?" +
                " AND " + dbHelper.MONTH + " = ?" +
                " AND " + dbHelper.DAY + " = ?"
                ,new String[]{String.valueOf(YEAR),String.valueOf(MONTH),String.valueOf(DAY)});
        if (c.moveToFirst()) {
            do {
                String index = c.getString(c.getColumnIndex(dbHelper.ID_PEOPLE));
                id.add(index);
            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG, "Cursor is null");
        c.close();
        FindNamePeopleById(id);
        c.close();
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
                    people.add(new People(name,"-"));
                }while(cursor.moveToNext());
            }
            else
                Log.d(LOG_TAG,"Cursor is null");
        }
    }
}
