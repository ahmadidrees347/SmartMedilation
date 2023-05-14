package com.smart.medilation.ui.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.android.material.card.MaterialCardView;
import com.smart.medilation.R;
import com.smart.medilation.ui.BaseActivity;

public class SelectionActivity extends BaseActivity {

    MaterialCardView btn_doctor, btn_patient, btn_admin;

    ImageView imgEmergency;
    private final String whatsAppChatUrl = "http://api.whatsapp.com/send?phone=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        btn_admin = findViewById(R.id.btn_admin);
        btn_doctor = findViewById(R.id.btn_doctor);
        btn_patient = findViewById(R.id.btn_patient);
        imgEmergency = findViewById(R.id.imgEmergency);

        imgEmergency.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(whatsAppChatUrl + "+923115082101"));
                startActivity(intent);
            } catch (Exception exp) {
                showToast(exp.getMessage());
            }
        });
        btn_admin.setOnClickListener(v -> {
            Intent intent = new Intent(SelectionActivity.this, LoginActivity.class);
            intent.putExtra("fromAdmin", true);
            startActivity(intent);
        });
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