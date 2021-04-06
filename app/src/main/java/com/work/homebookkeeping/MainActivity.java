package com.work.homebookkeeping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageButton options;
    Button trans;
    ImageButton wallet;
    ImageButton next;
    ImageButton delete;
    TextView name;
    TextView money;
    RecyclerView transes;
    TransAdapter transAdapter;

    SharedPreferences sharedPreferences;
    DBComands dbComands;
    SQLiteDatabase db;

    public static HashMap<String, Double> valute;

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

        dbComands = new DBComands(this);
        db = dbComands.getWritableDatabase();

        sharedPreferences = getSharedPreferences(getString(R.string.save_val_file), Context.MODE_PRIVATE);
        valute = new HashMap<String, Double>();

        /*try {
            valute.clear();
            valute.put(getString(R.string.roubles), 1d);
            valute.put(getString(R.string.euro), 90d);
            valute.put(getString(R.string.dollars), 76d);
            if (sharedPreferences.contains(getString(R.string.euro))) {
                valute.remove(getString(R.string.euro));
                valute.put(getString(R.string.euro), Double.parseDouble(sharedPreferences.getString(getString(R.string.euro), "90")));
            }
            if (sharedPreferences.contains(getString(R.string.dollars))) {
                valute.remove(getString(R.string.dollars));
                valute.put(getString(R.string.dollars), Double.parseDouble(sharedPreferences.getString(getString(R.string.dollars), "90")));
            }
        }
        catch (Exception e) {
            Log.d("DB", e.getMessage());
        }

        initTranses();
        fillFields(db);
        fillTranses(db);*/

        options.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Recycle")
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, OptionsActivity.class));
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
                Log.d("DB", "wallet");
                startActivity(new Intent(MainActivity.this, WalletActivity.class));
                Log.d("DB", "endwallet");
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
                fillTranses(db);
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
                        fillFields(db);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            valute.clear();
            valute.put(getString(R.string.roubles), 1d);
            valute.put(getString(R.string.euro), 90d);
            valute.put(getString(R.string.dollars), 76d);
            if (sharedPreferences.contains(getString(R.string.euro))) {
                valute.remove(getString(R.string.euro));
                valute.put(getString(R.string.euro), Double.parseDouble(sharedPreferences.getString(getString(R.string.euro), "90")));
            }
            if (sharedPreferences.contains(getString(R.string.dollars))) {
                valute.remove(getString(R.string.dollars));
                valute.put(getString(R.string.dollars), Double.parseDouble(sharedPreferences.getString(getString(R.string.dollars), "90")));
            }
        }
        catch (Exception e) {
            Log.d("DB", e.getMessage());
        }

        initTranses();
        fillFields(db);
        fillTranses(db);
    }

    @SuppressLint("SetTextI18n")
    void fillFields(SQLiteDatabase db) {
        int number;
        try {
            Log.d("DB", "1");
            @SuppressLint("Recycle") Cursor cursor = db.query("Wallets", new String[]{"_id", "Name"}, null, null, null, null, null);
            cursor.moveToLast();
            Log.d("DB", String.valueOf(cursor.getCount()));
            name.setText(cursor.getString(cursor.getColumnIndex("Name")));
            Log.d("DB", "3");
            number = cursor.getInt(cursor.getColumnIndex("_id"));
            Log.d("DB", "4");
            money.setText(DBComands.getMoney(db, number).toString());
            Log.d("DB", "5");
            trans.setVisibility(View.VISIBLE);
            Log.d("DB", "a");
        } catch (Exception e) {
            name.setText("Ни одного счёта не создано");
            money.setText("");
            dbComands.onUpgrade(db, 0, 1);
            trans.setVisibility(View.INVISIBLE);
            Log.d("DB", e.getMessage());
        }
    }

    private void fillTranses(SQLiteDatabase db) {
        transAdapter.clearItems();
        List<Trans> trans = DBComands.getTranses(db, name.getText().toString());
        transAdapter.setItems(trans);
    }

    void initTranses() {
        transes = findViewById(R.id.recycler);
        transes.setLayoutManager(new LinearLayoutManager(this));
        transAdapter = new TransAdapter();
        transes.setAdapter(transAdapter);
    }
}