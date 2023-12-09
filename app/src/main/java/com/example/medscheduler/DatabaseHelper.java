package com.example.medscheduler;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.time.LocalDateTime;

//creates/drops database table
public class DatabaseHelper extends SQLiteOpenHelper {
    static final int DATABASE_VERSION = 5;
    static final String DATABASE_NAME = "MED_SCHEDULER";
    static final String DATABASE_TABLE = "medications";
    static final String COLUMN_ID = "id";
    static final String COLUMN_NAME = "name";
    static final String COLUMN_LAST_TAKEN = "last";
    static final String COLUMN_NEXT_DOSE = "next";
    static final String COLUMN_HOURS_BETWEEN = "duration";
    DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "CREATE TABLE " + DATABASE_TABLE +" ("+
                COLUMN_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COLUMN_NAME+ " TEXT NOT NULL, "+
                COLUMN_LAST_TAKEN+ " DATETIME, "+
                COLUMN_NEXT_DOSE+ " DATETIME, "+
                COLUMN_HOURS_BETWEEN+ " INTEGER)";

        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ DATABASE_TABLE);
    }
}