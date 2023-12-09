package com.example.medscheduler;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.TimeZone;

public class EditMedicationView extends AppCompatActivity {
    DatabaseManager dbManager;
    Calendar newDate;
    LocalDateTime newTimeInput;
    ZoneId zoneId;
    TimeZone tz;
    ImageButton datePicker;
    EditText editTextLast, editTextName, editTextNumber;
    String id, last;
    Spinner spinner;
    Button takeNow;
    LocalDateTime fetchedNext, fetchedLast;
    String fetchedName, fetchedId;
    int fetchedDuration;


    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_medication_view);
        dbManager = MainActivity.dbManager;
        editTextName = findViewById(R.id.editTextName);
        editTextLast = findViewById(R.id.editTextLast);
        editTextNumber = findViewById(R.id.editTextNumber);
        takeNow = findViewById(R.id.btnDose);
        spinner = findViewById(R.id.spinnerChoice);
        String [] spinnerOptions = getResources().getStringArray(R.array.duration);
        //adapter to use array in spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item ,spinnerOptions);
        spinner.setAdapter(spinnerAdapter);

        //get data passed in intent
        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        int fetchID = Integer.parseInt(id);
        Cursor cursor = dbManager.fetchById(fetchID);
        if(cursor.moveToFirst()){
            fetchedId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
            fetchedName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));
            fetchedDuration = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_HOURS_BETWEEN)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                fetchedLast = LocalDateTime.parse(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_LAST_TAKEN)));
                fetchedNext = fetchedLast.plusHours(fetchedDuration);
            }

        }
        DateTimeFormatter formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm");
            last = fetchedLast.format(formatter);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(fetchedNext.isAfter(LocalDateTime.now())){
                takeNow.setEnabled(false);
                takeNow.setText("Medication Not Available Yet");
            }
        }

        //set last dose from intent
        editTextLast.setText(last);

        //set name from intent
        editTextName.setText(fetchedName);

        //set spinner selection and duration value
        String numberText;
        if(fetchedDuration%24 == 0){
            spinner.setSelection(1);
            numberText = String.valueOf(fetchedDuration/24);
            editTextNumber.setText(numberText);
        } else if (fetchedDuration%168 == 0) {
            numberText = String.valueOf(fetchedDuration/168);
            spinner.setSelection(2);
            editTextNumber.setText(numberText);
        }else{
            spinner.setSelection(0);
            numberText = String.valueOf(fetchedDuration);
            editTextNumber.setText(numberText);
        }



        datePicker = findViewById(R.id.imageButton);
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });


    }
    public void showDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance();
        Calendar date = Calendar.getInstance();
        new DatePickerDialog(EditMedicationView.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(EditMedicationView.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
                        Log.v("EDIT_MED_LOG", "The choosen one " + date.getTime());
                        newDate = date;
                        tz = newDate.getTimeZone();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            zoneId = tz.toZoneId();
                            newTimeInput = LocalDateTime.ofInstant(newDate.toInstant(), zoneId);
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm");
                            String formattedDateTime = newTimeInput.format(formatter);
                            editTextLast.setText(formattedDateTime);
                            Log.v("EDIT_MED_LOG", formattedDateTime);
                        }
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }
    public void backToMain(){
        Intent backToMain = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(backToMain);
    }

    public void deleteMedicationOnClick(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(EditMedicationView.this);
        builder.setTitle("Permanently Delete Medication?");
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int deletedId = Integer.parseInt(id);
                dbManager.delete(deletedId);
                backToMain();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                backToMain();
            }
        });
        builder.show();
    }
    public void editMedicationOnClick(View view){
        //int id, String name, LocalDateTime last, LocalDateTime next, int duration
        int editId = Integer.parseInt(id);
        String editName = editTextName.getText().toString();
        LocalDateTime last = newTimeInput;
        int duration = Integer.parseInt(editTextNumber.getText().toString());
        LocalDateTime next = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
             next = last.plusHours(duration);
        }
        dbManager.update(editId, editName, last, next, duration);
        backToMain();
    }

    public void giveMedicationOnClick(View view){
        //int id, String name, LocalDateTime last, LocalDateTime next, int duration
        int editId = Integer.parseInt(id);
        String editName = editTextName.getText().toString();
        LocalDateTime last = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            last = LocalDateTime.now();
        }
        int duration = Integer.parseInt(editTextNumber.getText().toString());
        LocalDateTime next = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            next = last.plusHours(fetchedDuration);
        }
        dbManager.update(editId, editName, last, next, duration);
        backToMain();
    }
}