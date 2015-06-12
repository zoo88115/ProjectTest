package com.example.zoo88115.projecttest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zoo88115 on 2015/6/11 0011.
 */
public class MyDBHelper extends SQLiteOpenHelper{
    static String name="TempData";
    static SQLiteDatabase.CursorFactory factory=null;
    static  int version=1;

    public MyDBHelper(Context context){
        super(context,name,factory,version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL="CREATE TABLE IF NOT EXISTS Status(ID INTEGER PRIMARY KEY AUTOINCREMENT,Time Timestamp,Photo BLOB,Content String)";
        db.execSQL(SQL);
        String SQL2="CREATE TABLE IF NOT EXISTS User(ID INTEGER PRIMARY KEY AUTOINCREMENT,Icon BLOB,Name String)";
        db.execSQL(SQL2);
        String SQL3="CREATE TABLE IF NOT EXISTS Test(ID INTEGER PRIMARY KEY AUTOINCREMENT,TestIcon BLOB)";
        db.execSQL(SQL3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String SQL="DROPTABLE Status";
        db.execSQL(SQL);
        String SQL2="DROPTABLE User";
        db.execSQL(SQL2);
        String SQL3="DROPTABLE SQL3";
        db.execSQL(SQL3);
    }
}
