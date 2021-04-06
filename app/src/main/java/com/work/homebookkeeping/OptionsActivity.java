package com.work.homebookkeeping;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

@SuppressLint("UseSwitchCompatOrMaterialCode")
public class OptionsActivity extends AppCompatActivity {

    SwitchCompat switchAuto;
    EditText euro;
    EditText dollar;
    RadioButton never;
    RadioButton exit;
    RadioButton everyday;
    Button sync;
    Button load;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        switchAuto = findViewById(R.id.switchCourse);
        euro = findViewById(R.id.editTextNumberEuro);
        dollar = findViewById(R.id.editTextNumberDollars);
        never = findViewById(R.id.radioButtonSyncNever);
        exit = findViewById(R.id.radioButtonSyncExit);
        everyday = findViewById(R.id.radioButtonSyncDay);
        sync = findViewById(R.id.buttonSync);
        load = findViewById(R.id.buttonLoad);

        sharedPreferences = getSharedPreferences(getString(R.string.save_val_file), Context.MODE_PRIVATE);

        if (switchAuto != null) {
            switchAuto.setOnCheckedChangeListener(this::onCheckedChanged);
        }
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (!b) {
            euro.setEnabled(true);
            dollar.setEnabled(true);
        }
        else {
            euro.setEnabled(false);
            dollar.setEnabled(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.euro), euro.getText().toString());
        editor.putString(getString(R.string.dollars), dollar.getText().toString());
        editor.putBoolean(getString(R.string.file_online), switchAuto.isChecked());
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (sharedPreferences.contains(getString(R.string.euro))) {
                euro.setText(sharedPreferences.getString(getString(R.string.euro), ""));
            }
            if (sharedPreferences.contains(getString(R.string.dollars))) {
                dollar.setText(sharedPreferences.getString(getString(R.string.dollars), ""));
            }
            if (sharedPreferences.contains(getString(R.string.file_online))) {
                if (!sharedPreferences.getBoolean(getString(R.string.file_online), false))
                    switchAuto.setChecked(sharedPreferences.getBoolean(getString(R.string.file_online), false));
                else {
                    //Взятие с интернета
                }
            }
        }
        catch (Exception e) {
            Log.d("DB", e.getMessage());
        }
    }
}