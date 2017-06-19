package com.example.marcin.lista;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class BaseHelper extends SQLiteOpenHelper {

    public static final String database_name = "ListBase";
    public static final String database_table = "Elements";
    private SQLiteDatabase database;

    public BaseHelper(Context context) {
        super(context, database_name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + database_table + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, AMOUNT INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + database_table);
        onCreate(db);
    }

    public void open() {
        database = getWritableDatabase();
    }

    public boolean add(Row row) {
        ContentValues cv = new ContentValues();
        cv.put("NAME", row.getName());
        cv.put("AMOUNT", row.getAmount());
        long l = database.insert(database_table, null, cv);
        row.setId(l);
        return l != -1;
    }

    public List<Row> getData() {
        List<Row> rows = new ArrayList<>();
        SQLiteCursor cursor = (SQLiteCursor) database.rawQuery("SELECT * FROM " + database_table, null);
        while (cursor.moveToNext()) {
            rows.add(new Row(cursor.getString(1), cursor.getInt(2), cursor.getLong(0)));
        }
        cursor.close();
        return rows;
    }

    public boolean delete(String id) {
        return database.delete(database_table, "ID = ?", new String[]{id}) > 0;
    }

    public boolean deleteAll() {
        int i = database.delete(database_table, "1", null);
        database.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + database_table + "'");
        return i > 0;
    }

    public boolean updateAmount(long id, int value) {
        ContentValues cv = new ContentValues();
        cv.put("AMOUNT", value);
        return database.update(database_table, cv, "ID = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean updateName(long id, String value) {
        ContentValues cv = new ContentValues();
        cv.put("NAME", value);
        return database.update(database_table, cv, "ID = ?", new String[]{String.valueOf(id)}) > 0;
    }
}
