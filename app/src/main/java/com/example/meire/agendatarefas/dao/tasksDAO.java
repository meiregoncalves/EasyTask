package com.example.meire.agendatarefas.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.meire.agendatarefas.model.LoginModel;
import com.example.meire.agendatarefas.model.TaskModel;

import java.util.LinkedList;
import java.util.List;

public class tasksDAO {
    private DBOpenHelper banco ;

    public tasksDAO(Context context) {
        banco = new DBOpenHelper(context);
    }

    public static final String TABLE_TASKS = "tblTasks" ;
    public static final String COL_ID = "rowid" ;
    public static final String COL_TITLE = "title" ;
    public static final String COL_DESCRIPTION = "description" ;
    public static final String COL_ADRESS = "adress" ;
    public static final String COL_PHONE = "phone" ;
    public static final String COL_DONE = "done" ;
    public static final String COL_ADRESS_NAME = "adress_name" ;
    public static final String COL_LATITUDE = "latitude" ;
    public static final String COL_LONGITUDE = "longitude" ;
    public static final String COL_DATETIME_REMINDER = "datetime_reminder" ;
    public static final String COL_REPETITION = "repetition" ;

    public TaskModel getBy(int id) {
        SQLiteDatabase db = banco.getReadableDatabase();
        String colunas[] = { COL_ID , COL_TITLE, COL_DESCRIPTION, COL_ADRESS, COL_PHONE, COL_DONE, COL_ADRESS_NAME, COL_LATITUDE, COL_LONGITUDE, COL_DATETIME_REMINDER, COL_REPETITION };
        String where = "rowid = " + id;
        Cursor cursor = db.query(true, TABLE_TASKS, colunas, where, null, null, null, null, null);
        TaskModel task = null ;
        if (cursor != null )
        {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                task = new TaskModel();
                task.setTitle(cursor.getString(cursor.getColumnIndex(COL_TITLE)));
                task.setDescription(cursor.getString(cursor.getColumnIndex(COL_DESCRIPTION)));
                task.setAdress(cursor.getString(cursor.getColumnIndex(COL_ADRESS)));
                task.setPhone(cursor.getString(cursor.getColumnIndex(COL_PHONE)));
                task.setDone(cursor.getString(cursor.getColumnIndex(COL_DONE)));
                task.setAdress_name(cursor.getString(cursor.getColumnIndex(COL_ADRESS_NAME)));
                task.setLatitude(cursor.getDouble(cursor.getColumnIndex(COL_LATITUDE)));
                task.setLongitude(cursor.getDouble(cursor.getColumnIndex(COL_LONGITUDE)));
                task.setDatetime_reminder(cursor.getString(cursor.getColumnIndex(COL_DATETIME_REMINDER)));
                task.setRepetition(cursor.getInt(cursor.getColumnIndex(COL_REPETITION)));
            }
        }
        return task;
    }

    public List<TaskModel> getAll() {
        List<TaskModel> tasks = new LinkedList<>();
        String query = "SELECT rowid, title, description, adress, phone, done, adress_name, latitude, longitude, datetime_reminder, repetition FROM " + TABLE_TASKS + " order by done" ;
        SQLiteDatabase db = banco .getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null );
        TaskModel task = null ;
        if (cursor.moveToFirst()) {
            do {
                task = new TaskModel();
                task.setTitle(cursor.getString(cursor.getColumnIndex(COL_TITLE)));
                task.setDescription(cursor.getString(cursor.getColumnIndex(COL_DESCRIPTION)));
                task.setAdress(cursor.getString(cursor.getColumnIndex(COL_ADRESS)));
                task.setAdress_name(cursor.getString(cursor.getColumnIndex(COL_ADRESS_NAME)));
                task.setLatitude(cursor.getDouble(cursor.getColumnIndex(COL_LATITUDE)));
                task.setLongitude(cursor.getDouble(cursor.getColumnIndex(COL_LONGITUDE)));
                task.setPhone(cursor.getString(cursor.getColumnIndex(COL_PHONE)));
                task.setDone(cursor.getString(cursor.getColumnIndex(COL_DONE)));
                task.setDatetime_reminder(cursor.getString(cursor.getColumnIndex(COL_DATETIME_REMINDER)));
                task.setRepetition(cursor.getInt(cursor.getColumnIndex(COL_REPETITION)));
                task.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COL_ID))));
                tasks.add(task);
            } while (cursor.moveToNext());
        }
        return tasks;
    }

    public long add(TaskModel task) {
            long resultado;
            SQLiteDatabase db = banco.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_TITLE, task.getTitle().toString());
            values.put(COL_DESCRIPTION, task.getDescription().toString());
            values.put(COL_ADRESS, task.getAdress().toString());
            values.put(COL_PHONE, task.getPhone().toString());
            values.put(COL_DONE, task.getDone().toString());
            values.put(COL_ADRESS_NAME, task.getAdress_name().toString());
            values.put(COL_LATITUDE, task.getLatitude().toString());
            values.put(COL_LONGITUDE, task.getLongitude().toString());
            values.put(COL_DATETIME_REMINDER, task.getDatetime_reminder());
            values.put(COL_REPETITION, task.getRepetition());

            resultado = db.insert(TABLE_TASKS, null, values);
            db.close();
            return resultado;
    }

    public Boolean del(int id) {
        long resultado;
        SQLiteDatabase db = banco.getWritableDatabase();
        resultado = db.delete(TABLE_TASKS, "rowid = " + id, null);
        db.close();
        if (resultado == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Boolean update(TaskModel task) {
        long resultado;
        SQLiteDatabase db = banco.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, task.getTitle());
        values.put(COL_DESCRIPTION, task.getDescription());
        values.put(COL_ADRESS, task.getAdress());
        values.put(COL_PHONE, task.getPhone());
        values.put(COL_DONE, task.getDone());
        values.put(COL_ADRESS_NAME, task.getAdress_name());
        values.put(COL_LATITUDE, task.getLatitude());
        values.put(COL_LONGITUDE, task.getLongitude());
        values.put(COL_DATETIME_REMINDER, task.getDatetime_reminder());
        values.put(COL_REPETITION, task.getRepetition());
        String Where = "rowid = ?";
        String argument[] = { Integer.toString(task.getId()) };
        resultado = db.update(TABLE_TASKS, values, Where, argument);
        db.close();
        if (resultado == -1) {
            return false;
        } else {
            return true;
        }
    }
}
