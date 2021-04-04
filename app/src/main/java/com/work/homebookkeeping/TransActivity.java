package com.work.homebookkeeping;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class TransActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    int val;
    Date date;
    SimpleDateFormat formatForDateNow;
    Button add;
    SwitchCompat inExCome;
    TextView inExComeText;
    EditText dateTrans;
    EditText sum;
    RadioButton ruble;
    RadioButton euro;
    RadioButton dollar;
    EditText comment;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trans);

        date = new Date();
        formatForDateNow = new SimpleDateFormat("dd.MM.yyyy");

        add = findViewById(R.id.buttonAdd);
        inExCome = findViewById(R.id.switchType);
        inExComeText = findViewById(R.id.textViewType);
        dateTrans = findViewById(R.id.editTextDate);
        sum = findViewById(R.id.editTextSum);
        ruble = findViewById(R.id.radioButtonRubles);
        euro = findViewById(R.id.radioButtonEuro);
        dollar = findViewById(R.id.radioButtonDollars);
        comment = findViewById(R.id.editTextComment);

        DBComands dbComands = new DBComands(this);
        SQLiteDatabase db = dbComands.getWritableDatabase();

        if (inExCome != null) {
            inExCome.setOnCheckedChangeListener(this);
        }

        dateTrans.setText(formatForDateNow.format(date));

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                String money = sum.getText().toString();
                if (ruble.isChecked())
                    val = 0;
                else if (euro.isChecked())
                    val = 1;
                else if (dollar.isChecked())
                    val = 2;
                if (!inExCome.isChecked())
                    money = "-" + money;
                DBComands.addTrans(db, intent.getStringExtra("name"), dateTrans.getText().toString(), money, val, comment.getText().toString());
                startActivity(new Intent(TransActivity.this, MainActivity.class));
            }
        });


    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b)
            inExComeText.setText(R.string.income);
        else
            inExComeText.setText(R.string.expense);
    }
}