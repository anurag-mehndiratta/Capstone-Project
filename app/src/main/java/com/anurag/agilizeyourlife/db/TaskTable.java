package com.anurag.agilizeyourlife.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TaskTable {

    // Database table
    public static final String TABLE_TASK = "task";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_LIST = "list";

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_TASK
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TITLE + " text not null, "
            + COLUMN_DESCRIPTION + " text not null,"
            + COLUMN_LIST
            + " integer not null"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(TaskTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);
        onCreate(database);
    }
}