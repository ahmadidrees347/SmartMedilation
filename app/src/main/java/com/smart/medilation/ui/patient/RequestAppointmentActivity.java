package com.smart.medilation.ui.patient;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;

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
import com.smart.medilation.model.AppointmentModel;
import com.smart.medilation.ui.BaseActivity;
import com.smart.medilation.utils.Constants;

import java.util.Calendar;
import java.util.Locale;

public class RequestAppointmentActivity extends BaseActivity {

    ImageView imageBack, imgLogout;
    Button btnRequest;
    DatePicker datePicker;
    TimePicker timePicker;
    Spinner spnTime, spnType;

    String doctorId = "";
    String strTime = "";
    String strType = "";
    String strDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_appointment);

        String name = getIntent().getStringExtra("name");
        String email = getIntent().getStringExtra("email");
        String phone = getIntent().getStringExtra("phone");
        String imageText = getIntent().getStringExtra("image");
        String exp = getIntent().getStringExtra("exp");
        String qualification = getIntent().getStringExtra("qualification");
        String specialization = getIntent().getStringExtra("specialization");

        doctorId = getIntent().getStringExtra("doctorId");

        imgLogout = findViewById(R.id.imgLogout);
        imgLogout.setOnClickListener(v -> showLogoutDialog());
        spnTime = findViewById(R.id.spnTime);
        spnType = findViewById(R.id.spnType);

        imageBack = findViewById(R.id.imageBack);
        btnRequest = findViewById(R.id.btnRequest);
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);

        TextView txtName = findViewById(R.id.txtName);
        TextView txtType = findViewById(R.id.txtType);


        txtName.setText(name);
        txtType.setText(specialization + " Specialist");
        CircularImageView image = findViewById(R.id.image);
        Glide.with(this)
                .load(imageText)
                .placeholder(R.drawable.ic_user)
                .into(image);

        int timePeriodAdditionInDate = (1000 * 60 * 60 * 24);
        long time = System.currentTimeMillis() + timePeriodAdditionInDate;
        datePicker.setMinDate(time);
        datePicker.setMaxDate(time + (timePeriodAdditionInDate * 7));

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        datePicker.init(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                (datePicker, year, month, dayOfMonth) -> {
                    strDate = "" + (month + 1) + "/" + dayOfMonth + "/" + year;
                    Log.e("date*", strDate);
                }
        );
        timePicker.setOnTimeChangedListener((timePicker, hourOfDay, minute) -> {
            Log.e("time*", "" + hourOfDay + "/" + minute);
            strTime = getTimeText(hourOfDay, minute);
            Log.e("time*", "" + strTime);
        });


        imageBack.setOnClickListener(v -> onBackPressed());
        btnRequest.setOnClickListener(v -> {
            strTime = spnTime.getSelectedItem().toString();
            strType = spnType.getSelectedItem().toString();
            if (strDate.isEmpty()) {
                showToast("Kindly Select Date");
                return;
            }
            if (strTime.equalsIgnoreCase("Select Time Slot")) {
                showToast("Kindly Select Time");
                return;
            }
            if (strType.equalsIgnoreCase("Select Appointment Type")) {
                showToast("Kindly Select Appointment Type");
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
                            AppointmentModel model = new AppointmentModel(id, doctorId, name, user.getUid(), pref.getUserName(), strTime, strDate, "Pending", strType, "Cash", false);
                            ref.setValue(model).addOnCompleteListener(task -> {
                                dismissDialog();
                                if (task.isSuccessful()) {
                                    showToast("Add Successfully");
                                    startActivity(new Intent(RequestAppointmentActivity.this, SuccessActivity.class));
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
        });
    }

    private String getTimeText(int hourOfDay, int minute) {
        int hour = hourOfDay % 12;
        if (hour == 0) hour = 12;
        String zone = "";
        if (hourOfDay > 12) zone = "PM";
        else zone = "AM";
        return java.lang.String.format(Locale.getDefault(), "%02d:%02d %s", hour, minute, zone);
    }
}