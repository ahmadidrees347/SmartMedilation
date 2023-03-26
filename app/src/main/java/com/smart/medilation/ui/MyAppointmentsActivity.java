package com.smart.medilation.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smart.medilation.R;
import com.smart.medilation.adapters.AppointmentAdapter;
import com.smart.medilation.model.AppointmentModel;
import com.smart.medilation.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class MyAppointmentsActivity extends BaseActivity implements AppointmentAdapter.ClickListener {

    ImageView imageBack, imgLogout;
    TextView txtNoApp;
    Boolean fromDoctor = false;
    Boolean fromHistory = false;
    RecyclerView recyclerAppointments;
    AppointmentAdapter appointmentAdapter;
    List<AppointmentModel> appointmentList = new ArrayList<>();

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appointments);

        imgLogout = findViewById(R.id.imgLogout);
        imgLogout.setOnClickListener(v -> showLogoutDialog());

        fromDoctor = getIntent().getBooleanExtra("fromDoctor", false);
        fromHistory = getIntent().getBooleanExtra("fromHistory", false);
        showLDialog();

        txtNoApp = findViewById(R.id.txtNoApp);
        imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(v -> onBackPressed());

        recyclerAppointments = findViewById(R.id.recyclerAppointments);
        recyclerAppointments.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        appointmentAdapter = new AppointmentAdapter(getApplicationContext(), appointmentList, this);
        appointmentAdapter.isFromDoctor = fromDoctor;
        appointmentAdapter.isFromHistory = fromHistory;
        recyclerAppointments.setAdapter(appointmentAdapter);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference(Constants.Appointment);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointmentList.clear();
                String userID = mAuth.getCurrentUser().getUid();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    AppointmentModel user = child.getValue(AppointmentModel.class);
                    if (user != null &&
                            (user.doctorId.equalsIgnoreCase(userID) ||
                                    user.patientId.equalsIgnoreCase(userID))) {
                        if (fromHistory && !user.status.equalsIgnoreCase("Pending")) {
                            appointmentList.add(user);
                        }
                        if (!fromHistory && user.status.equalsIgnoreCase("Pending")) {
                            appointmentList.add(user);
                        }
                    }
                }
                appointmentAdapter.notifyDataSetChanged();
                dismissDialog();
                checkList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dismissDialog();
            }
        });
    }

    @Override
    public void onAppointmentClick(AppointmentModel model, boolean status, int position) {
        showLDialog();
        if (status) {
            model.status = "Schedule";
        } else {
            model.status = "Cancel";
        }
        mRef.child(model.id)
                .setValue(model)
                .addOnCompleteListener(task -> {
                    dismissDialog();
                    appointmentList.remove(position);
                    appointmentAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(task -> dismissDialog());

        showToast("Successfully updated user");

        checkList();
    }

    private void checkList() {
        if (appointmentList.isEmpty()) {
            txtNoApp.setVisibility(View.VISIBLE);
        } else {
            txtNoApp.setVisibility(View.GONE);
        }
    }
}