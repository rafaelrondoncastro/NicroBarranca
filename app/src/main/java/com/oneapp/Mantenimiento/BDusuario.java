package com.oneapp.Mantenimiento;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ingra on 5/12/2016.
 */

public class BDusuario extends SQLiteOpenHelper {

    String sqlCreate = "CREATE TABLE usu (id INTEGER, usuario TEXT,  password TEXT, nombre TEXT)";

    public BDusuario(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(sqlCreate);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS usu");
        db.execSQL(sqlCreate);

    }
}
