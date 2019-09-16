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

public class All_People extends AppCompatActivity {

    DBHelper db;
    EditText editText;
    Button addName;
    ArrayList<String>people;
    ListView lv;

    final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all__people);
        db = new DBHelper(this);
        people = new ArrayList<String>();
        editText = (EditText) findViewById(R.id.textName);
        addName = (Button) findViewById(R.id.add);
        lv = (ListView) findViewById(R.id.AllPeopleList);

        final SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = people.get(i);
                Main2Activity.people.add(new People(name,"-"));
                //InsertData(sqLiteDatabase);
                Log.d(LOG_TAG,name);
            }
        });

        FindAllPeople(sqLiteDatabase);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                people);
        lv.setAdapter(adapter);

        addName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Name = editText.getText().toString();

                ContentValues cv = new ContentValues();

                if (Name.equals("")) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Введите ФИО!",
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }
                else{
                    people.add(Name);
                    cv.put(db.FULL_NAME,Name);
                    long rowID = sqLiteDatabase.insert(db.PEOPLE,
                            null,
                            cv);
                    Log.d(LOG_TAG,
                            "row inserted, ID = " + rowID);

                    for(String s : people){
                        Log.d("Name",s);
                    }
                    lv.invalidateViews();
                    editText.setText("");
                }
                db.close();
        }
        });
    }

    //Проверка наличия имен в БД и их вывод на экран
    private void FindAllPeople(SQLiteDatabase sqLiteDatabase){
        Cursor c = sqLiteDatabase.query(db.PEOPLE,
                null,
                null,
                null,
                null,
                null,
                null);

        if(c.moveToFirst()){
            int idColIndex = c.getColumnIndex(db.KEY_ID);
            int nameColIndex = c.getColumnIndex(db.FULL_NAME);

            do {
                String Name = c.getString(nameColIndex);
                people.add(Name);
                // получаем значения по номерам столбцов и пишем все в лог
                Log.d(LOG_TAG,
                        "ID = " + c.getInt(idColIndex) +
                                ", name = " + c.getString(nameColIndex));
                // переход на следующую строку
                // а если следующей нет (текущая - последняя),
                // то false - выходим из цикла
            } while (c.moveToNext());
        }
        else
            Log.d(LOG_TAG, "0 rows");
        c.close();
    }

    private void InsertData(SQLiteDatabase sqLiteDatabase){
        ContentValues cv = new ContentValues();

        cv.put(db.YEAR,Main2Activity.YEAR);
        sqLiteDatabase.insert(db.DATA_PEOPLE ,
                null,
                cv);
        cv.put(db.MONTH,Main2Activity.MONTH);
        sqLiteDatabase.insert(db.DATA_PEOPLE ,
                null,
                cv);
        cv.put(db.DAY,Main2Activity.DAY);
        sqLiteDatabase.insert(db.DATA_PEOPLE ,
                null,
                cv);
    }
}
