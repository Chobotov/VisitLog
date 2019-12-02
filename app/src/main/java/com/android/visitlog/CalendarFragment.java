package com.android.visitlog;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;


public class CalendarFragment extends Fragment{

    public View v;
    private String commit;
    private RecyclerView recyclerView;
    private MaterialCalendarView mcv;
    private EventDecorator eventDecorator;
    private Button fab;
    private ItemAdapter adapter;
    public String YEAR, MONTH, DAY;
    private ArrayList<People> peopleList;
    private DBHelper dbHelper;

    private int whichTime;

    final String LOG_TAG = "myLogs";

    public CalendarFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_calendar, container, false);

        whichTime = 1;

        commit = "";

        final int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        final int minute = Calendar.getInstance().get(Calendar.MINUTE);

        dbHelper = new DBHelper(v.getContext());
        YEAR = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        MONTH = String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1);
        DAY = String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        fab = v.findViewById(R.id.floatingActionButton);

        mcv = v.findViewById(R.id.calendarView);
        recyclerView = v.findViewById(R.id.list);
        eventDecorator = new EventDecorator(getResources().getColor(R.color.colorAccent));
        eventDecorator.setDates(dbHelper.SelectAllNotEmptyDays(YEAR,MONTH));

        peopleList = new ArrayList<People>();

        FindIDPeopleByData();

        mcv.setDateSelected(CalendarDay.today(),true);
        mcv.addDecorator(eventDecorator);
        mcv.invalidateDecorators();

        adapter = new ItemAdapter(v.getContext(), peopleList, new ItemAdapter.ComeLeaveRemove() {
            @Override
            public void RemovePeopleData(People people, int position) {
                dbHelper.DeleteDataFromDataTable(people.Name,YEAR,MONTH,DAY);
                peopleList.remove(position);
                eventDecorator.setDates(dbHelper.SelectAllNotEmptyDays(YEAR,MONTH));
                mcv.invalidateDecorators();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void InsertTimeInData(People people) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if(whichTime == 1){
                            people.CameTime = hourOfDay + ":" + minute;
                            dbHelper.InsertCameTime(people.Name, people.CameTime, YEAR, MONTH, DAY);
                            whichTime = 2;
                        }
                        else{
                            people.LeaveTime = hourOfDay + ":" + minute;
                            dbHelper.InsertLeaveTime(people.Name, people.LeaveTime, YEAR, MONTH, DAY);
                            whichTime = 1;
                        }
                        adapter.notifyDataSetChanged();
                    }
                },hour,minute,android.text.format.DateFormat.is24HourFormat(getContext()));
                timePickerDialog.show();
            }

            @Override
            public void InsertCommentInData(People people) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setCancelable(true);

                View view1 = LayoutInflater.from(getContext()).inflate(R.layout.add_comment_alert, null);

                builder.setView(view1);

                EditText editText = view1.findViewById(R.id.text_edit_commentAlert);
                editText.setText(commit);

                builder.setPositiveButton(R.string.Add, (dialogInterface, i) -> {

                    if(editText.getText().toString().length() >= 30){
                        commit = editText.getText().toString();
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());

                        builder1.setCancelable(true);

                        builder1.setMessage("Text is more than 30 symbols!");
                        AlertDialog alertDialog = builder1.create();
                        alertDialog.show();
                    }
                    else{
                        commit = "";
                        people.commit = editText.getText().toString();
                        Log.d("commit",people.commit);
                        dbHelper.SetCommentThisPeople(people,YEAR,MONTH,DAY);
                        update();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void CheckTime(People people) {
                String id = dbHelper.GetIdByName(people.Name);

                SetTimeToPeople(id,people);
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PeopleActivity.class);
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
                eventDecorator.setDates(dbHelper.SelectAllNotEmptyDays(YEAR,MONTH));
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
        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        update();
        eventDecorator.setDates(dbHelper.SelectAllNotEmptyDays(YEAR,MONTH));
        mcv.invalidateDecorators();
    }

    public void update() {
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
                        + " FROM " + dbHelper.DATA_PEOPLE
                        + " WHERE " + dbHelper.YEAR + " = ?"
                        + " AND " + dbHelper.MONTH + " = ?"
                        + " AND " + dbHelper.DAY + " = ?"
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
                    + " WHERE " + dbHelper.KEY_ID
                    + " = ?", new String[]{s});
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex(dbHelper.FULL_NAME));
                    People people = new People(name);
                    people.Group = dbHelper.FindGroupThisPeople(people);
                    people.commit = dbHelper.GetCommentToPeople(people,YEAR,MONTH,DAY);
                    peopleList.add(people);
                } while (cursor.moveToNext());
            } else
                Log.d(LOG_TAG, "Cursor is null");
        }
        sqLiteDatabase.close();
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
