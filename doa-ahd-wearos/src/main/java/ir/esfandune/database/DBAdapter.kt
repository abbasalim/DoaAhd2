package ir.esfandune.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import ir.esfandune.database.DBAdapter.DatabaseHelper
import kotlin.Throws
import ir.esfandune.database.DBAdapter
import ir.esfandune.database.Doa
import java.util.ArrayList

class DBAdapter(private val context: Context) {
    val DATABASE_MAINTABLE = "main"
    val KEY_ARABI = "arabi"
    val KEY_EZAFI = "extera"
    val KEY_FARSI = "farsi"
    val KEY_ROWID = "id"
    val TAG = "DBAdapter"
    private val CREATE_MAINTABLE =
        "create table main(id INTEGER PRIMARY KEY  NOT NULL, arabi text ,farsi text ,extera text);"
    private val DATABASE_VERSION = 5
    private val DBHelper: DatabaseHelper
    private var db: SQLiteDatabase? = null
    private val yek_nam = arrayOf(KEY_ROWID, KEY_ARABI, KEY_FARSI, KEY_EZAFI)

    //---opens the database---
    @Throws(SQLException::class)
    fun open(): DBAdapter {
        db = DBHelper.writableDatabase
        return this
    }

    //---closes the database---
    fun close() {
        DBHelper.close()
    }

    //---insert a contact into the database---
    fun insertSH_SUB(doa: Doa): Long {
        val initialValues = ContentValues()
        //        initialValues.put(KEY_ROWID, doa.getId());
        initialValues.put(KEY_ARABI, doa.getarabi())
        initialValues.put(KEY_FARSI, doa.getfarsi())
        initialValues.put(KEY_EZAFI, doa.getezafi())
        return db!!.insert(DATABASE_MAINTABLE, null, initialValues)
    }

    //---retrieves all the contacts---
    val allItem: List<Doa>
        get() {
            val cursor = db!!.query(DATABASE_MAINTABLE, yek_nam, null, null, null, null, null)
            val nams = cursorToList_Doa(cursor)
            cursor.close()
            return nams
        }

    /////////////
    private fun cursorToList_Doa(cursor: Cursor): List<Doa> {
        val nams: MutableList<Doa> = ArrayList()
        Log.i(TAG, "1")
        if (cursor.count > 0) {
            Log.i(TAG, "2")
            while (cursor.moveToNext()) {
                val nam = Doa()
                nam.id = cursor.getInt(cursor.getColumnIndex(KEY_ROWID))
                nam.setarabi(cursor.getString(cursor.getColumnIndex(KEY_ARABI)))
                nam.setfarsi(cursor.getString(cursor.getColumnIndex(KEY_FARSI)))
                nam.setezafi(cursor.getString(cursor.getColumnIndex(KEY_EZAFI)))
                //	Log.i(TAG, nam.getarabi());
                nams.add(nam)
            }
        }
        cursor.close()
        return nams
    }

    /////////////////////
    private inner class DatabaseHelper internal constructor(context: Context?) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            try {
                db.execSQL(CREATE_MAINTABLE)
                //                db.execSQL("INSERT INTO main (arabi,farsi) VALUES ('خطا در کپی دیتابیس','خطا در کپی دیتابیس');");
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            Log.w(
                TAG, "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data"
            )
            db.execSQL("DROP TABLE IF EXISTS $DATABASE_MAINTABLE")
            onCreate(db)
        }
    } //---retrieves a particular contact---

    companion object {
        const val DATABASE_NAME = "db_doaahd"
    }

    init {
        DBHelper = DatabaseHelper(context)
    }
}