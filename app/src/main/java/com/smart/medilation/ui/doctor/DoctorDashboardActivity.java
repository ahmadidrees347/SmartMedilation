package com.smart.medilation.ui.doctor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.smart.medilation.R;
import com.smart.medilation.ui.BaseActivity;
import com.smart.medilation.ui.MyAppointmentsActivity;
import com.smart.medilation.ui.ProfileActivity;
import com.smart.medilation.ui.patient.PatientDashboardActivity;
import com.smart.medilation.ui.patient.SelectDomainActivity;

public class DoctorDashboardActivity extends BaseActivity {

    ImageView imageBack, imgLogout, imgProfile;
    MaterialCardView btnHistory, btnMyAppointments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_dashboard);

        imgProfile = findViewById(R.id.imgProfile);
        imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(v -> onBackPressed());
        imgLogout = findViewById(R.id.imgLogout);
        imgLogout.setOnClickListener(v -> showLogoutDialog());
        imgProfile.setOnClickListener(v -> {
            Intent intent = new Intent(DoctorDashboardActivity.this, ProfileActivity.class);
            intent.putExtra("fromDoctor", pref.getIsDocLogin());
            startActivity(intent);
        });

        btnHistory = findViewById(R.id.btnHistory);
        btnMyAppointments = findViewById(R.id.btnMyAppointments);

        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(DoctorDashboardActivity.this, MyAppointmentsActivity.class);
            intent.putExtra("fromDoctor", pref.getIsDocLogin());
            intent.putExtra("fromHistory", true);
            startActivity(intent);
        });
        btnMyAppointments.setOnClickListener(v -> {
            Intent intent = new Intent(DoctorDashboardActivity.this, MyAppointmentsActivity.class);
            intent.putExtra("fromDoctor", pref.getIsDocLogin());
            startActivity(intent);
        });
    }
}