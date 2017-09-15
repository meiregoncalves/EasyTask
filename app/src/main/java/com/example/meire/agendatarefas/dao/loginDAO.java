package com.example.meire.agendatarefas.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.meire.agendatarefas.model.LoginModel;

public class loginDAO {
    private DBOpenHelper banco ;

    public loginDAO(Context context) {
        banco = new DBOpenHelper(context);
    }

    public static final String TABELA_LOGINS = "tblusers" ;
    public static final String COL_USER = "user" ;
    public static final String COL_PASSWORD = "password" ;

    public LoginModel getBy(LoginModel login) {
        SQLiteDatabase db = banco.getReadableDatabase();
        String colunas[] = {COL_USER, COL_PASSWORD};
        String where = "user = '" + login.getUser() + "' and password = '" + login.getPassword() + "'";
        Cursor cursor = db.query(true, TABELA_LOGINS, colunas, where, null, null, null, null, null);
        LoginModel loginm = null ;
        if (cursor != null )
        {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                loginm = new LoginModel();
                loginm.setUser(cursor.getString(cursor.getColumnIndex(COL_USER)));
                loginm.setPassword(cursor.getString(cursor.getColumnIndex(COL_PASSWORD)));
            }
        }
        return loginm;
    }

    public boolean getUserExists(LoginModel login) {
        SQLiteDatabase db = banco.getReadableDatabase();
        String colunas[] = {COL_USER, COL_PASSWORD};
        String where = "user = '" + login.getUser() + "' and password = '" + login.getPassword() + "'";
        Cursor cursor = db.query(true, TABELA_LOGINS, colunas, where, null, null, null, null, null);
        LoginModel loginm = null ;
        if (cursor != null )
        {
            if (cursor.getCount() > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean getUserExists(String user) {
        SQLiteDatabase db = banco.getReadableDatabase();
        String colunas[] = {COL_USER, COL_PASSWORD};
        String where = "user = '" + user + "'";
        Cursor cursor = db.query(true, TABELA_LOGINS, colunas, where, null, null, null, null, null);
        LoginModel loginm = null ;
        if (cursor != null )
        {
            if (cursor.getCount() > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean add(LoginModel login) {
        if (getBy(login) == null) {
            long resultado;
            SQLiteDatabase db = banco.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_USER, login.getUser());
            values.put(COL_PASSWORD, login.getPassword());
            resultado = db.insert(TABELA_LOGINS, null, values);
            db.close();
            if (resultado == -1) {
                return false;
            } else {
                return true;
            }
        }
        else {
            return true;
        }
    }

    public Boolean del(LoginModel login) {
        long resultado;
        SQLiteDatabase db = banco.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER, login.getUser());
        values.put(COL_PASSWORD, login.getPassword());
        resultado = db.delete(TABELA_LOGINS, "user = '" + login.getUser() + "' and password = '" + login.getPassword() + "'", null);
        db.close();
        if (resultado == -1) {
            return false;
        } else {
            return true;
        }
    }
}
