package com.smart.medilation.ui.payment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.smart.medilation.R;
import com.smart.medilation.model.AppointmentModel;
import com.smart.medilation.ui.BaseActivity;
import com.smart.medilation.ui.patient.SuccessActivity;
import com.smart.medilation.utils.Constants;

public class PaymentActivity extends BaseActivity {

    ImageView imageBack;
    CardView jazzCash, easyPaisa, cash;
    TextView txtPayment;
    long timeInMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        timeInMillis = getIntent().getLongExtra("timeInMillis", 0);
        AppointmentModel myModel = (AppointmentModel) getIntent().getSerializableExtra("myModel");
        txtPayment = findViewById(R.id.txtPayment);
        String paymentText = getString(R.string.total_payment) + " " + myModel.payment;
        txtPayment.setText(paymentText);

        imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(v -> onBackPressed());

        jazzCash = findViewById(R.id.jazzCash);
        easyPaisa = findViewById(R.id.easyPaisa);
        cash = findViewById(R.id.cash);
        jazzCash.setOnClickListener(v -> insertAppointmentRecord(myModel, "JazzCash", true));
        easyPaisa.setOnClickListener(v -> insertAppointmentRecord(myModel, "EasyPaisa", true));
        cash.setOnClickListener(v -> insertAppointmentRecord(myModel, "Cash", false));
    }

    private void insertAppointmentRecord(AppointmentModel myModel, String paymentType, boolean paymentReceive) {
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mRef = mDatabase.getReference(Constants.Appointment);
        DatabaseReference ref = mRef.push();
        myModel.id = ref.getKey();
        myModel.paymentType = paymentType;
        myModel.paymentReceive = paymentReceive;
        ref.setValue(myModel).addOnCompleteListener(task -> {
            dismissDialog();
            if (task.isSuccessful()) {
                showToast("Add Successfully");
                String msgSchedule = "Be Ready for you Appointment with doctor in 1 Hour ";
                String msg = "Your Appointment with doctor is Successfully schedule at time slot: "
                        + myModel.time + ", on " + myModel.date;
                String docMsg = "Appointment of " + myModel.patientName + " wanted to schedule at time slot: "
                        + myModel.time + ", on " + myModel.date;
                sendNotification(pref.getUserId(), "Appointment Scheduled", msg);
                sendNotification(pref.getUserId(), "Appointment Scheduled", msgSchedule, timeInMillis);
                sendNotification(myModel.doctorId, "New Appointment", docMsg);
                startActivity(new Intent(PaymentActivity.this, SuccessActivity.class));
            } else {
                showToast("" + task.getException());
                showToast("Msg " + task.getException());
            }
        });
    }
}