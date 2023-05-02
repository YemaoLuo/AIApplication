package com.cpb.aiapplication.helper;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String dbName = "OSSS.db";
    private static final String createLog = "CREATE TABLE log(id Integer PRIMARY KEY autoincrement, name varchar not null, data varchar not null);";


    public DBHelper(@Nullable Context context) {
        super(context, dbName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createLog);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean save(@NonNull String fileName, @NonNull String data) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            String sql = "insert into log(name, data) values(?, ?)";
            SQLiteStatement sqLiteStatement = db.compileStatement(sql);
            sqLiteStatement.bindString(1, fileName);
            sqLiteStatement.bindString(2, data);
            sqLiteStatement.executeInsert();
            return true;
        } catch (Exception e) {
            Log.d("DBHelper", e.getMessage(), e);
            return false;
        }
    }

    public List<String> loadAll() {
        List<String> namesList = new ArrayList<>();
        try {
            SQLiteDatabase db = getWritableDatabase();
            String[] projection = {"name"};
            String selection = null;
            String[] selectionArgs = null;
            String sortOrder = "id DESC";
            Cursor cursor = db.query(
                    "log",
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                namesList.add(name);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            Log.d("DBHelper", e.getMessage(), e);
        }
        return namesList;
    }

    public String load(@NonNull String fileName) {
        String data = null;
        try {
            SQLiteDatabase db = getWritableDatabase();
            String[] projection = {"data"};
            String selection = "name = ?";
            String[] selectionArgs = {fileName};
            Cursor cursor = db.query(
                    "log",
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );
            if (cursor.moveToFirst()) {
                data = cursor.getString(cursor.getColumnIndexOrThrow("data"));
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            Log.d("DBHelper", e.getMessage(), e);
        }
        return data;
    }

    public boolean remove(@NonNull String fileName) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            int result = db.delete("log", "name = ?", new String[]{fileName});
            return result > 0;
        } catch (Exception e) {
            Log.d("DBHelper", e.getMessage(), e);
            return false;
        }
    }

    public boolean removeAll() {
        try {
            SQLiteDatabase db = getWritableDatabase();
            int result = db.delete("log", null, null);
            return result > 0;
        } catch (Exception e) {
            Log.d("DBHelper", e.getMessage(), e);
            return false;
        }
    }

    public String nameBuilder(int m, int n, int k, int j, int s) {
        String name = m + "-" + n + "-" + k + "-" + j + "-" + s;
        int count = 0;
        try {
            SQLiteDatabase db = getWritableDatabase();
            String[] projection = {"name"};
            String selection = "name LIKE ?";
            String[] selectionArgs = {name + "%"};
            Cursor cursor = db.query(
                    "log",
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );
            while (cursor.moveToNext()) {
                count = Math.max(count, Integer.parseInt(cursor.getString(0).split("-")[5]));
            }
            cursor.close();
            db.close();
            name += "-" + (count + 1);
            return name;
        } catch (Exception e) {
            Log.d("DBHelper", e.getMessage(), e);
            return "ERROR";
        }
    }
}