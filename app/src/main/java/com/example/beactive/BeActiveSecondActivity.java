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
import android.widget.ImageView;
import android.widget.TextView;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.beactive.Calculation.Calculation;
import com.example.beactive.Libraries.mySharedPref;
import com.example.beactive.Services.Service_StepCounter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import petrov.kristiyan.colorpicker.ColorPicker;

public class BeActiveSecondActivity extends AppCompatActivity implements CustomDialog.CustomDialogListener  {
    private FloatingActionButton fButton_Start,fButton_Stop;
    private ImageView ring,iconAnimation;
    private TextView stepsText,kmText,caloriesText;
    private MeowBottomNavigation meowBottomNavigation;
    private Calculation calculation;
    private mySharedPref mySharedPref;
    private final int BE_ACTIVE = 0;
    private final int COLOR=1;
    private final int ICON_GENDER=2;
    private final int DEFAULT_COLOR=-12926467;
    private final String COLOR_RING= "colorRing";
    private final String ICON_CHOICE= "iconChoice";
    private final String STEPS_KEY = "Steps";
    private CustomDialog customDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_be_active_second);

        fButton_Start = findViewById(R.id.floating_button_play);
        fButton_Stop = findViewById(R.id.floating_button_stop);
        meowBottomNavigation = findViewById(R.id.botton_navigation_meow);
        ring = findViewById(R.id.progresbar);
        iconAnimation = findViewById(R.id.icon_walking);

        meowBottomNavigation.add(new MeowBottomNavigation.Model(ICON_GENDER,R.drawable.ic_baseline_wc_24));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(COLOR,R.drawable.ic_baseline_color_lens_24));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(BE_ACTIVE,R.drawable.ic_baseline_directions_run_24));

        stepsText = findViewById(R.id.text_progress_steps);
        kmText = findViewById(R.id.kilometers);
        caloriesText = findViewById(R.id.calories);

        mySharedPref = new mySharedPref(this);
        int colorRing = mySharedPref.getInt(COLOR_RING,DEFAULT_COLOR);
        String iconChoice = mySharedPref.getString(ICON_CHOICE,getString(R.string.genderFemale));

        changeIconAnim(iconChoice);

        if(colorRing != DEFAULT_COLOR){
            ring.setColorFilter(colorRing);
        }

        checkServices();

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

        meowBottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
                Log.e("Clicked item",item.getId()+" ");
            }
        });

        meowBottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                switch (item.getId()){
                    case BE_ACTIVE:
                         Log.d(getString(R.string.switchCase),item.getId()+"");
                        break;
                    case COLOR:
                         openColorPicker();
                        break;
                    case ICON_GENDER:
                         openDialog();
                        break;
                    default:
                         Log.d(getString(R.string.switchCase),item.getId()+"");

                }
            }
        });

        meowBottomNavigation.show(BE_ACTIVE,true);

    }

    private void openColorPicker(){
        final ColorPicker colorPicker = new ColorPicker(this);
        ArrayList<String>colors = new ArrayList<>();
        colors.add(getString(R.string.pink));
        colors.add(getString(R.string.green));
        colors.add(getString(R.string.purple));
        colors.add(getString(R.string.yellow));
        colors.add(getString(R.string.orange));
        colors.add(getString(R.string.blue2));

        colorPicker.setColors(colors).setColumns(6).setRoundColorButton(true).setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
            @Override
            public void onChooseColor(int position, int color) {
                if(color != DEFAULT_COLOR){
                    ring.setColorFilter(color);
                    mySharedPref.putInt(COLOR_RING,color);
                }else{
                    ring.setColorFilter(null);
                    mySharedPref.putInt(COLOR_RING,DEFAULT_COLOR);
                }
                meowBottomNavigation.show(BE_ACTIVE,true);
            }

            @Override
            public void onCancel() {
                meowBottomNavigation.show(BE_ACTIVE,true);
            }
        }).show();

    }
    private void showTextBySharedPref() {
        stepsText.setText(String.valueOf(mySharedPref.getInt(STEPS_KEY,0)));
        caloriesText.setText(String.format("%.2f",Calculation.CALORIE_PER_STEP*mySharedPref.getInt(STEPS_KEY,0))+" kcal");
         kmText.setText(String.format("%.2f",Calculation.KILOMETER_PER_STEP*mySharedPref.getInt(STEPS_KEY,0))+" km");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void startService() {

        actionToService(Service_StepCounter.START_FOREGROUND_SERVICE);
    }

    private void checkServices(){
        if(isMyServiceRunning(Service_StepCounter.class)){
            fButton_Start.setVisibility(View.GONE);
            fButton_Stop.setVisibility(View.VISIBLE);
            showTextBySharedPref();

        }else{
            setTextInit();
        }
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
        caloriesText.setText(getString(R.string.initCalories));
        kmText.setText(getString(R.string.initKilometers));

    }
    protected void setTextInFields(){
        stepsText.setText(String.valueOf(calculation.getSteps()));
        caloriesText.setText(String.format("%.2f",calculation.getCaloriesAccordingSteps())+" kcal");
        kmText.setText(String.format("%.2f",calculation.getKilometersAccordingSteps())+" km");
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
        checkServices();
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

    private void changeIconAnim(String choice){
        if(choice.equals(getString(R.string.genderMale))){
          iconAnimation.setBackgroundResource(R.drawable.man_walk_anim);
        }else{
            iconAnimation.setBackgroundResource(R.drawable.woman_walk_anim);
        }
        ((AnimationDrawable)iconAnimation.getBackground()).start();
    }

    @Override
    public void applyTexts(String choice) {
        mySharedPref.putString(ICON_CHOICE,choice);
        changeIconAnim(choice);
        customDialog.dismiss();
        meowBottomNavigation.show(BE_ACTIVE,true);
    }

    public void openDialog() {
        customDialog = new CustomDialog();
        customDialog.show(getSupportFragmentManager(), "Custom dialog");
    }

    @Override
    public void cancelApply(){
        meowBottomNavigation.show(BE_ACTIVE,true);
    }
}