package com.oneapp.Mantenimiento;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ingra on 5/12/2016.
 */

public class BDip extends SQLiteOpenHelper {

    String sqlCreate = "CREATE TABLE Ip (id INTEGER, ip TEXT)";

    public BDip(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(sqlCreate);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS Ip");
        db.execSQL(sqlCreate);

    }
}
