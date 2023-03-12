package com.smart.medilation.ui.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.smart.medilation.R;
import com.smart.medilation.model.DoctorModel;
import com.smart.medilation.model.PatientModel;
import com.smart.medilation.ui.BaseActivity;

import java.util.UUID;
import java.util.regex.Pattern;

public class RegistrationActivity extends BaseActivity {
    TextView txtTitle;

    ImageView imageBack;
    Button btn_signup;
    CircularImageView imageView;
    Spinner spnSpecialization;
    LinearLayout layoutSpecialization;
    private EditText edtName, edtEmail, edtPassword, edtPhoneNum, edtExp, edtQualification;
    //defining Firebase Auth object
    private FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private boolean fromDoctor = false;

    private Uri filePath = null;

    private boolean isValidEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        fromDoctor = getIntent().getBooleanExtra("fromDoctor", false);
        txtTitle = findViewById(R.id.txtTitle);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance();
        String path;
        if (fromDoctor) {
            path = "Doctor";
            txtTitle.setText(getString(R.string.reg_as_doctor));
        } else {
            path = "Patient";
            txtTitle.setText(getString(R.string.reg_as_patient));
        }
        mRef = mDatabase.getReference(path);

        imageView = findViewById(R.id.image);

        imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(v -> onBackPressed());
        btn_signup = findViewById(R.id.btn_signup);

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edtPassword);
        edtPhoneNum = findViewById(R.id.edtPhoneNum);
        spnSpecialization = findViewById(R.id.spnSpecialization);
        layoutSpecialization = findViewById(R.id.layoutSpecialization);
        edtExp = findViewById(R.id.edtExp);
        edtQualification = findViewById(R.id.edtQualification);

        if (!fromDoctor) {
            layoutSpecialization.setVisibility(View.GONE);
            edtExp.setVisibility(View.GONE);
            edtQualification.setVisibility(View.GONE);
        }

        imageView.setOnClickListener(v -> chooseImage());
        btn_signup.setOnClickListener(v -> {
            final String name = edtName.getText().toString().trim();
            final String email = edtEmail.getText().toString().trim();
            final String password = edtPassword.getText().toString().trim();
            final String phoneNum = edtPhoneNum.getText().toString().trim();
            final String exp = edtExp.getText().toString().trim();
            final String qualification = edtQualification.getText().toString().trim();

            if (name.isEmpty()) {
                edtName.setError("Name Required");
                return;
            }
            if (email.isEmpty()) {
                edtEmail.setError("Email Required");
                return;
            }
            if (!isValidEmail(email)) {
                edtEmail.setError("Invalid Email");
                return;
            }
            if (password.isEmpty()) {
                edtPassword.setError("Password Required");
                return;
            }
            if (phoneNum.isEmpty()) {
                edtPhoneNum.setError("Phone Number Required");
                return;
            }
            if (filePath == null) {
                Toast.makeText(RegistrationActivity.this, "Upload Profile Image!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (fromDoctor) {
                if (exp.isEmpty()) {
                    edtExp.setError("Experience Required");
                    return;
                }
                if (qualification.isEmpty()) {
                    edtQualification.setError("Qualification Required");
                    return;
                }
            }
            edtName.setError(null);
            edtEmail.setError(null);
            edtPassword.setError(null);
            edtPhoneNum.setError(null);
            edtExp.setError(null);
            edtQualification.setError(null);

            showLDialog();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegistrationActivity.this, task -> {
                        if (task.isSuccessful()) {
                            final FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                firebaseUser.sendEmailVerification().addOnCompleteListener(task1 -> {
                                    dismissDialog();
                                    if (task1.isSuccessful()) {
                                        if (fromDoctor) {
                                            String specialization = spnSpecialization.getSelectedItem().toString();
                                            DoctorModel doctor = new DoctorModel(firebaseUser.getUid(), name, email, password, phoneNum, exp, specialization, qualification);
                                            uploadImage(firebaseUser.getUid(), doctor, null);
                                        } else {
                                            PatientModel patient = new PatientModel(firebaseUser.getUid(), name, email, password, phoneNum);
                                            uploadImage(firebaseUser.getUid(), null, patient);
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(RegistrationActivity.this, "User not Found", Toast.LENGTH_SHORT).show();
                            }

                        } else {

                            dismissDialog();
                            Toast.makeText(RegistrationActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegistrationActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            dismissDialog();
                        }
                    });
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            imageView.setImageURI(filePath);
        }
    }

    private void uploadImage(String id, DoctorModel doctor, PatientModel patient) {
        if (filePath != null) {
            StorageReference ref =
                    FirebaseStorage.getInstance().getReference().child("Smart/" + UUID.randomUUID().toString());
            ref.putFile(filePath).addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                Task<Void> task;
                if (fromDoctor) {
                    doctor.setImage(uri.toString());
                    task = mRef.child(id).setValue(doctor);
                } else {
                    patient.setImagePath(uri.toString());
                    task = mRef.child(id).setValue(patient);
                }
                task.addOnCompleteListener(task11 -> {
                    if (task11.isSuccessful()) {
                        Toast.makeText(RegistrationActivity.this, "Account Created Successfully, Verify your Email to Login your Account!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(RegistrationActivity.this, "" + task11.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
            }).addOnFailureListener(e -> Toast.makeText(RegistrationActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show()));
        }
    }
}