package com.work.homebookkeeping;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class OptionsActivity extends AppCompatActivity {

    public static Integer euro = 90;
    public static Integer dollar = 76;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
    }
}