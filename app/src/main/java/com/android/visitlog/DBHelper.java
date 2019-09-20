package com.android.visitlog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = "MyLogs";

    public static  final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "mydb";

    public static final String DATA_PEOPLE = "data";
    public static final String PEOPLE = "people";
    public static final String PEOPLES_GROUP = "peoplesGroup";
    public static final String GROUPS = "groups";

    public static String KEY_ID = "_id";
    public static String FULL_NAME = "fullName";
    public static String GROUP_NAME = "groupName";
    public static String ID_GROUP = "idGroup";
    public static String ID_PEOPLE = "idPeople";

    public static String YEAR = "year";
    public static String MONTH = "month";
    public static String DAY = "day";
    public static String CAME_TIME = "cameTime";
    public static String OUT_TIME = "outTime";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + PEOPLE +
                "(" +
                KEY_ID + " integer primary key," +
                FULL_NAME + " text" +
                ")");

        db.execSQL("create table " + DATA_PEOPLE +
                "(" +
                ID_PEOPLE + " integer," +
                YEAR + " integer," +
                MONTH + " integer," +
                DAY + " integer," +
                CAME_TIME + " numeric," +
                OUT_TIME + " numeric," +
                "FOREIGN KEY (" + ID_PEOPLE + ") REFERENCES " +
                PEOPLE + " (" + KEY_ID + ")" +
                ")");

        db.execSQL("create table " + PEOPLES_GROUP +
                "(" +
                ID_PEOPLE + " integer,"+
                ID_GROUP + " integer," +
                "FOREIGN KEY (" + ID_PEOPLE + ") REFERENCES " +
                PEOPLE + " (" + KEY_ID + ")," +
                "FOREIGN KEY (" + ID_GROUP + ") REFERENCES " +
                GROUPS + " ( " + KEY_ID + ")" +
                ")");

        db.execSQL("create table " + GROUPS +
                "(" +
                KEY_ID + " integer primary key," +
                GROUP_NAME + " text" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("drop table if exists " + PEOPLE);
        sqLiteDatabase.execSQL("drop table if exists " + DATA_PEOPLE);
        sqLiteDatabase.execSQL("drop table if exists " + PEOPLES_GROUP);
        sqLiteDatabase.execSQL("drop table if exists " + GROUPS);
        onCreate(sqLiteDatabase);
    }

    //Запись нового имени
    public void SetNewName(DBHelper dbHelper, String name){
        ContentValues cv = new ContentValues();

        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

        cv.put(dbHelper.FULL_NAME,name);
        sqLiteDatabase.insert(dbHelper.PEOPLE, null, cv);
    }

    public String GetIdByName(DBHelper dbHelper, String name){
        String id = "0";

        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + dbHelper.KEY_ID +
                " FROM " + dbHelper.PEOPLE +
                " WHERE " + dbHelper.FULL_NAME +
                " = ?",new String[]{name});
        if(cursor.moveToFirst())
        {
            int index = cursor.getColumnIndex(dbHelper.KEY_ID);
            do {
                id = cursor.getString(index);
            }while (cursor.moveToNext());
        }
        else
            cursor.close();

        Log.d(LOG_TAG,id);

        return id;
    }

    public void SetDataInDataTable(DBHelper dbHelper,String name,String YEAR,String MONTH,String DAY){
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();

        String id = GetIdByName(dbHelper,name);

        //Log.d("idbyName",id);

        cv.put(dbHelper.ID_PEOPLE,id);
        cv.put(dbHelper.YEAR,YEAR);
        cv.put(dbHelper.MONTH,MONTH);
        cv.put(dbHelper.DAY,DAY);

        sqLiteDatabase.insert(dbHelper.DATA_PEOPLE,null,cv);
    }

    public void ReadAllTable(DBHelper dbHelper){
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query(DBHelper.PEOPLE,null,null,null,null,null,null);

        if(cursor.moveToFirst()) {
            int nameindex = cursor.getColumnIndex(DBHelper.FULL_NAME);
            do {
                //Log.d(LOG_TAG, "name = " + cursor.getString(nameindex));
            } while (cursor.moveToNext());
        }
        else
           //Log.d(LOG_TAG,"0 rows");
        cursor.close();
    }

    //Удаление имени из таблиц
    public void DeleteNameFromPeopleTable(DBHelper dbHelper, String name){
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

        sqLiteDatabase.delete(dbHelper.PEOPLE,dbHelper.FULL_NAME + " = ?" , new String[]{name});
    }

    public void DeleteDataByName(DBHelper dbHelper,String Name){

        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

        String id = dbHelper.GetIdByName(dbHelper,Name);

        sqLiteDatabase.delete(dbHelper.DATA_PEOPLE,
                dbHelper.ID_PEOPLE + "= ?",
                new String[]{id});

    }
}
