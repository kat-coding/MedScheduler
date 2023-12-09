package com.example.medscheduler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;
import java.time.LocalDateTime;

public class DatabaseManager {
    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;

    //constructor
    public DatabaseManager(Context context){
        this.context = context;
    }

    //initiate dbhelper and database
    public DatabaseManager open(){
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }
    //closes db
    public void close(){
        dbHelper.close();
    }
    //add to db
    public void insert(String name, LocalDateTime last, LocalDateTime next, int duration){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_NAME, name);
        contentValues.put(DatabaseHelper.COLUMN_LAST_TAKEN, String.valueOf(last));
        contentValues.put(DatabaseHelper.COLUMN_NEXT_DOSE, String.valueOf(next));
        contentValues.put(DatabaseHelper.COLUMN_HOURS_BETWEEN, duration);
        database.insert(DatabaseHelper.DATABASE_TABLE, null, contentValues);
    }
    public Cursor fetch() {
        //column names
        String[] columns = new String[]{DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_NAME,
                DatabaseHelper.COLUMN_LAST_TAKEN, DatabaseHelper.COLUMN_NEXT_DOSE, DatabaseHelper.COLUMN_HOURS_BETWEEN};
        //select columns from table
        Cursor cursor = database.query(DatabaseHelper.DATABASE_TABLE,columns, null,
                null, null, null, null);
        //return results in cursor
        if(cursor != null){
            cursor.moveToFirst();
        }
        return cursor;
    }
    public Cursor fetchById(int id){
        //column names
        String[] columns = new String[]{DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_NAME,
                DatabaseHelper.COLUMN_LAST_TAKEN, DatabaseHelper.COLUMN_NEXT_DOSE, DatabaseHelper.COLUMN_HOURS_BETWEEN};
        //select columns from table
        String selection = "id = ?";
        String [] selectionArgs = {String.valueOf(id)};
        Cursor cursor = database.query(DatabaseHelper.DATABASE_TABLE, columns, selection, selectionArgs, null, null, null, null);
        //return results in cursor
        if(cursor != null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    public void update(int id, String name, LocalDateTime last, LocalDateTime next, int duration){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_ID, id);
        contentValues.put(DatabaseHelper.COLUMN_NAME, name);
        contentValues.put(DatabaseHelper.COLUMN_LAST_TAKEN, String.valueOf(last));
        contentValues.put(DatabaseHelper.COLUMN_NEXT_DOSE, String.valueOf(next));
        contentValues.put(DatabaseHelper.COLUMN_HOURS_BETWEEN, duration);
        database.update(DatabaseHelper.DATABASE_TABLE, contentValues, DatabaseHelper.COLUMN_ID+"="+id, null);
    }
    public void delete(int id){
        database.delete(DatabaseHelper.DATABASE_TABLE, DatabaseHelper.COLUMN_ID+"="+id, null);
    }
}
