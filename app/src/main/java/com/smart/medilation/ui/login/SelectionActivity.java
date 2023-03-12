package com.smart.medilation.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.card.MaterialCardView;
import com.smart.medilation.R;

public class SelectionActivity extends AppCompatActivity {

    MaterialCardView btn_doctor, btn_patient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        btn_doctor = findViewById(R.id.btn_doctor);
        btn_patient = findViewById(R.id.btn_patient);

        btn_doctor.setOnClickListener(v -> {
            Intent intent = new Intent(SelectionActivity.this, LoginActivity.class);
            intent.putExtra("fromDoctor", true);
            startActivity(intent);
        });
        btn_patient.setOnClickListener(v -> {
            Intent intent = new Intent(SelectionActivity.this, LoginActivity.class);
            intent.putExtra("fromDoctor", false);
            startActivity(intent);
        });
    }
}