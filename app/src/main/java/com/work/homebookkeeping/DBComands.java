package com.work.homebookkeeping;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DBComands extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "HomeBookkeeper";

    public DBComands(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Wallets (_id INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        for (int j = 0; j < 100; j++)
            db.execSQL("drop table if exists DATABASE" + i);
        db.execSQL("drop table if exists Wallets");
        onCreate(db);
    }

    static String QuotedStr(String str) {
        str = "'" + str + "'";
        return str;
    }

    public static Cursor getWallet(SQLiteDatabase db, String searchName) {
        String sName;
        Cursor cursor = db.query("Wallets", new String[] {"_id", "Name"}, null, null, null, null, null);
        cursor.moveToFirst();
        Log.d("DB", "1");
        for (int i = 0; i < cursor.getCount(); i++) {
            sName = cursor.getString(cursor.getColumnIndex("Name"));
            Log.d("DB", "2++");
            Log.d("DB", sName);
            //Log.d("DB", cursor.getString(cursor.getColumnIndex("Name")));
            Log.d("DB", searchName);
            if (sName.equals(searchName)) {
                Log.d("DB", "2.6");
                break;
            }
            Log.d("DB", "2--");
            cursor.moveToNext();
        }
        Log.d("DB", "3");
        return cursor;
    }

    public static Integer getMoney(SQLiteDatabase db, Integer number) {
        int sum = 0;
        @SuppressLint("Recycle") Cursor cursor = db.query("DATABASE" + number.toString(), new String[] {"Sum", "Val"}, null, null, null, null, null);
        cursor.moveToFirst();
        do {
            switch (cursor.getInt(cursor.getColumnIndex("Val"))) {
                case 0:
                    sum += cursor.getInt(cursor.getColumnIndex("Sum"));
                    break;
                case 1:
                    sum += cursor.getInt(cursor.getColumnIndex("Sum")) * OptionsActivity.euro;
                    break;
                case 2:
                    sum += cursor.getInt(cursor.getColumnIndex("Sum")) * OptionsActivity.dollar;
                    break;
                default:
                    break;
            }
        } while (cursor.moveToNext());
        return sum;
    }

    public static void addWallet(SQLiteDatabase db, String walletName) {
        int number;
        db.execSQL("INSERT INTO Wallets (Name) VALUES (" + QuotedStr(walletName) + ")");
        @SuppressLint("Recycle") Cursor cursor = db.query("Wallets", new String[] {"_id", "NAME"}, null, null, null, null, null);
        cursor.moveToLast();
        number = cursor.getInt(cursor.getColumnIndex("_id"));
        db.execSQL("CREATE TABLE DATABASE" + number + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, DateTrans DATE, Sum INTEGER, Val INTEGER, Comment VARCHAR(255))");
        db.execSQL("INSERT INTO DATABASE" + number + " (DateTrans, Sum, Val, Comment) VALUES (0, 0, 0, '')");
    }

    public static void deleteWallet(SQLiteDatabase db, String walletName) {
        int number;
        Cursor cursor = getWallet(db, walletName);
        number = cursor.getInt(cursor.getColumnIndex("_id"));
        db.execSQL("DROP TABLE DATABASE" + number);
        db.execSQL("DELETE FROM Wallets WHERE _id = " + number);
    }

    public static void addTrans(SQLiteDatabase db, String walletName, String date, String sum, int val, String comment) {
        int number;
        Cursor cursor = getWallet(db, walletName);
        number = cursor.getInt(cursor.getColumnIndex("_id"));
        db.execSQL("INSERT INTO DATABASE" + number + " (DateTrans, Sum, Val, Comment) VALUES (" + QuotedStr(date) + ", " + Integer.parseInt(sum) + ", " + val + ", " + QuotedStr(comment) + ")");
    }
}
