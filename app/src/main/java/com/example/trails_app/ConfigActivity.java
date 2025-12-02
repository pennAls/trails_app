package com.example.trails_app;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class ConfigActivity extends AppCompatActivity {

    private TextInputEditText etWeight, etHeight, etBirthDate;
    private RadioGroup rgGender, rgMapType, rgNavMode;
    private RadioButton rbMapVector, rbMapSatellite, rbNavNorthUp, rbNavCourseUp, rbMale, rbFemale;
    private Button btnSave, btnCancel;

    private SharedPreferences sharedPrefs;
    private static final String PREF_NAME = "UserConfigs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        sharedPrefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        initViews();
        loadData();
        setupListeners();
    }

    private void initViews() {
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        etBirthDate = findViewById(R.id.etBirthDate);
        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        rgMapType = findViewById(R.id.rgMapType);
        rbMapVector = findViewById(R.id.rbMapVector);
        rbMapSatellite = findViewById(R.id.rbMapSatellite);
        rgNavMode = findViewById(R.id.rgNavMode);
        rbNavNorthUp = findViewById(R.id.rbNavNorthUp);
        rbNavCourseUp = findViewById(R.id.rbNavCourseUp);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void loadData() {
        etWeight.setText(sharedPrefs.getString("weight", ""));
        etHeight.setText(sharedPrefs.getString("height", ""));
        etBirthDate.setText(sharedPrefs.getString("birthDate", ""));
        String gender = sharedPrefs.getString("gender", "");
        if (gender.equals("F")) {
            rbFemale.setChecked(true);
        } else {
            rbMale.setChecked(true);
        }

        int mapType = sharedPrefs.getInt("mapType", 1); // 1 = Vector, 2 = Satellite
        if (mapType == 2) {
            rbMapSatellite.setChecked(true);
        } else {
            rbMapVector.setChecked(true);
        }
        int navMode = sharedPrefs.getInt("navMode", 1); // 1 = North Up, 2 = Course Up
        if (navMode == 2) {
            rbNavCourseUp.setChecked(true);
        } else {
            rbNavNorthUp.setChecked(true);
        }
    }

    private void setupListeners() {
        etBirthDate.setOnClickListener(v -> showDatePicker());
        btnCancel.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveData());
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String selectedDate = String.format("%02d/%02d/%d", dayOfMonth, (monthOfYear + 1), year1);
                    etBirthDate.setText(selectedDate);
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void saveData() {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("weight", etWeight.getText().toString());
        editor.putString("height", etHeight.getText().toString());
        editor.putString("birthDate", etBirthDate.getText().toString());
        if (rbFemale.isChecked()) {
            editor.putString("gender", "F");
        } else {
            editor.putString("gender", "M");
        }
        if (rbMapSatellite.isChecked()) {
            editor.putInt("mapType", 2); // Satélite
        } else {
            editor.putInt("mapType", 1); // Vetorial
        }

        if (rbNavCourseUp.isChecked()) {
            editor.putInt("navMode", 2); // Course Up
        } else {
            editor.putInt("navMode", 1); // North Up
        }

        editor.apply();
        Toast.makeText(this, "Configurações salvas!", Toast.LENGTH_SHORT).show();
        finish();
    }
}