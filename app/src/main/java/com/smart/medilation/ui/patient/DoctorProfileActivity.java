package com.smart.medilation.ui.patient;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.smart.medilation.R;
import com.smart.medilation.adapters.DateAdapter;
import com.smart.medilation.adapters.TimeSlotAdapter;
import com.smart.medilation.model.AppointmentModel;
import com.smart.medilation.model.DateModel;
import com.smart.medilation.ui.BaseActivity;
import com.smart.medilation.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class DoctorProfileActivity extends BaseActivity {
    ImageView imageBack, imgChat, imgCall, imgLogout;

    CircularImageView image;
    TextView txtName, txtType, txtExp, txtCertificates;

    Button btnRequest;

    List<DateModel> dateList = new ArrayList<>();
    List<String> timeSlotList = new ArrayList<>();
    DateAdapter dateAdapter;
    TimeSlotAdapter timeSlotAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);

        dateList = getNextDays();
        RecyclerView recyclerDate = findViewById(R.id.recyclerDate);
        recyclerDate.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        dateAdapter = new DateAdapter(this, dateList);
        recyclerDate.setAdapter(dateAdapter);

        timeSlotList = getTimeSlots();
        RecyclerView recyclerTime = findViewById(R.id.recyclerTime);
        recyclerTime.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        timeSlotAdapter = new TimeSlotAdapter(this, timeSlotList);
        recyclerTime.setAdapter(timeSlotAdapter);

        imgLogout = findViewById(R.id.imgLogout);
        imgLogout.setOnClickListener(v -> showLogoutDialog());
        image = findViewById(R.id.image);
        imageBack = findViewById(R.id.imageBack);
        imgChat = findViewById(R.id.imgChat);
        imgCall = findViewById(R.id.imgCall);
        btnRequest = findViewById(R.id.btnRequest);
        imageBack.setOnClickListener(v -> onBackPressed());


        String doctorId = getIntent().getStringExtra("doctorId");
        String name = getIntent().getStringExtra("name");
        String email = getIntent().getStringExtra("email");
        String phone = getIntent().getStringExtra("phone");
        String imageText = getIntent().getStringExtra("image");
        String exp = getIntent().getStringExtra("exp");
        String qualification = getIntent().getStringExtra("qualification");
        String specialization = getIntent().getStringExtra("specialization");
        btnRequest.setOnClickListener(v -> {
            /*Intent intent = new Intent(getApplicationContext(), RequestAppointmentActivity.class);
            intent.putExtra("doctorId", doctorId);
            intent.putExtra("name", name);
            intent.putExtra("email", email);
            intent.putExtra("phone", phone);
            intent.putExtra("image", imageText);
            intent.putExtra("exp", exp);
            intent.putExtra("qualification", qualification);
            intent.putExtra("specialization", specialization);
            startActivity(intent);*/

            bookAppointment(doctorId, name, timeSlotAdapter.getSelectedItem(), dateAdapter.getSelectedItem());
        });

        imgCall.setOnClickListener(v -> startDialer(phone));
        imgChat.setOnClickListener(v -> startChat(phone));

        Glide.with(this)
                .load(imageText)
                .placeholder(R.drawable.ic_user)
                .into(image);

        txtName = findViewById(R.id.txtName);
        txtType = findViewById(R.id.txtType);
        txtExp = findViewById(R.id.txtExp);
        txtCertificates = findViewById(R.id.txtCertificates);

        txtName.setText(name);
        txtType.setText(specialization + " Specialist");
        txtExp.setText(exp + " Years");
        txtCertificates.setText(qualification);
    }

    private void startDialer(String tel) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + tel));
        startActivity(intent);
    }

    private void startChat(String tel) {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:" + tel));
        startActivity(sendIntent);
    }

    public void bookAppointment(String doctorId, String name, String strTime, String strDate) {
        String strType = "";
        if (strDate.isEmpty()) {
            showToast("Kindly Select Date");
            return;
        }
        if (strTime.equalsIgnoreCase("Select Time Slot")) {
            showToast("Kindly Select Time");
            return;
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && userVerification(user)) {
            showLDialog();
            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
            DatabaseReference mRef = mDatabase.getReference(Constants.Appointment);
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean isAlreadyHasAppointment = false;
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        AppointmentModel model = child.getValue(AppointmentModel.class);
                        if (model != null
                                && model.time.equalsIgnoreCase(strTime)
                                && model.date.equalsIgnoreCase(strDate)) {
                            isAlreadyHasAppointment = true;
                            break;
                        }
                    }
                    if (!isAlreadyHasAppointment) {
                        DatabaseReference ref = mRef.push();
                        String id = ref.getKey();
                        String status = "Pending";
                        String paymentType = "Cash";
                        boolean paymentReceive = false;
                        AppointmentModel model = new AppointmentModel(id, doctorId, name, user.getUid(),
                                pref.getUserName(), strTime, strDate, status, strType,
                                paymentType, paymentReceive);
                        ref.setValue(model).addOnCompleteListener(task -> {
                            dismissDialog();
                            if (task.isSuccessful()) {
                                showToast("Add Successfully");
                                String msg = "Your Appointment with doctor is Successfully schedule at" + strTime + "," + strDate;
                                String docMsg = "Appointment of " + name + " wanted to schedule at " + strTime + "," + strDate;
                                sendNotification(pref.getUserId(), "Appointment Scheduled", msg);
                                sendNotification(doctorId, "New Appointment", docMsg);
                                startActivity(new Intent(DoctorProfileActivity.this, SuccessActivity.class));
                            } else {
                                showToast("" + task.getException());
                                showToast("Msg " + task.getException());
                            }
                        });
                    } else {
                        dismissDialog();
                        showToast("Doctor has already an appointment at specific Date & time.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    dismissDialog();
                }
            });
        } else {
            showToast("User is Not Verified");
        }
    }
}