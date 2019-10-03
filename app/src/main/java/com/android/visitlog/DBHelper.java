package com.android.visitlog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.Collection;


public class DBHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = "MyLogs";

    private static  final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "mydb";

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
    public static String LEAVE_TIME = "leaveTime";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + PEOPLE
                + "("
                + KEY_ID + " integer primary key,"
                + FULL_NAME + " text"
                + ")");

        db.execSQL("create table " + DATA_PEOPLE
                + "("
                + ID_PEOPLE + " integer,"
                + YEAR + " integer,"
                + MONTH + " integer,"
                + DAY + " integer,"
                + CAME_TIME + " text,"
                + LEAVE_TIME + " text,"
                + "FOREIGN KEY (" + ID_PEOPLE + ") REFERENCES "
                + PEOPLE + " (" + KEY_ID + ")"
                + ")");

        db.execSQL("create table " + PEOPLES_GROUP +
                "(" +
                ID_PEOPLE + " integer,"
                + ID_GROUP + " integer,"
                + "FOREIGN KEY (" + ID_PEOPLE + ") REFERENCES "
                + PEOPLE + " (" + KEY_ID + "),"
                + "FOREIGN KEY (" + ID_GROUP + ") REFERENCES "
                + GROUPS + " ( " + KEY_ID + ")"
                + ")");

        db.execSQL("create table " + GROUPS +
                "("
                + KEY_ID + " integer primary key,"
                + GROUP_NAME + " text"
                +
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
    public void addPeople(String name){
        ContentValues cv = new ContentValues();

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        cv.put(FULL_NAME,name);
        sqLiteDatabase.insert(PEOPLE, null, cv);

    }

    public String GetNameByID(String id){
        String name = "null";

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery(
                "SELECT "
                        + FULL_NAME
                        + " FROM "
                        + PEOPLE
                        + " WHERE "
                        + KEY_ID
                        +" =?",
                new String[]{id});
        if(cursor.moveToFirst()){
            int index = cursor.getColumnIndex(FULL_NAME);
            do {
               name = cursor.getString(index);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return name;
    }

    public String GetGroupByID(String id){
        String group = "null";

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery(
                "SELECT "
                        + GROUP_NAME
                        + " FROM "
                        + GROUPS
                        + " WHERE "
                        + ID_GROUP
                        +" =?",
                new String[]{id});
        if(cursor.moveToFirst()){
            int index = cursor.getColumnIndex(FULL_NAME);
            do {
                group = cursor.getString(index);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return group;
    }

    //Получить id человека по имени
    public String GetIdByName( String name){
        String id = "0";

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT "
                + KEY_ID
                + " FROM " + PEOPLE
                + " WHERE " + FULL_NAME
                + " = ?",new String[]{name});
        if(cursor.moveToFirst())
        {
            int index = cursor.getColumnIndex(KEY_ID);
            do {
                id = cursor.getString(index);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return id;
    }

    //Получить id группы по названию
    public String GetIdGroupByName(String name){
        String id = "0";

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT "
                + KEY_ID
                + " FROM " + GROUPS
                + " WHERE " + GROUP_NAME
                + " = ?",new String[]{name});
        if(cursor.moveToFirst())
        {
            int index = cursor.getColumnIndex(KEY_ID);
            do {
                id = cursor.getString(index);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return id;
    }

    //Запись новой строки с датой в таблицу Data
    public void SetDataInDataTable(String name,String YEAR,String MONTH,String DAY){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ContentValues cv = new ContentValues();

        String id = GetIdByName(name);

        cv.put(ID_PEOPLE,id);
        cv.put(this.YEAR,YEAR);
        cv.put(this.MONTH,MONTH);
        cv.put(this.DAY,DAY);
        cv.put(CAME_TIME,"Пришел");
        cv.put(LEAVE_TIME,"Ушел");

        sqLiteDatabase.insert(DATA_PEOPLE,null,cv);
    }

    //Удаление имени из таблицы PEOPLE
    public void removePeople(String name){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(PEOPLE,FULL_NAME + " = ?" , new String[]{name});
    }

    //Удаление имени из таблицы GROUPS
    public void removeGroup(String name){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(GROUPS,GROUP_NAME + " = ?" , new String[]{name});
    }

    //Удаление даты из таблицы DATA_PEOPLE
    public void DeleteDataFromDataTable(String Name,String year,String month,String day){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        String id  = GetIdByName(Name);

        sqLiteDatabase.delete(DATA_PEOPLE,
                 ID_PEOPLE + "= ? "
                + " AND " + YEAR + " = ? "
                + " AND " + MONTH + " = ? "
                + " AND " + DAY + " = ? ", new String[]{id,year,month,day});

    }

    //Удаление всех дат выбранного человека
    public void DeleteAllDataFromDataTable(String Name){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        String id  = GetIdByName(Name);

        sqLiteDatabase.delete(DATA_PEOPLE,
                ID_PEOPLE + "= ? "
                       , new String[]{id});

    }

    //Добавление время Пришел в DATA_PEOPLE
    public void InsertCameTime(String name,String time,String year,String month,String day){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        String id = GetIdByName(name);

        ContentValues cv = new ContentValues();

        cv.put(CAME_TIME,time);

        sqLiteDatabase.update(DATA_PEOPLE,cv,ID_PEOPLE
                + "=? "
                + "AND " + YEAR + "=? "
                + "AND " +  MONTH + "=? "
                + "AND " +  DAY + "=? ",new String[]{id,year,month,day});

    }

    //Добавление время Ушел в DATA_PEOPLE
    public void InsertLeaveTime(String name,String time,String year,String month,String day){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        String id = GetIdByName(name);

        ContentValues cv = new ContentValues();

        cv.put(LEAVE_TIME,time);

        sqLiteDatabase.update(DATA_PEOPLE,cv,ID_PEOPLE
                + "=? "
                + "AND " + YEAR + "=? "
                + "AND " +  MONTH + "=? "
                + "AND " +  DAY + "=? ",new String[]{id,year,month,day});

    }

    public ArrayList<People> getAllPeople(){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        ArrayList<People> people = new ArrayList<>();

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT " 
            + FULL_NAME +" FROM " 
            + PEOPLE,null);
        while (cursor.moveToNext()){
            String Name = cursor.getString(cursor.getColumnIndex(FULL_NAME));
            people.add(new People(Name));
        }
        cursor.close();
        
        return people;
    }

    public boolean containsPeople(People people){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT " 
            + FULL_NAME +" FROM " 
            + PEOPLE + " WHERE " 
            + FULL_NAME + " =?", new String[]{people.Name});

        if(cursor.moveToFirst())
        {
            return true;
        }
        cursor.close();
        return false;
    }

    public ArrayList<CalendarDay> SelectAllNotEmptyDays(String year, String month){

        ArrayList<CalendarDay> days = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT "
                + DAY + " FROM "
                + DATA_PEOPLE + " WHERE "
                + MONTH + " = ? " + " AND "
                + YEAR + " = ?",new String[]{month,year});

        if(cursor.moveToFirst()){
            do{
                int index = cursor.getColumnIndex(DAY);
                int day = cursor.getInt(index);
                days.add(CalendarDay.from(Integer.valueOf(year),Integer.valueOf(month), day));
                //Log.d("days",String.valueOf(day));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return days;
    }

    //Добавление новой группы в БД
    public void addGroup(String name) {
        ContentValues cv = new ContentValues();

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        cv.put(GROUP_NAME,name);
        sqLiteDatabase.insert(GROUPS,null,cv);
    }
    //Проверка наличия группы в БД
    public boolean containsGroup(Group group) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(
                "SELECT " + GROUP_NAME
                        + " FROM " + GROUPS
                        + " WHERE " + GROUP_NAME
                        + " =?",new String[]{group.Name});
        if(cursor.moveToFirst()){
            return  true;
        }
        else{
            Log.d("ContainsGroup","No group like that!");
        }
        cursor.close();
        return false;

    }

    public ArrayList<Group> getAllGroups() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        ArrayList<Group> groups = new ArrayList<>();

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT "
                + GROUP_NAME +" FROM "
                + GROUPS,null);
        while (cursor.moveToNext()){
            String Name = cursor.getString(cursor.getColumnIndex(GROUP_NAME));
            groups.add(new Group(Name));
        }
        cursor.close();

        return groups;
    }

    //  Возвращает всех людей добавленных в эту группу
    public ArrayList<People> getGroupMembers(String groupName) {
       String GroupId = GetIdGroupByName(groupName);

       ArrayList<People>Peoples = new ArrayList<>();
       SQLiteDatabase sqLiteDatabase = getReadableDatabase();
       Cursor cursor;

       cursor = sqLiteDatabase.rawQuery(
               "SELECT "
               + ID_PEOPLE
               + " FROM "
               + PEOPLES_GROUP
               + " WHERE "
               + ID_GROUP
               + " =? ",
               new String[]{GroupId});
       if(cursor.moveToFirst()) {
           int index = cursor.getColumnIndex(ID_PEOPLE);
           do{
               String ID = cursor.getString(index);
               Peoples.add(new People(GetNameByID(ID)));
           }while (cursor.moveToNext());
       }
       else
           Log.e("PG","Cursor is null!!!");
       cursor.close();
       return Peoples;
    }

    // Возвращает всех людей имеющих в начале полученный текст
    public ArrayList<People> getPeopleFilter(String newText) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        ArrayList<People>Peoples = new ArrayList<>();
        Cursor cursor;
        cursor = sqLiteDatabase.rawQuery(
                "SELECT "
                + FULL_NAME
                + " FROM "
                + PEOPLE
                + " WHERE "
                + FULL_NAME
                + " LIKE ?",
                new String[]{"%" + newText + "%"});
        if(cursor.moveToFirst()){
            int index = cursor.getColumnIndex(FULL_NAME);
            do{
                String name = cursor.getString(index);
                Peoples.add(new People(name));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return Peoples;
    }

    // Добавляет всех людей из выбранной группы на текущуюю выбранную дату
    public void addFromGroup(String groupname,String year,String month,String day) {
        String id = GetIdGroupByName(groupname);

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery(
                "SELECT "
                        + FULL_NAME
                        + " FROM "
                        + PEOPLE
                        + " WHERE "
                        + KEY_ID
                        + " = "
                        + "(SELECT "
                        + ID_PEOPLE
                        + " FROM "
                        + PEOPLES_GROUP
                        + " WHERE "
                        + ID_GROUP
                        + " =?)",new String[]{id});

        if(cursor.moveToFirst()){
            int index = cursor.getColumnIndex(FULL_NAME);
            do{
                String name = cursor.getString(index);
                SetDataInDataTable(name,year,month,day);
            }while (cursor.moveToNext());
        }
        cursor.close();
    }

    // Удаляет человека из группы
    public void removePeopleFromGroup(String groupName ,String peopleName) {
        String PeopleId = GetIdByName(peopleName);
        String GroupId = GetIdGroupByName(groupName);

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        sqLiteDatabase.delete(PEOPLES_GROUP,
                ID_PEOPLE + " =? " + " AND " + ID_GROUP + " =? ",
                new String[]{PeopleId,GroupId});
    }

    // Добавить человека в группу
    public void addPeopleInGroup(String name,String groupName) {
        String PeopleId = GetIdByName(name);
        String GroupId = GetIdGroupByName(groupName);

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(ID_PEOPLE,PeopleId);
        cv.put(ID_GROUP,GroupId);

        sqLiteDatabase.insert(PEOPLES_GROUP,null,cv);
    }

    // Возвращает всех людей, что ещё не состоят в группе
    public ArrayList<People> getAllPeopleNotInGroup(String groupName )
    {
        String GroupId = GetIdGroupByName(groupName);

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        ArrayList<People> Peoples = new ArrayList<>();
        ArrayList<String> id = new ArrayList<>();
        ArrayList<String> Group = new ArrayList<>();

        Cursor cursor;
        cursor = sqLiteDatabase.rawQuery(
                "SELECT "
                + KEY_ID
                + " FROM "
                + PEOPLE,
                null);
        if (cursor.moveToFirst()) {

            int index = cursor.getColumnIndex(KEY_ID);
            do{
                id.add(cursor.getString(index));
            }while (cursor.moveToNext());
        }
        else {
            return getAllPeople();
        }

        cursor = sqLiteDatabase.rawQuery(
                "SELECT "
                        + ID_PEOPLE
                        + " FROM "
                        + PEOPLES_GROUP
                        + " WHERE "
                        + ID_GROUP
                        + " =?",
                new String[]{GroupId});
        if (cursor.moveToFirst()) {

            int index = cursor.getColumnIndex(ID_PEOPLE);
            do{
                Group.add(cursor.getString(index));
            }while (cursor.moveToNext());

            for(int i = 0; i < id.size();i++){
                if(!Group.contains(id.get(i))){
                    Peoples.add(new People(GetNameByID(id.get(i))));
                }
            }
        }
        else{
            return getAllPeople();
        }

        cursor.close();
        return Peoples;
    }
    // Затычка
    // Возвращает лист людей из фильтрованной группы
    public ArrayList<People> getFilterGroupPeople(String text, String groupName) {
        return new ArrayList<>();
    }
    // Затычка
    // Возвращает лист групп подходящих по названию поиска
    public ArrayList<Group> getGroupsFilter(String newText) {

        return new ArrayList<>();
    }
}
