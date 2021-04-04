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
        } catch (Exception e) {
            name.setText("Ни одного счёта не создано");
            money.setText("");
            dbComands.onCreate(db);
        }

        options.setOnClickListener(new View.OnClickListener() {
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
                //for (int i = 0; i < 100; i++)
                //db.execSQL("drop table if exists DATABASE" + i);
                //db.execSQL("drop table if exists Wallets");
                Integer number;
                String sName;
                String searchName = name.getText().toString();
                cursor = DBComands.getWallet(db, searchName);
                try {
                    //name.setText(cursor.getString(cursor.getColumnIndex("Name")));
                    number = cursor.getInt(cursor.getColumnIndex("_id"));
                    cursor = db.query("DATABASE" + number.toString(), new String[]{"_id", "DateTrans", "Sum", "Val", "Comment"}, null, null, null, null, null);
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
    }
}