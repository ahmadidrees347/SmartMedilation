package com.smart.medilation.ui;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.smart.medilation.R;
import com.smart.medilation.model.DoctorModel;
import com.smart.medilation.model.PatientModel;

import java.util.UUID;
import java.util.regex.Pattern;

public class ProfileActivity extends BaseActivity {
    ImageView imageBack;
    Button btn_signup;
    CircularImageView imageView;
    Spinner spnSpecialization;
    LinearLayout layoutSpecialization;
    private EditText edtName, edtEmail, edtPhoneNum, edtExp, edtQualification;
    //defining Firebase Auth object
    private FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private boolean fromDoctor = false;

    private Uri filePath = null;
    DoctorModel doctor;
    PatientModel patient;

    private boolean isValidEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        fromDoctor = getIntent().getBooleanExtra("fromDoctor", false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance();
        String path;
        if (fromDoctor) {
            path = "Doctor";
        } else {
            path = "Patient";
        }
        mRef = mDatabase.getReference(path);

        imageView = findViewById(R.id.image);

        imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(v -> onBackPressed());
        btn_signup = findViewById(R.id.btn_signup);

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edt_email);
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

//        imageView.setOnClickListener(v -> chooseImage());
        btn_signup.setOnClickListener(v -> {
            final String name = edtName.getText().toString().trim();
            final String email = edtEmail.getText().toString().trim();
            final String phoneNum = edtPhoneNum.getText().toString().trim();
            final String exp = edtExp.getText().toString().trim();
            final String qualification = edtQualification.getText().toString().trim();
            String specialization = spnSpecialization.getSelectedItem().toString();

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
            if (phoneNum.isEmpty()) {
                edtPhoneNum.setError("Phone Number Required");
                return;
            }
            if (filePath == null) {
                Toast.makeText(ProfileActivity.this, "Upload Profile Image!", Toast.LENGTH_SHORT).show();
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
            edtPhoneNum.setError(null);
            edtExp.setError(null);
            edtQualification.setError(null);

            showLDialog();

            if (mAuth.getCurrentUser() != null) {
                String userId = mAuth.getCurrentUser().getUid();

                if (fromDoctor) {
                    DoctorModel doctorModel = new DoctorModel(userId, name, email, doctor.getPassword(), phoneNum, exp, specialization, qualification);

                    doctorModel.setImage(filePath.toString());
                    mRef.child(userId)
                            .setValue(doctorModel)
                            .addOnCompleteListener(task -> {
                                showToast("Updated Record Successfully");
                                dismissDialog();
                            })
                            .addOnFailureListener(task -> {
                                showToast("Msg " + task.getMessage());
                                dismissDialog();
                            });
//                    uploadImage(userId, doctor, null);
                } else {
                    PatientModel patientModel = new PatientModel(userId, name, email, patient.getPassword(), phoneNum);
                    patientModel.setImagePath(filePath.toString());
                    mRef.child(userId)
                            .setValue(patientModel)
                            .addOnCompleteListener(task -> {
                                showToast("Updated Record Successfully");
                                dismissDialog();
                            })
                            .addOnFailureListener(task -> {
                                showToast("Msg " + task.getMessage());
                                dismissDialog();
                            });
//                    uploadImage(userId, null, patient);
                }
            }
        });


        edtEmail.setEnabled(false);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    if (fromDoctor) {
                        doctor = child.getValue(DoctorModel.class);
                        if (doctor != null) {
                            edtName.setText(doctor.getName());
                            edtEmail.setText(doctor.getEmail());
                            edtPhoneNum.setText(doctor.getPhoneNum());
                            edtExp.setText(doctor.getExperience());
                            edtQualification.setText(doctor.getQualification());
                            filePath = Uri.parse(doctor.getImage());
                            Glide.with(ProfileActivity.this)
                                    .load(filePath)
                                    .placeholder(R.drawable.ic_user)
                                    .into(imageView);
                            break;
                        }
                    } else {
                        patient = child.getValue(PatientModel.class);
                        if (patient != null) {
                            edtName.setText(patient.getName());
                            edtEmail.setText(patient.getEmail());
                            edtPhoneNum.setText(patient.getPhoneNum());
                            filePath = Uri.parse(patient.getImagePath());
                            Glide.with(ProfileActivity.this)
                                    .load(filePath)
                                    .placeholder(R.drawable.ic_user)
                                    .into(imageView);
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
                        Toast.makeText(ProfileActivity.this, "Account Created Successfully, Verify your Email to Login your Account!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(ProfileActivity.this, "" + task11.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
            }).addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show()));
        }
    }
}