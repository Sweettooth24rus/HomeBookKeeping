package com.work.homebookkeeping;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class WalletActivity extends AppCompatActivity {

    Button add;
    EditText walletName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_wallet);

        add = findViewById(R.id.buttonAdd);
        walletName = findViewById(R.id.editTextWalletName);
        DBComands dbComands = new DBComands(this);
        SQLiteDatabase db = dbComands.getWritableDatabase();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("DB", "start write");
                Log.d("DB", walletName.getText().toString());
                dbComands.AddWallet(db, walletName.getText().toString());
                Log.d("DB", "EndAddWallet");
                startActivity(new Intent(WalletActivity.this, MainActivity.class));
            }
        });
    }
}
