package com.smart.medilation.ui.patient;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.android.material.card.MaterialCardView;
import com.smart.medilation.R;
import com.smart.medilation.ui.BaseActivity;
import com.smart.medilation.ui.MyAppointmentsActivity;
import com.smart.medilation.ui.ProfileActivity;

public class PatientDashboardActivity extends BaseActivity {

    ImageView imageBack, imgLogout, imgProfile;
    MaterialCardView btnSchedule, btnMySchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_dashboard);

        btnSchedule = findViewById(R.id.btnSchedule);
        btnMySchedule = findViewById(R.id.btnMySchedule);

        imageBack = findViewById(R.id.imageBack);
        imgProfile = findViewById(R.id.imgProfile);
        imageBack.setOnClickListener(v -> onBackPressed());
        imgLogout = findViewById(R.id.imgLogout);
        imgLogout.setOnClickListener(v -> showLogoutDialog());
        imgProfile.setOnClickListener(v -> {
            Intent intent = new Intent(PatientDashboardActivity.this, ProfileActivity.class);
            intent.putExtra("fromDoctor", pref.getIsDocLogin());
            startActivity(intent);
        });
        btnSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(PatientDashboardActivity.this, SelectDomainActivity.class);
            startActivity(intent);
        });
        btnMySchedule.setOnClickListener(v -> {
            Intent intent = new Intent(PatientDashboardActivity.this, MyAppointmentsActivity.class);
            startActivity(intent);
        });

    }

}