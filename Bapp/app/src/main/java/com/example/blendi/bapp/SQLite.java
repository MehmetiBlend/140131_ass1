package com.example.blendi.bapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by blendi on 9/13/2014.
 */
public class SQLite {

    public static String KEY_ROWID = "_id";
    public static String KEY_NAME = "city_country_name";
    public static String KEY_TEMP = "_temperature";
    public static String KEY_DESC = "_description";

    private static final String DB_NAME = "Weather";
    private static final String TBL_NAME = "PlacesTable";
    private static final int DB_VERSION = 1;

    private DbHelper myHelper;
    private final Context myContext;
    private SQLiteDatabase myDatabase;

    public SQLite(Context c) {
        myContext = c;
    }

    private static class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL("CREATE TABLE " + TBL_NAME + " (" + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_NAME + " TEXT NOT NULL, " + KEY_TEMP + " TEXT NOT NULL, " + KEY_DESC + " TEXT NOT NULL);");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i2) {

            db.execSQL("DROP TABLE IF EXISTS " + TBL_NAME);
            onCreate(db);
        }
    }

    public SQLite Open() throws SQLDataException {

        myHelper = new DbHelper(myContext);
        myDatabase = myHelper.getWritableDatabase();
        return this;
    }

    public void Close() {
        myHelper.close();
    }

    public long PutData(String name, String temp, String desc) throws SQLException {

        ContentValues cv = new ContentValues();
        cv.put(KEY_NAME, name);//put vaule of name in KEY_NAME column
        cv.put(KEY_TEMP, temp);
        cv.put(KEY_DESC, desc);

        return myDatabase.insert(TBL_NAME, null, cv);//insert data in TBL_NAME table
    }
/* not implemented method
    public String[] GetData(String name) {

        String[] Data = new String[4];
        String[] columns = new String[]{KEY_ROWID, KEY_NAME, KEY_TEMP, KEY_DESC};
        //Select * from TBL_NAME where KEY_NAME = name
        Cursor c = myDatabase.query(TBL_NAME, columns, KEY_NAME + "='" + name + "'", null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();//pick up the newest one and
            Data[0] = c.getString(0);
            Data[1] = c.getString(1);
            Data[2] = c.getString(2);
            Data[3] = c.getString(3);
            return Data;
        }
        return null;
    }
*/
    public String getAllData() {

        String data= "";
        String[] columns = new String[]{KEY_ROWID, KEY_NAME, KEY_TEMP, KEY_DESC};
        // Select * from TBL_NAME Group by KEY_NAME, Distinct = true
        Cursor c = myDatabase.query(true,TBL_NAME,columns,null,null,KEY_NAME,null,null,null);

        int i= 0;
        int iName = c.getColumnIndex(KEY_NAME);
        int iTemp = c.getColumnIndex(KEY_TEMP);
        int iDesc = c.getColumnIndex(KEY_DESC);

        //get all data from Cursor and put them into a string
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            i++;
            data +="\n"+ i + " " + c.getString(iName)+ "      "
                    +c.getString(iTemp) + "        " + c.getString(iDesc);
        }
        return data;

    }
    public void Delete() {
        myDatabase.execSQL("DROP TABLE " + TBL_NAME);
        myHelper.onCreate(myDatabase);
    }
}
