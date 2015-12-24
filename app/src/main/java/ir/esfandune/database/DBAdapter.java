package ir.esfandune.database;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {


    static final String CREATE_MAINTABLE = "create table main(id INTEGER PRIMARY KEY  NOT NULL, arabi text ,farsi text ,extera text);";
    public static final String DATABASE_MAINTABLE = "main";
    public static final String DATABASE_NAME = "db_doaahd";
    static final int DATABASE_VERSION = 2;
    public static final String KEY_ARABI = "arabi";
    public static final String KEY_EZAFI = "extera";
    public static final String KEY_FARSI = "farsi";
    public static final String KEY_ROWID = "id";
    public static final String TAG = "DBAdapter";
    DatabaseHelper DBHelper;
    final Context context;
    SQLiteDatabase db;
    String yek_nam[] = {
            KEY_ROWID, KEY_ARABI, KEY_FARSI, KEY_EZAFI
    };





    public DBAdapter(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try {
                db.execSQL(CREATE_MAINTABLE);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");

            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_MAINTABLE);
            onCreate(db);
        }
    }

    //---opens the database---
    public DBAdapter open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---
    public void close()
    {
        DBHelper.close();
    }

    //---insert a contact into the database---
    public long insertSH_SUB(Doa doa)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ROWID, Integer.valueOf(doa.getId()));
        initialValues.put(KEY_ARABI, doa.getarabi());
        initialValues.put(KEY_FARSI, doa.getfarsi());
        initialValues.put(KEY_EZAFI, doa.getezafi());


        return db.insert(DATABASE_MAINTABLE, null, initialValues);
    }




    //---retrieves all the contacts---
    public  List<Doa> getAllItem()
    {

        Cursor cursor = db.query(DATABASE_MAINTABLE, yek_nam, null, null, null, null, null);
        List<Doa> nams = cursorToList_Doa(cursor);
        cursor.close();
        return nams;
    }
    /////////////////////

    /////////////
    private List<Doa> cursorToList_Doa(Cursor cursor) {
        List<Doa> nams = new ArrayList<Doa>();
        Log.i(TAG, "1");
        if (cursor.getCount() > 0)
        {
            Log.i(TAG, "2");
            while (cursor.moveToNext()) {
                Doa nam = new Doa();
                nam.setId(cursor.getInt(cursor.getColumnIndex(KEY_ROWID)));
                nam.setarabi(cursor.getString(cursor.getColumnIndex(KEY_ARABI)));
                nam.setfarsi(cursor.getString(cursor.getColumnIndex(KEY_FARSI)));
                nam.setezafi (cursor.getString(cursor.getColumnIndex(KEY_EZAFI)));
                //	Log.i(TAG, nam.getarabi());
                nams.add(nam);
            } ;
        }
        cursor.close();
        return nams;
    }


    //---retrieves a particular contact---



}
