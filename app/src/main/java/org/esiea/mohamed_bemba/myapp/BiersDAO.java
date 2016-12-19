package org.esiea.mohamed_bemba.myapp;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BiersDAO {

    // Champs de la base de données
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.ID,
            MySQLiteHelper.TABLE_NAME };

    public BiersDAO(Context context) {
        dbHelper = new MySQLiteHelper(context);

    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void createBier(String name) {
        database.execSQL("INSERT INTO "+MySQLiteHelper.TABLE_NAME+" ("+MySQLiteHelper.NAME+") VALUES ('"+name+"');");
    }

    public void deleteBier(String name) {
        database.execSQL(" DELETE FROM " +MySQLiteHelper.TABLE_NAME+" WHERE "+MySQLiteHelper.NAME+" = '"+name+"';");

    }

    public ArrayList<String> getAllBiers() {
        ArrayList<String> names = new ArrayList<String>();

        Cursor resultSet = database.rawQuery("Select * from "+MySQLiteHelper.TABLE_NAME+";",null);
        Log.d("biersdao","Apreès cursor");
        resultSet.moveToFirst();
        Log.d("biersdao","Apreès movetofirst");
        int i =0;

        while(resultSet.isAfterLast() == false) {
            names.add(resultSet.getString(1));
            i++;
            resultSet.moveToNext();
        }
        resultSet.close();
        Log.d("biersdao","Taille de la db = "+i);
        return names;
    }



    public boolean isAlreadyFavorite(String name) {

        Cursor resultSet = database.rawQuery("Select * from "+MySQLiteHelper.TABLE_NAME+";",null);
        Log.d("biersdao","Apreès cursor");
        resultSet.moveToFirst();
        Log.d("biersdao","Apreès movetofirst");

        while(resultSet.isAfterLast() == false) {
           if(resultSet.getString(1).equals(name)){
                return true;
            }
            resultSet.moveToNext();
        }

        resultSet.close();
        return false;
    }




}