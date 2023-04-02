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
import com.smart.medilation.ui.doctor.DoctorDashboardActivity;
import com.smart.medilation.ui.admin.AdminActivity;
import com.smart.medilation.ui.patient.PatientDashboardActivity;

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
            finishAffinity();
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
                pref.setIsAdminLogin(true);
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

                            DatabaseReference mRef = mDatabase.getReference(path);
                            if (user != null && userVerification(user)) {
                                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        boolean isRecordFound = false;
                                        for (DataSnapshot child : dataSnapshot.getChildren()) {

                                            if (fromDoctor) {
                                                DoctorModel doctor = child.getValue(DoctorModel.class);
                                                if (doctor != null && doctor.isApproved &&
                                                        doctor.id.equalsIgnoreCase(user.getUid())) {
                                                    dismissDialog();
                                                    pref.setUserId(user.getUid());
                                                    pref.setUserName(doctor.name);
                                                    pref.setUserName(doctor.name);
                                                    pref.setUserImage(doctor.image);
                                                    pref.setIsDocLogin(true);
                                                    pref.setLogIn(true);
                                                    Intent mainPage = new Intent(getApplicationContext(), DoctorDashboardActivity.class);
                                                    startActivity(mainPage);
                                                    finishAffinity();
                                                    isRecordFound = true;
                                                    break;
                                                }
                                            } else {
                                                PatientModel patient = child.getValue(PatientModel.class);
                                                if (patient != null &&
                                                        patient.id.equalsIgnoreCase(user.getUid())) {
                                                    dismissDialog();
                                                    pref.setUserId(user.getUid());
                                                    pref.setUserName(patient.name);
                                                    pref.setUserImage(patient.getImagePath());
                                                    pref.setIsDocLogin(false);
                                                    pref.setLogIn(true);
                                                    Intent mainPage = new Intent(getApplicationContext(), PatientDashboardActivity.class);
                                                    startActivity(mainPage);
                                                    finishAffinity();
                                                    isRecordFound = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (!isRecordFound) {
                                            dismissDialog();
                                            showToast("Your Record is not found!");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError
                                                                    databaseError) {
                                        dismissDialog();

                                    }
                                });
                            } else {
                                showToast("Verify your Email to Login your Account!");
                                dismissDialog();
                            }

                        } else {
                            dismissDialog();
                            showToast("Invalid Username or Password!");
                        }
                    }).addOnFailureListener(e -> {
                        showToast("" + e);
                        dismissDialog();
                    });
        });
    }
}