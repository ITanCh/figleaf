package com.example.figleaf;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class myOpenHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "user_information";
    public static final String ID = "_id";
    public static final String TABLE_NAME = "info";
    public static final String NAME = "name";
    public static final String PW = "password";
    public static final String PICPATH = "picpath";
    public static final String DIRTY = "dirty";

    public myOpenHelper(Context context, String name, CursorFactory factory,
                        int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("create table if not exists " + TABLE_NAME + "("
                + ID + " integer primary key,"
                + NAME + " varchar,"
                + PW + " varchar,"
                + PICPATH + " varchar,"
                + DIRTY + " varchar)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

}
