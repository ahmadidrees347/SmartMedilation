package com.smart.medilation.ui.login;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.smart.medilation.R;
import com.smart.medilation.ui.BaseActivity;

import java.util.Objects;
import java.util.regex.Pattern;

public class ResetPasswordActivity extends BaseActivity {

    ImageView imageBack;
    EditText edt_email;
    Button btn_reset_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        edt_email = findViewById(R.id.edt_email);
        imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(v -> onBackPressed());
        btn_reset_password = findViewById(R.id.btn_reset);
        btn_reset_password.setOnClickListener(v -> {
            String strEmail = edt_email.getText().toString().trim();
            if (isValidEmail(strEmail)) {
                showLDialog();
                FirebaseAuth.getInstance().sendPasswordResetEmail(strEmail).addOnCompleteListener(task -> {
                    dismissDialog();
                    if (task.isSuccessful()) {
                        showToast("Email sent Successfully!");
                        finish();
                    } else {
                        showToast("" + Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
            }
            else showToast("Email IS Not Valid!");
        });


    }

    private boolean isValidEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
}

