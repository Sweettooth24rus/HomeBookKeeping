package com.work.homebookkeeping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    ImageButton options;
    Button trans;
    ImageButton wallet;
    ImageButton next;
    TextView name;
    TextView money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        options = findViewById(R.id.imageButtonOptions);
        trans = findViewById(R.id.buttonTrans);
        wallet = findViewById(R.id.imageButtonAddWallet);
        next = findViewById(R.id.imageButtonNextWallet);
        name = findViewById(R.id.textViewName);
        money = findViewById(R.id.textViewMoney);
        DBComands dbComands = new DBComands(this);
        SQLiteDatabase db = dbComands.getWritableDatabase();

        try {
            Integer number;
            Cursor cursor = db.query("Wallets", new String[]{"_id", "Name"}, null, null, null, null, null);
            cursor.moveToLast();
            name.setText(cursor.getString(cursor.getColumnIndex("Name")));
            number = cursor.getInt(cursor.getColumnIndex("_id"));
            money.setText(DBComands.getMoney(db, number).toString());
        }
        catch (Exception e){
            name.setText("Ни одного счёта не создано");
            money.setText("");
            dbComands.onCreate(db);
        }

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number;
                Cursor cursor = db.query("Wallets", new String[] {"_id", "Name"}, null, null, null, null, null);
                cursor.moveToFirst();
                do {
                    number = cursor.getString(cursor.getColumnIndex("Name"));
                    Log.d("DB", number);
                } while (cursor.moveToNext());
                //startActivity(new Intent(MainActivity.this, OptionsActivity.class));
                //for (int i = 0; i < 100; i++)
                //db.execSQL("drop table if exists DATABASE" + i);
                //db.execSQL("drop table if exists Wallets");
            }
        });

        trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TransActivity.class));
            }
        });

        wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, WalletActivity.class));
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer number;
                String sName;
                String searchName = name.getText().toString();
                Cursor cursor = db.query("Wallets", new String[] {"_id", "Name"}, null, null, null, null, null);
                cursor.moveToFirst();
                Log.d("DB", "1");

                do {
                    sName = cursor.getString(cursor.getColumnIndex("Name"));
                    Log.d("DB", "2++");
                    Log.d("DB", sName);
                    //Log.d("DB", cursor.getString(cursor.getColumnIndex("Name")));
                    Log.d("DB", searchName);
                    if (sName.equals(searchName) && (!cursor.moveToNext())) {
                        Log.d("DB", "2.4");
                        cursor.moveToFirst();
                        break;
                    }
                    else if (sName.equals(searchName)) {
                        Log.d("DB", "2.6");
                        break;
                    }
                    Log.d("DB", "2--");
                } while (cursor.moveToNext());
                Log.d("DB", "3");
                try {
                    name.setText(cursor.getString(cursor.getColumnIndex("Name")));
                    number = cursor.getInt(cursor.getColumnIndex("_id"));
                    money.setText(DBComands.getMoney(db, number).toString());
                }
                catch (Exception e) {
                    Log.d("DB", "e");
                    Log.d("DB", String.valueOf(cursor.moveToNext()));
                }
            }
        });
    }
}