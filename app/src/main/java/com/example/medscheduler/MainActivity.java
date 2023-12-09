package com.example.medscheduler;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    FloatingActionButton button;
    public static DatabaseManager dbManager;
    List<Medication> medList;

    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //list from database
        medList = new ArrayList<>();
        //fetch data from database and add to array
        dbManager = new DatabaseManager(this);
        dbManager.open();
        Cursor cursor = dbManager.fetch();
        if(cursor.moveToFirst()){
            do {
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));
                @SuppressLint("Range") String last = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_LAST_TAKEN));
                @SuppressLint("Range") String next = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NEXT_DOSE));
                @SuppressLint("Range") String duration = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_HOURS_BETWEEN));
                if(last == null){
                    last = "N/A";
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    medList.add(new Medication(id, name, LocalDateTime.parse(last),LocalDateTime.parse(next), Integer.parseInt(duration)));
                }
            }while(cursor.moveToNext());
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDateTime now = LocalDateTime.now();
                dbManager.insert("Tylenol", LocalDateTime.now(), now.plusHours(4),4);
            }
        }
        //set data to listView
        listView = findViewById(R.id.listView);
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.list_view, R.id.textViewMedName, medList);
        listView.setAdapter(adapter);
        //set listItem click event to go to medication page
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.pop_up_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String selectedId = medList.get(position).getId();
                        int edit = R.id.edit;
                        int takeMed = R.id.takeNow;
                        int delete = R.id.delete;
                        int menuItem = item.getItemId();

                        if (menuItem == edit) {

                                Intent editMedication = new Intent(getApplicationContext(), EditMedicationView.class);
                                editMedication.putExtra("id", selectedId);
                                startActivity(editMedication);
                        }
                        if (menuItem == takeMed) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                if (medList.get(position).getNextAvailable().isAfter(LocalDateTime.now())) {
                                    Toast.makeText(MainActivity.this, "Medication not available yet.", Toast.LENGTH_LONG).show();
                                } else {
                                    //int id, String name, LocalDateTime last, LocalDateTime next, int duration
                                    int curId = Integer.parseInt(selectedId);
                                    String curName = medList.get(position).getName();
                                    LocalDateTime curLast = null;
                                    LocalDateTime curNext = null;
                                    int curDuration = (int) medList.get(position).getHoursBetween();
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        curLast = LocalDateTime.now();
                                        curNext = curLast.plusHours(curDuration);
                                    }
                                    dbManager.update(curId, curName, curLast, curNext, curDuration);
                                    Toast.makeText(getApplicationContext(), "Medication taken", Toast.LENGTH_LONG).show();
                                    onRestart();
                                }
                            }
                        }
                        if (menuItem == delete) {
                            //Confirm delete before deleting with alert
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Permanently Delete Medication?");
                            builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int deletedId = Integer.parseInt(selectedId);
                                    dbManager.delete(deletedId);
                                    Toast.makeText(getApplicationContext(), "Medication Deleted", Toast.LENGTH_LONG).show();
                                    onRestart();
                                }
                            });
                            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    onRestart();
                                }
                            });
                            builder.show();
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
        //add button functionality
        button = findViewById(R.id.btnAdd);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addMedication = new Intent(getApplicationContext(), AddMedicationFormView.class);
                startActivity(addMedication);
            }
        });
    }
}