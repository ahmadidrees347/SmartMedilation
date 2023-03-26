package com.smart.medilation.ui.patient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.smart.medilation.R;
import com.smart.medilation.ui.SplashActivity;
import com.smart.medilation.ui.login.SelectionActivity;

public class SuccessActivity extends AppCompatActivity {

    Button btnDone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        btnDone = findViewById(R.id.btnDone);
        btnDone.setOnClickListener(v-> {
            onBackPressed();
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SuccessActivity.this, PatientDashboardActivity.class));
        finishAffinity();
    }
}