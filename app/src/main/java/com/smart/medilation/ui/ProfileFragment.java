package com.smart.medilation.ui;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

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

public class ProfileFragment extends BaseFragment {

    Button btn_signup, btnLogout;
    CircularImageView imageView;
    Spinner spnSpecialization, spnExp, spnQualification;
    LinearLayout layoutSpecialization, layoutExp, layoutQualification;
    private EditText edtName, edtEmail, edtPhoneNum, edtAbout, edtRateOnline, edtRatePhysical;
    //defining Firebase Auth object
    private FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private boolean fromDoctor = false;
    private String specialization = "";
    private String exp = "";
    private String qualification = "";

    private Uri filePath = null;
    DoctorModel doctor;
    PatientModel patient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            fromDoctor = getArguments().getBoolean("fromDoctor", false);
    }

    private boolean isValidEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fromDoctor = pref.getIsDocLogin();
        showLDialog();

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

        imageView = view.findViewById(R.id.image);


        btnLogout = view.findViewById(R.id.btnLogout);
        btn_signup = view.findViewById(R.id.btn_signup);

        edtName = view.findViewById(R.id.edtName);
        edtEmail = view.findViewById(R.id.edt_email);
        edtPhoneNum = view.findViewById(R.id.edtPhoneNum);
        spnSpecialization = view.findViewById(R.id.spnSpecialization);
        layoutSpecialization = view.findViewById(R.id.layoutSpecialization);
        layoutExp = view.findViewById(R.id.layoutExp);
        spnExp = view.findViewById(R.id.spnExp);
        layoutQualification = view.findViewById(R.id.layoutQualification);
        spnQualification = view.findViewById(R.id.spnQualification);
        edtAbout = view.findViewById(R.id.edtAbout);
        edtRateOnline = view.findViewById(R.id.edtRateOnline);
        edtRatePhysical = view.findViewById(R.id.edtRatePhysical);

        if (!fromDoctor) {
            layoutSpecialization.setVisibility(View.GONE);
            layoutExp.setVisibility(View.GONE);
            layoutQualification.setVisibility(View.GONE);
            edtAbout.setVisibility(View.GONE);
            edtRateOnline.setVisibility(View.GONE);
            edtRatePhysical.setVisibility(View.GONE);
        }

        imageView.setOnClickListener(v -> chooseImage());
        btnLogout.setOnClickListener(v -> {
            showLogoutDialog();
        });
        btn_signup.setOnClickListener(v -> {
            final String name = edtName.getText().toString().trim();
            final String email = edtEmail.getText().toString().trim();
            final String phoneNum = edtPhoneNum.getText().toString().trim();
            final String about = edtAbout.getText().toString().trim();
            final String rateOnline = edtRateOnline.getText().toString().trim();
            final String ratePhysical = edtRatePhysical.getText().toString().trim();
            String specialization = spnSpecialization.getSelectedItem().toString();
            String exp = spnExp.getSelectedItem().toString();
            String qualification = spnQualification.getSelectedItem().toString();

            if (filePath == null) {
                showToast("Upload Profile Image!");
                return;
            }
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
            if (phoneNum.length() < 10) {
                edtPhoneNum.setError("Phone Number must have 10 characters");
                return;
            }

            if (fromDoctor) {
                if (rateOnline.isEmpty()) {
                    edtRateOnline.setError("Online Rate Per Session Required");
                    return;
                }
                if (ratePhysical.isEmpty()) {
                    edtRatePhysical.setError("Physical Rate Per Session Required");
                    return;
                }
                if (about.isEmpty()) {
                    edtAbout.setError("About Text Required");
                    return;
                }
            }
            edtName.setError(null);
            edtEmail.setError(null);
            edtPhoneNum.setError(null);
            edtAbout.setError(null);
            edtRateOnline.setError(null);
            edtRatePhysical.setError(null);

            showLDialog();

            if (mAuth.getCurrentUser() != null) {
                String userId = mAuth.getCurrentUser().getUid();

                if (fromDoctor && doctor != null) {
                    DoctorModel doctorModel = new DoctorModel(userId, name, email, doctor.password,
                            phoneNum, exp, rateOnline,ratePhysical, specialization, qualification, about, doctor.isApproved,
                            doctor.isRejected, doctor.rating, doctor.timeSlots);

                    doctorModel.image = (filePath.toString());
                    mRef.child(userId)
                            .setValue(doctorModel)
                            .addOnCompleteListener(task -> {
                                pref.setUserName(doctorModel.name);
                                pref.setUserImage(doctorModel.image);
                                showToast("Updated Record Successfully");
                                dismissDialog();
                            })
                            .addOnFailureListener(task -> {
                                showToast("Msg " + task.getMessage());
                                dismissDialog();
                            });
                } else {
                    PatientModel patientModel = new PatientModel(userId, name, email, patient.getPassword(), phoneNum);
                    patientModel.setImagePath(filePath.toString());
                    mRef.child(userId)
                            .setValue(patientModel)
                            .addOnCompleteListener(task -> {
                                pref.setUserName(patientModel.name);
                                pref.setUserImage(patientModel.imagePath);
                                showToast("Updated Record Successfully");
                                dismissDialog();
                            })
                            .addOnFailureListener(task -> {
                                showToast("Msg " + task.getMessage());
                                dismissDialog();
                            });
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
                        if (doctor != null && doctor.id.equalsIgnoreCase(pref.getUserId())) {
                            pref.setUserName(doctor.name);
                            pref.setUserImage(doctor.image);
                            edtName.setText(doctor.name);
                            edtEmail.setText(doctor.email);
                            edtPhoneNum.setText(doctor.phoneNum);
                            edtRateOnline.setText(doctor.rate);
                            edtRatePhysical.setText(doctor.ratePhysical);
                            edtAbout.setText(doctor.about);
                            filePath = Uri.parse(doctor.image);
                            specialization = doctor.specialization;
                            exp = doctor.experience;
                            qualification = doctor.qualification;
                            Glide.with(ProfileFragment.this)
                                    .load(filePath)
                                    .placeholder(R.drawable.ic_user)
                                    .into(imageView);
                            selectValue(spnSpecialization, specialization);
                            selectValue(spnExp, exp);
                            selectValue(spnQualification, qualification);
                            dismissDialog();
                            break;
                        }
                    } else {
                        patient = child.getValue(PatientModel.class);
                        if (patient != null && patient.id.equalsIgnoreCase(pref.getUserId())) {
                            pref.setUserName(patient.name);
                            pref.setUserImage(patient.imagePath);
                            edtName.setText(patient.name);
                            edtEmail.setText(patient.email);
                            edtPhoneNum.setText(patient.phoneNum);
                            filePath = Uri.parse(patient.imagePath);
                            Glide.with(ProfileFragment.this)
                                    .load(filePath)
                                    .placeholder(R.drawable.ic_user)
                                    .into(imageView);
                            dismissDialog();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError
                                            databaseError) {
                dismissDialog();

            }
        });
    }

    private void selectValue(Spinner spinner, Object value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            imageView.setImageURI(filePath);

            uploadImage();
        }
    }

    private void uploadImage() {
        if (filePath != null) {
            showLDialog();
            StorageReference ref =
                    FirebaseStorage.getInstance().getReference().child("Smart/" + UUID.randomUUID().toString());
            ref.putFile(filePath).addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                Task<Void> task;
                if (fromDoctor) {
                    doctor.image = (uri.toString());
                    task = mRef.child(pref.getUserId()).setValue(doctor);
                    pref.setUserImage(doctor.image);
                } else {
                    patient.imagePath = (uri.toString());
                    task = mRef.child(pref.getUserId()).setValue(patient);
                    pref.setUserImage(patient.imagePath);
                }
                task.addOnCompleteListener(task11 -> {
                    dismissDialog();
                    if (task11.isSuccessful()) {
                        showToast("Update Image!");
                    } else {
                        showToast("" + task11.getException());
                    }
                });
            }).addOnFailureListener(e -> showToast("" + e.getMessage())));
        }
    }
}