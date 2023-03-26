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
import com.smart.medilation.ui.BaseActivity;

public class DoctorProfileActivity extends BaseActivity {
    ImageView imageBack, imgChat, imgCall, imgLogout;

    CircularImageView image;
    TextView txtName, txtType, txtExp, txtCertificates;

    Button btnRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);


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
            Intent intent = new Intent(getApplicationContext(), RequestAppointmentActivity.class);
            intent.putExtra("doctorId", doctorId);
            intent.putExtra("name", name);
            intent.putExtra("email", email);
            intent.putExtra("phone", phone);
            intent.putExtra("image", imageText);
            intent.putExtra("exp", exp);
            intent.putExtra("qualification", qualification);
            intent.putExtra("specialization", specialization);
            startActivity(intent);
        });

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