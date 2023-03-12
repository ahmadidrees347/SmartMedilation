package com.smart.medilation.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.smart.medilation.R;
import com.smart.medilation.model.DoctorModel;
import com.smart.medilation.model.PatientModel;
import com.smart.medilation.ui.BaseActivity;
import com.smart.medilation.ui.DoctorDashboardActivity;
import com.smart.medilation.ui.MainActivity;
import com.smart.medilation.ui.admin.AdminActivity;

import java.util.regex.Pattern;

public class LoginActivity extends BaseActivity {
    TextView txtTitle, btn_register;
    ImageView imageBack, imgAdmin;
    CircularImageView image;
    EditText edt_email, edt_password;
    Button btn_login;
    //defining Firebase Auth object
    private FirebaseAuth mAuth;
    private boolean fromDoctor = false;
    private boolean fromAdmin = false;
    FirebaseDatabase mDatabase;

    private boolean isValidEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fromDoctor = getIntent().getBooleanExtra("fromDoctor", false);
        fromAdmin = getIntent().getBooleanExtra("fromAdmin", false);

        txtTitle = findViewById(R.id.txtTitle);
        image = findViewById(R.id.image);
        if (fromAdmin) {
            txtTitle.setText(getString(R.string.login_as_admin));
            image.setImageResource(R.drawable.ic_adm);
        } else if (fromDoctor) {
            txtTitle.setText(getString(R.string.login_as_doctor));
            image.setImageResource(R.drawable.ic_doc);
        } else {
            txtTitle.setText(getString(R.string.login_as_patient));
            image.setImageResource(R.drawable.ic_patient);
        }

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance();

        imageBack = findViewById(R.id.imageBack);
        imgAdmin = findViewById(R.id.imgAdmin);
        imageBack.setOnClickListener(v -> onBackPressed());
        imgAdmin.setOnClickListener(v -> {
            Intent mainPage = new Intent(getApplicationContext(), AdminActivity.class);
            startActivity(mainPage);
        });

        edt_email = findViewById(R.id.edt_email);
        edt_password = findViewById(R.id.edt_password);

        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(v -> {
            if (fromAdmin) {
                Toast.makeText(LoginActivity.this, "Admin can not be Registered!", Toast.LENGTH_SHORT).show();
            } else {
                Intent register = new Intent(getApplicationContext(), RegistrationActivity.class);
                register.putExtra("fromDoctor", fromDoctor);
                startActivity(register);
            }
        });
        btn_login.setOnClickListener(v -> {

            final String email = edt_email.getText().toString().trim();
            final String password = edt_password.getText().toString().trim();

            if (fromAdmin &&
                    email.equalsIgnoreCase("admin") &&
                    password.equalsIgnoreCase("admin")) {
                imgAdmin.performClick();
                return;
            }

            if (email.isEmpty()) {
                edt_email.setError("Email Required");
                return;
            }
            if (!isValidEmail(email)) {
                edt_email.setError("Invalid Email");
                return;
            }
            if (password.isEmpty()) {
                edt_password.setError("Password Required");
                return;
            }
//            if (password.length() < 8) {
//                edt_password.setError("Password Length must be grater than 7");
//                return;
//            }

            edt_email.setError(null);
            edt_password.setError(null);
            showLDialog();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            String path;
                            if (fromDoctor) {
                                path = "Doctor";
                            } else {
                                path = "Patient";
                            }

                            dismissDialog();
                            DatabaseReference mRef = mDatabase.getReference(path);
                            if (user != null && user.isEmailVerified()) {
                                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        for (DataSnapshot child : dataSnapshot.getChildren()) {

                                            if (fromDoctor) {
                                                DoctorModel doctor = child.getValue(DoctorModel.class);
                                                if (doctor != null) {
                                                    if (doctor.getEmail().equals(edt_email.getText().toString()) &&
                                                            doctor.getPassword().equals(edt_password.getText().toString())) {
                                                        if (doctor.isApproved) {
                                                            pref.setIsDocLogin(true);
                                                            pref.setLogIn(true);
                                                            Intent mainPage = new Intent(getApplicationContext(), DoctorDashboardActivity.class);
                                                            startActivity(mainPage);
                                                            finishAffinity();
                                                        } else {
                                                            Toast.makeText(LoginActivity.this, "Your Account is not approved by admin yet!", Toast.LENGTH_SHORT).show();
                                                        }
                                                        break;
                                                    } else {
                                                        Toast.makeText(LoginActivity.this, "Your Account is not approved by admin yet!", Toast.LENGTH_SHORT).show();
                                                        break;
                                                    }
                                                } else {
                                                    Toast.makeText(LoginActivity.this, "Your Account is not found!", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                PatientModel patient = child.getValue(PatientModel.class);
                                                if (patient != null && patient.getEmail().equals(edt_email.getText().toString()) &&
                                                        patient.getPassword().equals(edt_password.getText().toString())) {
                                                    pref.setIsDocLogin(false);
                                                    pref.setLogIn(true);
                                                    Intent mainPage = new Intent(getApplicationContext(), MainActivity.class);
                                                    startActivity(mainPage);
                                                    finishAffinity();
                                                    break;
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError
                                                                    databaseError) {

                                    }
                                });
                            } else {
                                Toast.makeText(LoginActivity.this, "Verify your Email to Login your Account!", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            dismissDialog();
                            Toast.makeText(LoginActivity.this, "Invalid Username or Password!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(e -> {
                        Toast.makeText(LoginActivity.this, "" + e, Toast.LENGTH_SHORT).show();
                        dismissDialog();
                    });
        });
    }
}