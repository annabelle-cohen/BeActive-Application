package com.example.beactive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class BeActiveMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_be_active_main);


    }

    @Override
    protected void onStart() {
        super.onStart();
        startActivity(new Intent(this,BeActiveSecondActivity.class));
    }
}