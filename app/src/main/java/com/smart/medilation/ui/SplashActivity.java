package com.smart.medilation.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.smart.medilation.R;
import com.smart.medilation.ui.login.SelectionActivity;
import com.smart.medilation.ui.patient.MainActivity;

import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                launchHomeScreen();
            }
        }, 3000);
        ImageView imgStart = findViewById(R.id.imgStart);
        imgStart.setOnClickListener(v -> launchHomeScreen());
    }


    private void launchHomeScreen() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.isEmailVerified()) {
            userId = user.getUid();
            if (pref.getIsDocLogin() && pref.getLogin())
                startActivity(new Intent(SplashActivity.this, DoctorDashboardActivity.class));
            else if (pref.getLogin())
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            else
                startActivity(new Intent(SplashActivity.this, SelectionActivity.class));
        } else {
            startActivity(new Intent(SplashActivity.this, SelectionActivity.class));
        }
        finish();
    }
}