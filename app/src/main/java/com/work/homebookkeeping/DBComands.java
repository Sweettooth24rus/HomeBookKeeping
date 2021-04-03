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
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {}

    static String QuotedStr(String str) {
        str = "'" + str + "'";
        return str;
    }

    public static Integer getMoney(SQLiteDatabase db, Integer number) {
        Integer sum = 0;
        Cursor cursor = db.query("DATABASE" + number.toString(), new String[] {"Sum", "Val"}, null, null, null, null, null);
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

    public static void AddWallet(SQLiteDatabase db, String walletName) {
        Integer number;
        db.execSQL("INSERT INTO Wallets (Name) VALUES (" + QuotedStr(walletName) + ")");
        Cursor cursor = db.query("Wallets", new String[] {"_id", "NAME"}, null, null, null, null, null);
        cursor.moveToLast();
        number = cursor.getInt(cursor.getColumnIndex("_id"));
        db.execSQL("CREATE TABLE DATABASE" + number + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, DateTrans DATE, Sum INTEGER, Val INTEGER, Comment VARCHAR(255))");
        db.execSQL("INSERT INTO DATABASE" + number + " (DateTrans, Sum, Val, Comment) VALUES (0, 0, 0, '')");
    }
}
