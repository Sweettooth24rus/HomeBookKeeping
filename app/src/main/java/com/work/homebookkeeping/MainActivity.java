package com.work.homebookkeeping;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ImageButton options;
    Button trans;
    ImageButton wallet;
    ImageButton next;
    ImageButton delete;
    TextView name;
    TextView money;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        options = findViewById(R.id.imageButtonOptions);
        trans = findViewById(R.id.buttonTrans);
        wallet = findViewById(R.id.imageButtonAddWallet);
        next = findViewById(R.id.imageButtonNextWallet);
        delete = findViewById(R.id.imageButtonDeleteWallet);
        name = findViewById(R.id.textViewName);
        money = findViewById(R.id.textViewMoney);
        DBComands dbComands = new DBComands(this);
        SQLiteDatabase db = dbComands.getWritableDatabase();

        try {
            fillFields(db);
        } catch (Exception e) {
            name.setText("Ни одного счёта не создано");
            money.setText("");
            dbComands.onUpgrade(db, 0, 1);
        }

        options.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Recycle")
            @Override
            public void onClick(View view) {
                String nname;
                Cursor cursor = db.query("Wallets", new String[]{"_id", "Name"}, null, null, null, null, null);
                cursor.moveToFirst();
                do {
                    nname = cursor.getString(cursor.getColumnIndex("Name"));
                    Log.d("DB", nname);
                } while (cursor.moveToNext());
                //startActivity(new Intent(MainActivity.this, OptionsActivity.class));
                int number;
                String sName;
                String searchName = name.getText().toString();
                cursor = DBComands.getWallet(db, searchName);
                try {
                    //name.setText(cursor.getString(cursor.getColumnIndex("Name")));
                    number = cursor.getInt(cursor.getColumnIndex("_id"));
                    cursor = db.query("DATABASE" + Integer.toString(number), new String[]{"_id", "DateTrans", "Sum", "Val", "Comment"}, null, null, null, null, null);
                    cursor.moveToFirst();
                    for (int i = 0; i < cursor.getCount(); i++) {
                        sName = String.valueOf(cursor.getInt(cursor.getColumnIndex("_id"))) + " ";
                        sName = sName + cursor.getString(cursor.getColumnIndex("DateTrans")) + " ";
                        sName = sName + String.valueOf(cursor.getInt(cursor.getColumnIndex("Sum"))) + " ";
                        sName = sName + String.valueOf(cursor.getInt(cursor.getColumnIndex("Val"))) + " ";
                        sName = sName + cursor.getString(cursor.getColumnIndex("Comment"));
                        Log.d("DB", sName);
                        cursor.moveToNext();
                    }
                } catch (Exception e) {
                    Log.d("DB", "e");
                    Log.d("DB", String.valueOf(cursor.moveToNext()));
                }
            }
        });

        trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TransActivity.class);
                intent.putExtra("name", name.getText().toString());
                startActivity(intent);
            }
        });

        wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, WalletActivity.class));
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                int number;
                String searchName = name.getText().toString();
                Cursor cursor = DBComands.getWallet(db, searchName);
                if (!cursor.moveToNext())
                    cursor.moveToFirst();
                try {
                    name.setText(cursor.getString(cursor.getColumnIndex("Name")));
                    number = cursor.getInt(cursor.getColumnIndex("_id"));
                    money.setText(DBComands.getMoney(db, number).toString());
                } catch (Exception e) {
                    Log.d("DB", "e");
                    Log.d("DB", String.valueOf(cursor.moveToNext()));
                }
            }
        });

        delete.setOnTouchListener(new View.OnTouchListener() {
            Long time = 0L;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Toast.makeText(getApplicationContext(), "Удерживайте 3 секунды для удаления", Toast.LENGTH_SHORT).show();
                    time = System.currentTimeMillis();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if ((System.currentTimeMillis() - time) >= 3000) {
                        Log.d("DB", "YES");
                        DBComands.deleteWallet(db, name.getText().toString());
                        try {
                            fillFields(db);
                        } catch (Exception e) {
                            name.setText("Ни одного счёта не создано");
                            money.setText("");
                            dbComands.onUpgrade(db, 0 ,1);
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }
    void fillFields(SQLiteDatabase db) {
        int number;
        @SuppressLint("Recycle") Cursor cursor = db.query("Wallets", new String[]{"_id", "Name"}, null, null, null, null, null);
        cursor.moveToLast();
        name.setText(cursor.getString(cursor.getColumnIndex("Name")));
        number = cursor.getInt(cursor.getColumnIndex("_id"));
        money.setText(DBComands.getMoney(db, number).toString());
    }
}