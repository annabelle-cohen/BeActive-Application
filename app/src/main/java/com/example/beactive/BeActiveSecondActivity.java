package com.example.beactive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.beactive.Services.Service_StepCounter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class BeActiveSecondActivity extends AppCompatActivity {
    private FloatingActionButton fButton_Start,fButton_Stop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_be_active_second);

        fButton_Start = findViewById(R.id.floating_button_play);
        fButton_Stop = findViewById(R.id.floating_button_stop);

        fButton_Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fButton_Start.setVisibility(View.GONE);
                fButton_Stop.setVisibility(View.VISIBLE);
                startService();
            }
        });

        fButton_Stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fButton_Start.setVisibility(View.VISIBLE);
                fButton_Stop.setVisibility(View.GONE);
                stopService();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        ((AnimationDrawable)findViewById(R.id.female_walking).getBackground()).start();
    }

    private void startService() {

        actionToService(Service_StepCounter.START_FOREGROUND_SERVICE);
    }

    private void stopService(){
        actionToService(Service_StepCounter.STOP_FOREGROUND_SERVICE);
    }

    private void actionToService(String action){
        Intent startIntent = new Intent(BeActiveSecondActivity.this, Service_StepCounter.class);
        startIntent.setAction(action);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startForegroundService(startIntent);
        }else{
            startService(startIntent);
        }

    }

}