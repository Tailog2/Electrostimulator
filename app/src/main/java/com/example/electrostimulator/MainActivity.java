package com.example.electrostimulator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Build;
import android.view.View;
import android.widget.*;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private int timeLeft;
    private int timePassed;
    private boolean stateStartButton = false;
    private int mode;
    private Thread timer;
    private Date startTime;


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Save save = new Save(MainActivity.this);
        final Button buttonStart= findViewById(R.id.start_button);

        final Button buttonOptions = findViewById(R.id.button_options);
        final Button buttonConnection = findViewById(R.id.button_connection);
        final Button buttonHistory = findViewById(R.id.button_history);
        final Button buttonInstruction= findViewById(R.id.button_instruction);

        final NumberPicker minutesDozens = findViewById(R.id.minutes_dozens_number);
        final NumberPicker minutesUnits = findViewById(R.id.minutes_units_number);

        Spinner spinnerOptions = findViewById(R.id.mode_options);
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(this, R.array.made_options_list,
                        android.R.layout.simple_spinner_item);
        staticAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOptions.setAdapter(staticAdapter);

        Intent goToNextActivity = new Intent();

        minutesDozens.setTextSize(80);
        minutesUnits.setTextSize(80);

        minutesDozens.setMinValue(0);
        minutesDozens.setMaxValue(6);
        minutesUnits.setMinValue(0);
        minutesUnits.setMaxValue(9);

        //Define start buttons
        buttonStart.setOnClickListener(v -> {
            timeLeft = minutesDozens.getValue() * 10 + minutesUnits.getValue();
            if (stateStartButton){
                Date finishTime = Calendar.getInstance().getTime();
                buttonStart.setText(R.string.start_button);
                stateStartButton = false;
                timer.interrupt();
                            }
            else if (timeLeft == 0){
                Toast.makeText(getApplicationContext(), "Choose the duration of session", Toast.LENGTH_SHORT).show();
            }
            else{
                startTime = Calendar.getInstance().getTime();
                mode = spinnerOptions.getId();
                //Define timer thread
                timer = new Thread(() -> {
                    while (timeLeft !=0){
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (stateStartButton){
                            timeLeft--;
                            timePassed++;
                            minutesDozens.setValue(timeLeft / 10);
                            minutesUnits.setValue(timeLeft % 10);
                        }
                        else{
                            break;
                        }
                    }
                    stateStartButton  = false;
                    buttonStart.setText(R.string.start_button);
                    Date finishTime = Calendar.getInstance().getTime();
                    save.saveHistory(mode, timePassed, startTime, finishTime);



                    timePassed = 0;
                }, "Timer_thread");

                buttonStart.setText(R.string.stop_button);
                stateStartButton = true;
                timer.start();
            }
        });

        //Define menu's buttons
        buttonOptions.setOnClickListener(v -> {
            goToNextActivity.setClass(MainActivity.this, Options.class);
            startActivity(goToNextActivity);
        });
        buttonConnection.setOnClickListener(v -> {
            goToNextActivity.setClass(MainActivity.this, Connection.class);
            startActivity(goToNextActivity);
        });
        buttonHistory.setOnClickListener(v -> {
            goToNextActivity.setClass(MainActivity.this, History.class);
            startActivity(goToNextActivity);
        });
        buttonInstruction.setOnClickListener(v -> {
            goToNextActivity.setClass(MainActivity.this, Instruction.class);
            startActivity(goToNextActivity);
        });
    }

    @Override
    protected void onDestroy() {
        System.out.println();
        System.out.println();
        super.onDestroy();
    }
}