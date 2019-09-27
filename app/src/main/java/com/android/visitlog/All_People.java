package com.android.visitlog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class All_People extends AppCompatActivity{

    DBHelper dbHelper;
    EditText editText;
    ArrayList<String>people;
    ListView lv;
    String year;
    String month;
    String day;
    private Button addName;

    final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all__people);
        dbHelper = new DBHelper(this);
        people = new ArrayList<String>();
        editText = findViewById(R.id.textName);
        lv = findViewById(R.id.AllPeopleList);
        addName = findViewById(R.id.addNewName);

        Intent intent = getIntent();

        year = intent.getStringExtra("year");
        month = intent.getStringExtra("month");
        day = intent.getStringExtra("day");


        final SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                people);
        lv.setAdapter(adapter);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = people.get(i);
                dbHelper.SetDataInDataTable(name,year,month,day);
                adapter.notifyDataSetChanged();
                Intent intent = new Intent(All_People.this, MainActivity.class);
                startActivity(intent);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String name = people.get(position);
                people.remove(position);
                dbHelper.removePeople(name);
                //dbHelper.removePeople(dbHelper,name);
                adapter.notifyDataSetChanged();
                return true;
            }
        });

        addName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name = editText.getText().toString();

                if (Name.equals("")) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Введите ФИО!",
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }
                else{
                    people.add(Name);
                   // dbHelper.SetNewName(dbHelper,Name);
                    adapter.notifyDataSetChanged();
                    editText.setText("");
                }
                dbHelper.close();
            }
        });

        //InsertFullNamesIntoList(dbHelper, sqLiteDatabase, people);
    }



}
