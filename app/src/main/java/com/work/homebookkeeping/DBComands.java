package com.work.homebookkeeping;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        db.execSQL("CREATE TABLE IF NOT EXISTS Courses (_id INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT, Coef REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        for (int j = 0; j < 100; j++)
            db.execSQL("DROP TABLE IF EXISTS DATABASE" + i);
        db.execSQL("DROP TABLE IF EXISTS Wallets");
        onCreate(db);
    }

    static String QuotedStr(String str) {
        str = "'" + str + "'";
        return str;
    }

    public static HashMap<String, Double> getCourses(SQLiteDatabase db) {
        HashMap<String, Double> courses = new HashMap<String, Double>();
        @SuppressLint("Recycle") Cursor cursor = db.query("Courses", new String[] {"Name", "Coef"}, null, null, null, null, null);
        cursor.moveToFirst();
        do {
            courses.put(cursor.getString(cursor.getColumnIndex("Name")), cursor.getDouble(cursor.getColumnIndex("Coef")));
        } while (cursor.moveToNext());
        return courses;
    }

    public static void setCourses(SQLiteDatabase db, HashMap<String, Double> courses) {
        db.execSQL("DELETE FROM Courses");
        for (Map.Entry<String, Double> tmp: courses.entrySet()) {
            db.execSQL("INSERT INTO Courses (Name, Coef) VALUES (" + QuotedStr(tmp.getKey()) + ", " + tmp.getValue() + ")");
            Log.d("DB", tmp.getKey() + tmp.getValue());
        }
    }

    @SuppressLint("Recycle")
    public static List<Trans> getTranses(SQLiteDatabase db, String name) {
        int number;
        Trans trans;
        List<Trans> transes = new ArrayList<Trans>();
        Cursor cursor = getWallet(db, name);
        try {
            number = cursor.getInt(cursor.getColumnIndex("_id"));
            cursor = db.query("DATABASE" + Integer.toString(number), new String[]{"_id", "DateTrans", "Sum", "Val", "Comment"}, null, null, null, null, null);
            cursor.moveToFirst();
            if (!cursor.moveToNext())
                return transes;
            for (int i = 1; i < cursor.getCount(); i++) {
                trans = new Trans();
                trans.setDate(cursor.getString(cursor.getColumnIndex("DateTrans")));
                trans.setSum(cursor.getDouble(cursor.getColumnIndex("Sum")));
                trans.setVal(whatVal(cursor.getInt(cursor.getColumnIndex("Val"))));
                trans.setComment(cursor.getString(cursor.getColumnIndex("Comment")));
                if (cursor.getInt(cursor.getColumnIndex("Val")) != 0) {
                    trans.setSumR(sumRoubles(db, trans.getSum(), trans.getVal()));
                    trans.setRub(false);
                }
                else {
                    trans.setSumR((double) 0);
                    trans.setRub(true);
                }
                transes.add(trans);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            return new ArrayList<Trans>();
        }
        return transes;
    }

    public static Cursor getWallet(SQLiteDatabase db, String searchName) {
        String sName;
        Cursor cursor = db.query("Wallets", new String[] {"_id", "Name"}, null, null, null, null, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            sName = cursor.getString(cursor.getColumnIndex("Name"));
            if (sName.equals(searchName)) {
                break;
            }
            cursor.moveToNext();
        }
        return cursor;
    }

    public static Double getMoney(SQLiteDatabase db, Integer number) {
        Double sum = (double) 0;
        @SuppressLint("Recycle") Cursor cursor = db.query("DATABASE" + number.toString(), new String[] {"Sum", "Val"}, null, null, null, null, null);
        cursor.moveToFirst();
        do {
            switch (cursor.getInt(cursor.getColumnIndex("Val"))) {
                case 0:
                    sum += cursor.getDouble(cursor.getColumnIndex("Sum"));
                    break;
                case 1:
                    sum += cursor.getDouble(cursor.getColumnIndex("Sum")) * OptionsActivity.euro;
                    break;
                case 2:
                    sum += cursor.getDouble(cursor.getColumnIndex("Sum")) * OptionsActivity.dollar;
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
        db.execSQL("CREATE TABLE DATABASE" + number + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, DateTrans DATE, Sum REAL, Val INTEGER, Comment VARCHAR(255))");
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

    public static String whatVal(Integer val) {
        switch (val) {
            case 0:
                return "Рубль";
            case 1:
                return "Евро";
            case 2:
                return "Доллар";
            default:
                return "Рубль";
        }
    }

    public static Double sumRoubles(SQLiteDatabase db, Double sum, String val) {
        HashMap<String, Double> courses = getCourses(db);
        Double sumR = sum * courses.get(val);
        return sumR;
    }
}
