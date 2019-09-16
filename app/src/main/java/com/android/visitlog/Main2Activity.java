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
    public static int YEAR,MONTH,DAY;
    public static ListView productList;
    public static ArrayList<People> people = new ArrayList<People>();

    final String LOG_TAG = "myLogs";

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
                YEAR = year;
                MONTH = month;
                DAY = dayOfMonth;
                Log.d("Date",dayOfMonth + " " + month + " " + year );
            }
        });
        FindPeopleByData();
        productList = (ListView) findViewById(R.id.peopleList);
        ItemAdapter adapter = new ItemAdapter(this, R.layout.list_item, people);
        productList.setAdapter(adapter);

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(Main2Activity.this,All_People.class);
        startActivity(intent);
    }

    private void FindPeopleByData(){
        DBHelper db = new DBHelper(this);
        final SQLiteDatabase sqLiteDatabase = db.getReadableDatabase();

        Cursor c = sqLiteDatabase.rawQuery("SELECT " + db.ID_PEOPLE
                +" FROM " + db.DATA_PEOPLE +
                " WHERE " + db.YEAR + " = ?" +
                " AND " + db.MONTH + " = ? " +
                " AND " + db.DAY + " = ?"
                ,new String[]{String.valueOf(YEAR),String.valueOf(MONTH),String.valueOf(DAY)});
        logCursor(c);
        c.close();
        db.close();
    }

   private void logCursor(Cursor c) {
        if (c != null) {
            if (c.moveToFirst()) {
                String str;
                do {
                    str = "";
                    for (String cn : c.getColumnNames()) {
                        str = str.concat(cn + " = " + c.getString(c.getColumnIndex(cn)) + "; ");
                    }
                    Log.d(LOG_TAG, str);
                } while (c.moveToNext());
            }
        } else
            Log.d(LOG_TAG, "Cursor is null");
    }

}
