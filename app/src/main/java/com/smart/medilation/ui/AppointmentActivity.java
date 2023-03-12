package com.smart.medilation.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.smart.medilation.R;
import com.smart.medilation.model.AppointmentModel;
import com.smart.medilation.ui.admin.AddSlotsActivity;
import com.smart.medilation.utils.Constants;

public class AppointmentActivity extends AppCompatActivity {

    ImageView imageBack;
    Button btnRequest;

    String doctorId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);


        doctorId = getIntent().getStringExtra("doctorId");

        imageBack = findViewById(R.id.imageBack);
        btnRequest = findViewById(R.id.btnRequest);


        imageBack.setOnClickListener(v -> onBackPressed());
        btnRequest.setOnClickListener(v -> {

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null && user.isEmailVerified()) {
                FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                DatabaseReference mRef = mDatabase.getReference(Constants.Appointment);
                AppointmentModel model = new AppointmentModel(doctorId, user.getUid(), "5:01 PM", "3/10/2023", "Pending");
                mRef.push().setValue(model).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AppointmentActivity.this, "Add Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AppointmentActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
}