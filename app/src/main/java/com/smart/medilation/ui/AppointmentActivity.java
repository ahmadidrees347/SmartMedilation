package com.smart.medilation.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smart.medilation.R;
import com.smart.medilation.model.AppointmentModel;
import com.smart.medilation.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AppointmentActivity extends BaseActivity {

    ImageView imageBack;
    Button btnRequest;
    DatePicker datePicker;
    TimePicker timePicker;

    String doctorId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);


        doctorId = getIntent().getStringExtra("doctorId");

        imageBack = findViewById(R.id.imageBack);
        btnRequest = findViewById(R.id.btnRequest);
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);

        int timePeriodAdditionInDate = (1000 * 60 * 60 * 24);
        long time = System.currentTimeMillis() + timePeriodAdditionInDate;
        datePicker.setMinDate(time);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        datePicker.init(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                (datePicker, year, month, dayOfMonth) -> {
                    Log.e("date*", "" + (month + 1) + "/" + dayOfMonth + "/" + year);
                    SimpleDateFormat date = new SimpleDateFormat("MM dd yyyy", Locale.ENGLISH);
                    String parseDate = "" + (month + 1) + " " + dayOfMonth + " " + year;
                    try {
                        Log.e("date*", "" + date.parse(parseDate));
                    } catch (ParseException ignored) {
                    }
                }
        );
        timePicker.setOnTimeChangedListener((timePicker, hourOfDay, minute) -> {
            Log.e("time*", "" + hourOfDay + "/" + minute);
            Log.e("time*", "" + getTimeText(hourOfDay, minute));
        });


        imageBack.setOnClickListener(v -> onBackPressed());
        btnRequest.setOnClickListener(v -> {

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null && user.isEmailVerified()) {
                FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                DatabaseReference mRef = mDatabase.getReference(Constants.Appointment);
                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean isAlreadyHasRecord = false;
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            AppointmentModel model = child.getValue(AppointmentModel.class);
                            if (model != null
                                    && model.status.equalsIgnoreCase("Pending")
                                    && model.doctorId.equalsIgnoreCase(doctorId)
                                    && model.patientId.equalsIgnoreCase(user.getUid())) {
                                isAlreadyHasRecord = true;
                                break;
                            }
                        }
                        if (!isAlreadyHasRecord) {
                            AppointmentModel model = new AppointmentModel(doctorId, user.getUid(), "5:01 PM", "3/10/2023", "Pending");
                            mRef.push().setValue(model).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    showToast("Add Successfully");
                                } else {
                                    showToast("" + task.getException());
                                }
                            });
                        } else {
                            showToast("You has already has an appointment with Pending Status");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        dismissDialog();
                    }
                });


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