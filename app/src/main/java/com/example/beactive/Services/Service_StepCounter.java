package com.example.beactive.Services;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.beactive.BeActiveSecondActivity;
import com.example.beactive.Calculation.Calculation;
import com.example.beactive.Libraries.mySharedPref;
import com.example.beactive.R;

public class Service_StepCounter extends Service implements SensorEventListener {

    public static final String BROADCAST_NEW_STEPS_DETECTED = "com.example.beactive.NEW_STEPS_DETECTED";
    public static final String EXTRA_STEPS = "EXTRA_STEPS";
    public static final String START_FOREGROUND_SERVICE = "START_FOREGROUND_SERVICE";
    public static final String STOP_FOREGROUND_SERVICE = "STOP_FOREGROUND_SERVICE";
    public static final String PAUSE_FOREGROUND_SERVICE = "PAUSE_FOREGROUND_SERVICE";

    public static int NOTIFICATION_ID = 153;
    private int lastShowNotification = -1;
    public static String CHANNEL_ID="com.example.beactive.CHANNEL_ID_FORGROUND";
    public static String MAIN_ACTION = "com.example.beactive.Service_Step.action.main";

    private Boolean isServiceRunningRightNow = false,isSensorPresent,isFixation=true;
    private NotificationCompat.Builder notificationBuilder;
    PendingIntent pendingIntent;

    private SensorManager sensorManager;
    private Sensor sensor;
    private int stepCounter = 0;
    private Calculation calculation;
    private mySharedPref mySharedPref;




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("pttt","onStartCommand: "+(intent == null));
        if(intent == null){
            stopForeground(true);
            Log.e("err","in first if not sticky");
            return START_NOT_STICKY;

        }

        if(intent.getAction().equals(START_FOREGROUND_SERVICE)){
            if(isServiceRunningRightNow){
                Log.e("err","in second if start sticky");
                return START_STICKY;
            }

            isServiceRunningRightNow=true;
            startPedometer();
            notifyUserForForgroundService();
            refreshNotificationData();
            return START_STICKY;
        }else if(intent.getAction().equals(PAUSE_FOREGROUND_SERVICE)){

        }else if(intent.getAction().equals(STOP_FOREGROUND_SERVICE)){
            stopPedometer();
            stopForeground(true);
            stopSelf();
            isServiceRunningRightNow = false;
            return START_NOT_STICKY;
        }
        return START_STICKY;
    }

    private void registerListener() {
        sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void refreshNotificationData() {
    }

    private void notifyUserForForgroundService() {
        Log.e("err","in notify user");
        Intent notificationIntent = new Intent(this, BeActiveSecondActivity.class);
        notificationIntent.setAction(MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this,NOTIFICATION_ID,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder = getNotificationBuilder(this,CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_LOW);

        notificationBuilder.setContentIntent(pendingIntent)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_baseline_directions_run_24)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher_round))
                .setContentTitle("BeActive")
                .setContentText("Steps:"+stepCounter+" Calories: "+String.format("%.3f",calculation.getCaloriesAccordingSteps())+" Kilometers: "+String.format("%.3f",calculation.getKilometersAccordingSteps()));

        Notification notification = notificationBuilder.build();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(NOTIFICATION_ID,notification,ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
             }else{
                startForeground(NOTIFICATION_ID,notification);
            }

            if(NOTIFICATION_ID != lastShowNotification){
                final NotificationManager notificationManager = (NotificationManager)getSystemService(Service.NOTIFICATION_SERVICE);
                notificationManager.cancel(lastShowNotification);
            }

            lastShowNotification = NOTIFICATION_ID;

    }
    private void editNotification(){

        notificationBuilder.
                setContentText("Steps:"+stepCounter+"Calories: "+String.format("%.3f",calculation.getCaloriesAccordingSteps())+"Kilometers: "+String.format("%.3f",calculation.getKilometersAccordingSteps()));
        final NotificationManager notificationManager = (NotificationManager)getSystemService(Service.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID,notificationBuilder.build());
        mySharedPref.putInt("Steps",calculation.getSteps());
    }

    private NotificationCompat.Builder getNotificationBuilder(Context context, String channelId, int importanceLow) {

        NotificationCompat.Builder builder;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            prepareChannel(context,channelId,importanceLow);
            builder=new NotificationCompat.Builder(context,channelId);
        }else{
            builder = new NotificationCompat.Builder(context);
        }
        return builder;
    }

    private void prepareChannel(Context context, String channelId, int importanceLow) {
        final String appName = context.getString(R.string.app_name);
        String notifications_channel_description = "Monitor pedometer channel";
        String description = notifications_channel_description;
        final NotificationManager nm = (NotificationManager)getSystemService(Service.NOTIFICATION_SERVICE);
        if(nm != null){

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel nChannel = nm.getNotificationChannel(channelId);

                if(nChannel == null){
                    nChannel = new NotificationChannel(channelId,appName,importanceLow);
                    nChannel.setDescription(description);

                    nChannel.enableLights(true);
                    nChannel.setLightColor(Color.BLUE);

                    nm.createNotificationChannel(nChannel);
                }
            }

        }
    }

    private void stopPedometer() {
        sensorManager.unregisterListener(this);
        sensorManager=null;
        sensor=null;
        mySharedPref.putInt("Steps",0);
    }

    private void startPedometer() {

        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        mySharedPref = new mySharedPref(this);

        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)!=null){
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            calculation = new Calculation(0,0);
            isSensorPresent=true;
             registerListener();

        }else{
            isSensorPresent = false;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if(sensorEvent.sensor == sensor){
            if(isFixation){
                stepCounter = (int) sensorEvent.values[0];
                calculation.setSteps(stepCounter);
                calculation.calculateCalories();
                calculation.calculateKilometers();
                isFixation=false;
            }else{
                stepCounter++;
                calculation.setSteps(stepCounter);
                calculation.calculateCalories();
                calculation.calculateKilometers();
                Log.e("step count: ",stepCounter+" ");
                editNotification();
            }
            Bundle bundle = new Bundle();
            bundle.putSerializable(EXTRA_STEPS, calculation);
            Intent intent = new Intent(BROADCAST_NEW_STEPS_DETECTED);
            intent.putExtras(bundle);
            LocalBroadcastManager.getInstance(Service_StepCounter.this).sendBroadcast(intent);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

}
