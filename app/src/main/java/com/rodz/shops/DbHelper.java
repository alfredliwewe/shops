package com.rodz.shops;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mydb.db";
    private static final int DATABASE_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS shops (id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS products (id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, buying VARCHAR, selling VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS stock (id INTEGER PRIMARY KEY AUTOINCREMENT, shop VARCHAR, product VARCHAR, qty VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS progress (id INTEGER PRIMARY KEY AUTOINCREMENT, shop VARCHAR, product VARCHAR, qty VARCHAR, status VARCHAR, date_str VARCHAR, day VARCHAR)");
        /*db.execSQL("CREATE TABLE IF NOT EXISTS user (id INTEGER PRIMARY KEY AUTOINCREMENT, webid VARCHAR, name VARCHAR, phone VARCHAR, email VARCHAR, type VARCHAR, file VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS settings (name VARCHAR, value VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS cart (id INTEGER primary key autoincrement, product VARCHAR, qty VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS displayImages (id INTEGER primary key autoincrement, webid VARCHAR, product VARCHAR, file VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS purchase (id INTEGER PRIMARY KEY AUTOINCREMENT, webid VARCHAR, product INTEGER, `key` TEXT, user TEXT, amount NUMERIC, time TEXT, status TEXT, approach TEXT)"); */
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}