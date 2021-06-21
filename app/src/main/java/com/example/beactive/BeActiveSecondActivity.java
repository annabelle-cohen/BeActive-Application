package com.example.beactive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.example.beactive.Services.Service_StepCounter;

public class BeActiveSecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_be_active_second);

        startService();
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