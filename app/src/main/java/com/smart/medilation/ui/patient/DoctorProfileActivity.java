package com.smart.medilation.ui.patient;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.smart.medilation.R;

public class DoctorProfileActivity extends AppCompatActivity {
    ImageView imageBack, imgChat, imgCall;

    CircularImageView image;
    TextView txtName, txtType, txtExp, txtCertificates;

    Button btnRequest;

    String doctorId = "";
    String name = "";
    String email = "";
    String phone = "";
    String imageText = "";
    String exp = "";
    String qualification = "";
    String specialization = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);

        image = findViewById(R.id.image);
        imageBack = findViewById(R.id.imageBack);
        imgChat = findViewById(R.id.imgChat);
        imgCall = findViewById(R.id.imgCall);
        btnRequest = findViewById(R.id.btnRequest);
        imageBack.setOnClickListener(v -> onBackPressed());
        btnRequest.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), RequestAppointmentActivity.class);
            intent.putExtra("doctorId", doctorId);
            startActivity(intent);
        });

        doctorId = getIntent().getStringExtra("doctorId");
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        phone = getIntent().getStringExtra("phone");
        imageText = getIntent().getStringExtra("image");
        exp = getIntent().getStringExtra("exp");
        qualification = getIntent().getStringExtra("qualification");
        specialization = getIntent().getStringExtra("specialization");

        imgChat.setOnClickListener(v -> startDialer(phone));
        imgCall.setOnClickListener(v -> startChat(phone));

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
}