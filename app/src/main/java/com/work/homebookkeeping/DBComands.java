package com.work.homebookkeeping;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DBComands extends SQLiteOpenHelper {

    public static boolean created = false;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "HomeBookkeeper";

    public DBComands(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("DB", "start construct");
        //this.onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Log.d("DB", "start wallets");
        //Log.d("DB", db.getPath());
        Log.d("DB", "start wallets");
        db.execSQL("CREATE TABLE Wallets (_id INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT)");
        created = true;
        Log.d("DB", "Create wallets");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        Log.d("DB", "start upgrade");
    }

    static String QuotedStr(String str) {
        str = "'" + str + "'";
        Log.d("DB", str);
        return str;
    }

    public static void AddWallet(SQLiteDatabase db, String walletName) {
        Log.d("DB", "Start wallet");
        db.execSQL("INSERT INTO Wallets (Name) VALUES (" + QuotedStr(walletName) + ")");
        Log.d("DB", "Insert wallets");
        Integer number;
        Cursor cursor = db.query("Wallets", new String[] {"_id", "NAME"}, null, null, null, null, null);
        Log.d("DB", "Cursor");
        cursor.moveToLast();
        number = cursor.getInt(cursor.getColumnIndex("_id"));
        Log.d("DB", number.toString());
        db.execSQL("CREATE TABLE DATABASE" + number + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, DateTrans DATE, Sum INTEGER, Val INTEGER, Comment VARCHAR(255))");
        Log.d("DB", "Create db");
        db.execSQL("INSERT INTO DATABASE" + number + " (DateTrans, Sum, Val, Comment) VALUES (0, 0, 0, '')");
        Log.d("DB", "Insert db");
    }
}
