package com.smart.medilation.ui.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.smart.medilation.R;
import com.smart.medilation.model.DoctorModel;
import com.smart.medilation.model.PatientModel;
import com.smart.medilation.model.SlotModel;
import com.smart.medilation.ui.BaseActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Pattern;

public class RegistrationActivity extends BaseActivity {

    String passwordRegex = "^(?=.*[!@#$%^&*()-=_+{};':\"|,.<>/?])(?=.*[A-Z])(?=.*\\d).{8,}$";

    TextView txtTitle;

    ImageView imageBack;
    Button btn_signup;
    CircularImageView imageView;
    Spinner spnSpecialization, spnExp, spnQualification;
    LinearLayout layoutSpecialization, layoutExp, layoutQualification;
    private EditText edtName, edtEmail, edtPhoneNum, edtAbout, edtRate;

    private TextInputEditText edtPassword;
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

    public ArrayList<SlotModel> setDefaultList() {
        ArrayList<SlotModel> slotList = new ArrayList<>();
        String[] days = getResources().getStringArray(R.array.days);
        String[] timeSlot = getResources().getStringArray(R.array.timeSlot);
        ArrayList<SlotModel.TimeModel> slots = new ArrayList<>();
        for (String slot : timeSlot) {
            slots.add(new SlotModel.TimeModel(slot, false));
        }
        for (String time : days) {
            SlotModel model = new SlotModel(time, slots);
            slotList.add(model);
        }
        return slotList;
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
        spnExp = findViewById(R.id.spnExp);
        layoutSpecialization = findViewById(R.id.layoutSpecialization);
        layoutExp = findViewById(R.id.layoutExp);
        spnQualification = findViewById(R.id.spnQualification);
        layoutQualification = findViewById(R.id.layoutQualification);
        edtAbout = findViewById(R.id.edtAbout);
        edtRate = findViewById(R.id.edtRate);

        edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    edtName.setError("Name Required");
                } else {
                    edtName.setError(null);
                }
            }
        });
        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().isEmpty()) {
                    edtEmail.setError("Email Required");
                } else if (!isValidEmail(editable.toString())) {
                    edtEmail.setError("Invalid Email");
                } else {
                    edtEmail.setError(null);
                }
            }
        });
        edtPhoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().isEmpty()) {
                    edtPhoneNum.setError("Phone Number Required");
                } else {
                    edtPhoneNum.setError(null);
                }
            }
        });
        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    edtPassword.setError("Password Required");
                } else if (!editable.toString().matches(passwordRegex)) {
                    edtPassword.setError("Password must have Special Character, Capital Alphabet with minimum of length 8");

                } else {
                    edtPassword.setError(null);
                }
            }
        });

        if (fromDoctor) {
            edtRate.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.toString().isEmpty()) {
                        edtRate.setError("Rate Per Session Required");
                    } else {
                        edtRate.setError(null);
                    }
                }
            });
            edtAbout.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.toString().isEmpty()) {
                        edtAbout.setError("About Text Required");
                    } else {
                        edtAbout.setError(null);
                    }
                }
            });
        }

        if (!fromDoctor) {
            layoutSpecialization.setVisibility(View.GONE);
            layoutExp.setVisibility(View.GONE);
            layoutQualification.setVisibility(View.GONE);
            edtAbout.setVisibility(View.GONE);
            edtRate.setVisibility(View.GONE);
        }

        imageView.setOnClickListener(v -> chooseImage());
        btn_signup.setOnClickListener(v -> {
            final String name = edtName.getText().toString().trim();
            final String email = edtEmail.getText().toString().trim();
            final String password = edtPassword.getText().toString().trim();
            final String phoneNum = edtPhoneNum.getText().toString().trim();
            final String about = edtAbout.getText().toString().trim();
            final String rate = edtRate.getText().toString().trim();


            edtName.setError(null);
            edtEmail.setError(null);
            edtPassword.setError(null);
            edtPhoneNum.setError(null);
            edtAbout.setError(null);

            validateAllField();
            if (filePath == null) {
                Toast.makeText(RegistrationActivity.this, "Upload Profile Image!", Toast.LENGTH_SHORT).show();
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
            if (password.isEmpty()) {
                edtPassword.setError("Password Required");
                return;
            }
            if (!password.matches(passwordRegex)) {
                edtPassword.setError("Password must have Special Character, Capital Alphabet with minimum of length 8");
                return;
            }

            if (fromDoctor) {
                if (rate.isEmpty()) {
                    edtRate.setError("Rate Per Session Required");
                    return;
                }
                if (about.isEmpty()) {
                    edtAbout.setError("About Text Required");
                    return;
                }
            }

            showLDialog();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegistrationActivity.this, task -> {
                        if (task.isSuccessful()) {
                            final FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                firebaseUser.sendEmailVerification().addOnCompleteListener(task1 -> {

                                    if (task1.isSuccessful()) {
                                        if (fromDoctor) {
                                            String specialization = spnSpecialization.getSelectedItem().toString();
                                            String exp = spnExp.getSelectedItem().toString();
                                            String qualification = spnQualification.getSelectedItem().toString();
                                            DoctorModel doctor = new DoctorModel(firebaseUser.getUid(), name, email, password,
                                                    phoneNum, exp, rate, specialization, qualification, about, false, false, "", "");
                                            doctor.rating = doctor.arrayListToJson(new ArrayList<>());
                                            doctor.timeSlots = slotListToJson(setDefaultList());
                                            uploadImage(firebaseUser.getUid(), doctor, null);
                                        } else {
                                            PatientModel patient = new PatientModel(firebaseUser.getUid(), name, email, password, phoneNum);
                                            uploadImage(firebaseUser.getUid(), null, patient);
                                        }
                                    }
                                });
                            } else {
                                dismissDialog();
                                Toast.makeText(RegistrationActivity.this, "User not Found", Toast.LENGTH_SHORT).show();
                            }

                        } else {

                            dismissDialog();
                            Toast.makeText(RegistrationActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(e -> {
                        Toast.makeText(RegistrationActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        dismissDialog();
                    });
        });
    }

    public String slotListToJson(ArrayList<SlotModel> arrayList) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<SlotModel>>() {
        }.getType();
        return gson.toJson(arrayList, type);
    }

    private void validateAllField() {

        final String name = edtName.getText().toString().trim();
        final String email = edtEmail.getText().toString().trim();
        final String password = edtPassword.getText().toString().trim();
        final String phoneNum = edtPhoneNum.getText().toString().trim();
        final String about = edtAbout.getText().toString().trim();
        final String rate = edtRate.getText().toString().trim();

        if (name.isEmpty()) {
            edtName.setError("Name Required");

        }
        if (email.isEmpty()) {
            edtEmail.setError("Email Required");

        }
        if (!isValidEmail(email)) {
            edtEmail.setError("Invalid Email");

        }
        if (phoneNum.isEmpty()) {
            edtPhoneNum.setError("Phone Number Required");

        }
        if (password.isEmpty()) {
            edtPassword.setError("Password Required");

        }
        if (!password.matches(passwordRegex)) {
            edtPassword.setError("Password must have Special Character, Capital Alphabet with minimum of length 8");

        }

        if (fromDoctor) {
            if (rate.isEmpty()) {
                edtRate.setError("Rate Per Session Required");
            }
            if (about.isEmpty()) {
                edtAbout.setError("About Text Required");
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
                    doctor.image = (uri.toString());
                    task = mRef.child(id).setValue(doctor);
                } else {
                    patient.imagePath = (uri.toString());
                    task = mRef.child(id).setValue(patient);
                }
                task.addOnCompleteListener(task11 -> {
                    dismissDialog();
                    if (task11.isSuccessful()) {
                        Toast.makeText(RegistrationActivity.this, "Account Created Successfully, Verify your Email to Login your Account!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(RegistrationActivity.this, "" + task11.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
            }).addOnFailureListener(e -> {
                dismissDialog();
                Toast.makeText(RegistrationActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }));
        }
    }
}