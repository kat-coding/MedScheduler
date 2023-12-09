package com.example.medscheduler;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.time.LocalDateTime;

public class AddMedicationFormView extends AppCompatActivity implements AdapterView.OnItemSelectedListener  {
    Spinner spinner;
    Button addMed, cancel;
    int multiplier;
    DatabaseManager dbManager;
    EditText number, medName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medication_form_view);
        number = findViewById(R.id.editTextFrequency);
        medName = findViewById(R.id.editTextName);
        dbManager = MainActivity.dbManager;
        cancel = findViewById(R.id.btnCancel);

        spinner = findViewById(R.id.spinnerChoice);
        String [] spinnerOptions = getResources().getStringArray(R.array.duration);
        //adapter to use array in spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item ,spinnerOptions);

        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);
        addMed = findViewById(R.id.btnAddMed);
        addMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String medication = medName.getText().toString();
                int numberInput = Integer.parseInt(number.getText().toString());
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    LocalDateTime next = LocalDateTime.now();

                dbManager.insert(medication, next, next, numberInput*multiplier);
                Intent backToMain = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(backToMain);
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToMain = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(backToMain);
            }
        });
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selected = adapterView.getItemAtPosition(i).toString();
        switch(selected){
            case "Hours" :
                multiplier=1;
                break;
            case "Days" :
                multiplier = 24;
                break;
            case "Weeks" :
                multiplier = 168;
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Toast.makeText(getApplicationContext(), "Please make a selection", Toast.LENGTH_LONG).show();
    }
}