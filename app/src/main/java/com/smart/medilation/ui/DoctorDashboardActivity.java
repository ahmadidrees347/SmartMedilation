package com.smart.medilation.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smart.medilation.R;
import com.smart.medilation.adapters.CategoriesAdapter;
import com.smart.medilation.adapters.AppointmentAdapter;
import com.smart.medilation.model.AppointmentModel;
import com.smart.medilation.model.CategoriesModel;
import com.smart.medilation.ui.login.SelectionActivity;
import com.smart.medilation.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class DoctorDashboardActivity extends BaseActivity implements AppointmentAdapter.ClickListener {


    private FirebaseAuth mAuth;
    ImageView imageBack, imgLogout;
    RecyclerView recyclerReservations;
    AppointmentAdapter appointmentAdapter;
    List<AppointmentModel> appointmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_dashboard);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(v -> onBackPressed());
        imgLogout = findViewById(R.id.imgLogout);
        imgLogout.setOnClickListener(v -> showLogoutDialog());

        recyclerReservations = findViewById(R.id.recyclerReservations);
        recyclerReservations.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        appointmentAdapter = new AppointmentAdapter(getApplicationContext(), appointmentList, this);
        recyclerReservations.setAdapter(appointmentAdapter);


        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mRef = mDatabase.getReference(Constants.Appointment);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointmentList.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    AppointmentModel user = child.getValue(AppointmentModel.class);
                    appointmentList.add(user);
                }
                appointmentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onAppointmentClick(AppointmentModel model, boolean status) {
//        Intent intent = new Intent(DoctorDashboardActivity.this, DoctorsActivity.class);
//        intent.putExtra("category", model.getName());
//        startActivity(intent);
    }
}