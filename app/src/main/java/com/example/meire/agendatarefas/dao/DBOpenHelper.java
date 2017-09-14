package com.example.meire.agendatarefas.dao;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.meire.agendatarefas.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DBOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME​ = "bd_easytasks.db";
    private static final int VERSAO_BANCO​ = 1;
    private Context ctx​;
    public DBOpenHelper(Context context) {
        super(context, DB_NAME​, null, VERSAO_BANCO​);
        this.ctx​ = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_BOOK_TABLE_USR = "CREATE TABLE tblusers ( " +
                "user VARCHAR(50) UNIQUE, " +
                "password VARCHAR(50) UNIQUE)";

        sqLiteDatabase.execSQL(CREATE_BOOK_TABLE_USR);

        String CREATE_BOOK_TABLE_TASKS = "CREATE TABLE tblTasks ( " +
                "title VARCHAR(100)," +
                "description VARCHAR(300) NULL," +
                "adress_name VARCHAR(300) NULL," +
                "adress VARCHAR(8000) NULL," +
                "latitude REAL NULL," +
                "longitude REAL NULL," +
                "done CHAR(1) NULL," +
                "datetime_reminder VARCHAR(15)," +
                "repetition INT, " +
                "phone VARCHAR(20) NULL)";

        sqLiteDatabase.execSQL(CREATE_BOOK_TABLE_TASKS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS tblusers");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS tbltasks");
        this.onCreate(sqLiteDatabase);

    }
}