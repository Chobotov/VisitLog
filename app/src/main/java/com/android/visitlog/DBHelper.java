package com.android.visitlog;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {

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
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + PEOPLE);
        db.execSQL("drop table if exists " + DATA_PEOPLE);
        db.execSQL("drop table if exists " + PEOPLES_GROUP);
        db.execSQL("drop table if exists " + GROUPS);
        onCreate(db);
    }
}
