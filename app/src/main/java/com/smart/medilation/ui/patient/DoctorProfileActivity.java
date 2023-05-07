package com.smart.medilation.ui.patient;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.smart.medilation.R;
import com.smart.medilation.adapters.ReviewsAdapter;
import com.smart.medilation.model.DoctorModel;
import com.smart.medilation.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class DoctorProfileActivity extends BaseActivity {
    ImageView imageBack, imgChat, imgCall, imgLogout;

    CircularImageView image;
    TextView txtName, txtType, txtExp, txtCertificates, txtAbout,txtRating, txtNoReview;

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

        txtRating = findViewById(R.id.txtRating);
        txtAbout = findViewById(R.id.txtAbout);
        txtName = findViewById(R.id.txtName);
        txtType = findViewById(R.id.txtType);
        txtExp = findViewById(R.id.txtExp);
        txtCertificates = findViewById(R.id.txtCertificates);
        txtNoReview = findViewById(R.id.txtNoReview);

        DoctorModel myModel = (DoctorModel) getIntent().getSerializableExtra("myModel");
        List<DoctorModel.RatingModel> reviewList = myModel.jsonToArrayList(myModel.rating);
        RecyclerView recyclerReviews = findViewById(R.id.recyclerReviews);
        recyclerReviews.setLayoutManager(new LinearLayoutManager(this));
        ReviewsAdapter reviewsAdapter = new ReviewsAdapter(this, reviewList);
        recyclerReviews.setAdapter(reviewsAdapter);
        if (reviewList.isEmpty()) {
            txtNoReview.setVisibility(View.VISIBLE);
            recyclerReviews.setVisibility(View.GONE);
        } else {
            txtNoReview.setVisibility(View.GONE);
            recyclerReviews.setVisibility(View.VISIBLE);
        }

        btnRequest.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), RequestAppointmentActivity.class);
            intent.putExtra("myModel", myModel);
            startActivity(intent);
        });

        imgCall.setOnClickListener(v -> startDialer(myModel.phoneNum));
        imgChat.setOnClickListener(v -> startChat(myModel.phoneNum));

        Glide.with(this)
                .load(myModel.image)
                .placeholder(R.drawable.ic_user)
                .into(image);

        txtName.setText(myModel.name);
        txtAbout.setText(myModel.about);
        txtType.setText(myModel.specialization);
        txtExp.setText(myModel.experience);
        txtRating.setText(""+getRatingAvg(reviewList));
        txtCertificates.setText(myModel.qualification);
    }

    private double getRatingAvg(List<DoctorModel.RatingModel> ratings) {
        double totalRating = 0;
        for (DoctorModel.RatingModel rating : ratings) {
            totalRating += rating.rating;
        }

        if(totalRating == 0){
            return 4.5;
        } else {
            return totalRating / ratings.size();
        }
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