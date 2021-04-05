package com.work.homebookkeeping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
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

    DBComands dbComands;
    SQLiteDatabase db;

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

        initTranses();
        fillFields(db);
        fillTranses(db);

        Log.d("DB", String.valueOf(R.string.euro));

        options.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Recycle")
            @Override
            public void onClick(View view) {

                HashMap<String, Double> a = new HashMap<String, Double>();
                a.put("Евро", (double) 90);
                a.put("Доллар", (double) 76);
                DBComands.setCourses(db, a);
                //startActivity(new Intent(MainActivity.this, OptionsActivity.class));
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

    @SuppressLint("SetTextI18n")
    void fillFields(SQLiteDatabase db) {
        int number;
        try {
            @SuppressLint("Recycle") Cursor cursor = db.query("Wallets", new String[]{"_id", "Name"}, null, null, null, null, null);
            cursor.moveToLast();
            name.setText(cursor.getString(cursor.getColumnIndex("Name")));
            number = cursor.getInt(cursor.getColumnIndex("_id"));
            money.setText(DBComands.getMoney(db, number).toString());
            trans.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            name.setText("Ни одного счёта не создано");
            money.setText("");
            dbComands.onUpgrade(db, 0, 1);
            trans.setVisibility(View.INVISIBLE);
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