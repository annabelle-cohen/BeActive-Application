package com.example.beactive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.beactive.Calculation.Calculation;
import com.example.beactive.Libraries.mySharedPref;
import com.example.beactive.Services.Service_StepCounter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class BeActiveSecondActivity extends AppCompatActivity {
    private FloatingActionButton fButton_Start,fButton_Stop;
    private TextView stepsText,kmText,caloriesText;
    private Calculation calculation;
    private mySharedPref mySharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_be_active_second);

        fButton_Start = findViewById(R.id.floating_button_play);
        fButton_Stop = findViewById(R.id.floating_button_stop);

        stepsText = findViewById(R.id.text_progress_steps);
        kmText = findViewById(R.id.kilometers);
        caloriesText = findViewById(R.id.calories);

        if(isMyServiceRunning(Service_StepCounter.class)){
            fButton_Start.setVisibility(View.GONE);
            fButton_Stop.setVisibility(View.VISIBLE);
            mySharedPref = new mySharedPref(this);
            showTextBySharedPref();

        }else{
            setTextInit();
        }

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
                setTextInit();
                stopService();
            }
        });

    }

    private void showTextBySharedPref() {
        stepsText.setText(String.valueOf(mySharedPref.getInt("Steps",0)));
        caloriesText.setText(String.format("%.3f",Calculation.CALORIE_PER_STEP*mySharedPref.getInt("Steps",0))+" kcal");
         kmText.setText(String.format("%.3f",Calculation.KILOMETER_PER_STEP*mySharedPref.getInt("Steps",0))+" km");
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
    private void setTextInit(){
        stepsText.setText("0");
        caloriesText.setText("0.000 kcal");
        kmText.setText("0.000 km");

    }
    protected void setTextInFields(){
        stepsText.setText(String.valueOf(calculation.getSteps()));
        caloriesText.setText(String.format("%.3f",calculation.getCaloriesAccordingSteps())+" kcal");
        kmText.setText(String.format("%.3f",calculation.getKilometersAccordingSteps())+" km");
    }

    private void callToLocalBroadcastReceiver(){
        IntentFilter intentFilter = new IntentFilter(Service_StepCounter.BROADCAST_NEW_STEPS_DETECTED);
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,intentFilter);
        registerReceiver(myReceiver,intentFilter);
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent != null && intent.getAction().equals(Service_StepCounter.BROADCAST_NEW_STEPS_DETECTED)){
                Bundle bundle = intent.getExtras();
                calculation = (Calculation)bundle.getSerializable(Service_StepCounter.EXTRA_STEPS);
                setTextInFields();

            }
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        this.callToLocalBroadcastReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
    }




    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}