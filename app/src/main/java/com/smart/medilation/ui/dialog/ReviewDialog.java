package com.smart.medilation.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.smart.medilation.R;

public class ReviewDialog extends Dialog {

    RatingBar ratingBar;
    EditText edtReview;
    Button btnSend;
    public ReviewInterface reviewInterface;

    public ReviewDialog(@NonNull Context context, ReviewInterface reviewInterface) {
        super(context);
        this.reviewInterface = reviewInterface;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_review);

        getWindow().setGravity(Gravity.CENTER);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ratingBar = findViewById(R.id.ratingBar);
        edtReview = findViewById(R.id.edtReview);
        btnSend = findViewById(R.id.btnSend);

        btnSend.setOnClickListener(v -> {
            String text = edtReview.getText().toString().trim();
            float rating = ratingBar.getRating();
            if (rating == 0.0f) {
                Toast.makeText(getContext(), "Need rating", Toast.LENGTH_SHORT).show();
            } else if (text.isEmpty()) {
                edtReview.setError("Need Text");
            } else {
                edtReview.setError(null);
                reviewInterface.review(text, rating);
                dismiss();
            }
        });

        setCancelable(false);
    }

    public interface ReviewInterface {
        void review(String text, float rating);
    }
}
