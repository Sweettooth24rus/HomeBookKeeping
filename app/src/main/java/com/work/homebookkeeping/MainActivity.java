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

public class MainActivity extends AppCompatActivity {

    ImageButton options;
    Button trans;
    ImageButton wallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        options = findViewById(R.id.imageButtonOptions);
        trans = findViewById(R.id.buttonTrans);
        wallet = findViewById(R.id.imageButtonAddWallet);
        DBComands dbComands = new DBComands(this);
        SQLiteDatabase db = dbComands.getWritableDatabase();

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number;
                Cursor cursor = db.query("Wallets", new String[] {"_id", "Name"}, null, null, null, null, null);
                Log.d("DB", "Cursor");
                cursor.moveToFirst();
                while (cursor.moveToNext()) {
                    number = cursor.getString(cursor.getColumnIndex("Name"));
                    Log.d("DB", number);
                }
                //startActivity(new Intent(MainActivity.this, OptionsActivity.class));
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
    }


}