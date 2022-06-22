package com.dc.doctor_communication.SplashScreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.dc.doctor_communication.R;
import com.dc.doctor_communication.SignActivity;
import com.google.firebase.FirebaseApp;

public class SplashActivity extends AppCompatActivity {
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);


        handler.postDelayed(() -> {
            Intent intent = new Intent(getApplicationContext(), SignActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            overridePendingTransition(R.anim.translate_none, R.anim.translate_none);
            finish();
        }, 2000);
    }
}