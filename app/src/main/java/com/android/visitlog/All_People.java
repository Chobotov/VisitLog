package com.android.visitlog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class All_People extends AppCompatActivity{

    DBHelper db;
    EditText editText;
    ArrayList<String>people;
    ListView lv;
    private Button addName;

    final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all__people);
        db = new DBHelper(this);
        people = new ArrayList<String>();
        editText = (EditText) findViewById(R.id.textName);
        lv = (ListView) findViewById(R.id.AllPeopleList);
        addName = findViewById(R.id.addNewName);

        final SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                people);
        lv.setAdapter(adapter);

        db.ReadAllTable(db);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(LOG_TAG,String.valueOf(i));
                String name = people.get(i);
                people.remove(i);
                db.DeleteNameFromPeopleTable(db, name);
                db.ReadAllTable(db);
                adapter.notifyDataSetChanged();
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
                    db.SetNewName(db,Name);
                    adapter.notifyDataSetChanged();
                    editText.setText("");
                }
                db.close();
            }
        });

        InsertFullNamesIntoList(db, sqLiteDatabase, people);
    }


    private void InsertFullNamesIntoList(DBHelper dbHelper,SQLiteDatabase db, ArrayList<String> people){
        Cursor cursor = db.rawQuery("SELECT " + dbHelper.FULL_NAME +" FROM " + dbHelper.PEOPLE,null);
        people.clear();
        while (cursor.moveToNext()){
            String Name = cursor.getString(cursor.getColumnIndex(dbHelper.FULL_NAME));
            people.add(Name);
        }
        cursor.close();
    }
}
