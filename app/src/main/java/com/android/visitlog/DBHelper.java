package com.android.visitlog;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {

    public static  final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "mydb";
    public static final  String TABLE_PEOPLE = "people";
    public static final String TABLE_DATA = "data";

    public static String KEY_ID = "_id";
    public static String FULL_NAME = "fullName";

    public static String DATA = "data";
    public static String CAME_TIME = "cameTime";
    public static String OUT_TIME = "outTime";
    public static String ISHERE = "isHere";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_PEOPLE + "(" + KEY_ID
        + " integer primary key," + FULL_NAME + " text" + ")");

        db.execSQL("create table " + TABLE_DATA + "(" + KEY_ID
        + " integer primary key," + DATA + " text," + CAME_TIME + " text,"
        + OUT_TIME + " text," + ISHERE + " numeric" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_DATA);
        db.execSQL("drop table if exists " + TABLE_PEOPLE);

        onCreate(db);
    }
}
