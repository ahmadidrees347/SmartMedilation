package com.smart.medilation.ui.patient;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.smart.medilation.R;
import com.smart.medilation.adapters.AppointmentAdapter;
import com.smart.medilation.adapters.DateAdapter;
import com.smart.medilation.adapters.TimeSlotAdapter;
import com.smart.medilation.model.AppointmentModel;
import com.smart.medilation.model.DateModel;
import com.smart.medilation.model.DoctorModel;
import com.smart.medilation.model.SlotModel;
import com.smart.medilation.ui.BaseActivity;
import com.smart.medilation.ui.payment.PaymentActivity;
import com.smart.medilation.utils.Constants;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RequestAppointmentActivity extends BaseActivity {

    ImageView imageBack, imgChat, imgCall, imgLogout, imgLocation;

    CircularImageView image;
    TextView txtName, txtType;

    Button btnRequest;

    List<DateModel> dateList = new ArrayList<>();
    ArrayList<SlotModel.TimeModel> timeSlotList = new ArrayList<>();
    DateAdapter dateAdapter;
    TimeSlotAdapter timeSlotAdapter;
    String strType = "";
    LinearLayout layoutPhysical, layoutOnline, layoutDoor;
    TextView txtPhysical, txtOnline, txtDoor;


    private void setDefault() {
        Drawable backgroundDrawable = ContextCompat.getDrawable(this, R.drawable.round_stroke);
        int color = ContextCompat.getColor(this, R.color.black);

        txtPhysical.setTextColor(color);
        layoutPhysical.setBackground(backgroundDrawable);

        txtOnline.setTextColor(color);
        layoutOnline.setBackground(backgroundDrawable);

        txtDoor.setTextColor(color);
        layoutDoor.setBackground(backgroundDrawable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_appointment);
        layoutPhysical = findViewById(R.id.layoutPhysical);
        layoutOnline = findViewById(R.id.layoutOnline);
        layoutDoor = findViewById(R.id.layoutDoor);
        txtPhysical = findViewById(R.id.txtPhysical);
        txtOnline = findViewById(R.id.txtOnline);
        txtDoor = findViewById(R.id.txtDoor);
        imgLocation = findViewById(R.id.imgLocation);

        imgLocation.setOnClickListener(v -> {
            openLocationInGoogleMaps();
        });
        Drawable backgroundDrawable = ContextCompat.getDrawable(this, R.drawable.round_);
        int color = ContextCompat.getColor(this, R.color.white);

        layoutPhysical.setOnClickListener(v -> {
            strType = "Physical";
            setDefault();
            txtPhysical.setTextColor(color);
            layoutPhysical.setBackground(backgroundDrawable);
        });
        layoutOnline.setOnClickListener(v -> {
            strType = "Online";
            setDefault();
            txtOnline.setTextColor(color);
            layoutOnline.setBackground(backgroundDrawable);
        });
        layoutDoor.setOnClickListener(v -> {
            strType = "Door";
            setDefault();
            txtDoor.setTextColor(color);
            layoutDoor.setBackground(backgroundDrawable);
        });

        DoctorModel myModel = (DoctorModel) getIntent().getSerializableExtra("myModel");
        ArrayList<SlotModel> timeSlots = jsonToSlotList(myModel.timeSlots);


//        timeSlotList = getTimeSlots();
        RecyclerView recyclerTime = findViewById(R.id.recyclerTime);
        recyclerTime.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        timeSlotAdapter = new TimeSlotAdapter(this, timeSlotList);
        recyclerTime.setAdapter(timeSlotAdapter);

        dateList = getNextDays();
        RecyclerView recyclerDate = findViewById(R.id.recyclerDate);
        recyclerDate.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        dateAdapter = new DateAdapter(this, dateList, position -> {
            String day = dateList.get(position).getDay();
            for (SlotModel model : timeSlots) {
                if (day.equalsIgnoreCase(model.getDay())) {
                    timeSlotList = model.getSlots();
                    timeSlotAdapter = new TimeSlotAdapter(this, timeSlotList);
                    recyclerTime.setAdapter(timeSlotAdapter);
                    break;
                }
            }
        });
        recyclerDate.setAdapter(dateAdapter);
        dateAdapter.listener.onDateClick(0);

        imgLogout = findViewById(R.id.imgLogout);
        imgLogout.setOnClickListener(v -> showLogoutDialog());
        image = findViewById(R.id.image);
        imageBack = findViewById(R.id.imageBack);
        imgChat = findViewById(R.id.imgChat);
        imgCall = findViewById(R.id.imgCall);
        btnRequest = findViewById(R.id.btnRequest);
        imageBack.setOnClickListener(v -> onBackPressed());


        String specialization = myModel.specialization + " Specialist";

        btnRequest.setOnClickListener(v -> {
            DateModel selectedModel = dateAdapter.getSelectedItem();
            if(selectedModel == null) {
                showToast("Select Time and Date");
                return;
            }

            String time = selectedModel.getDay() + " " + selectedModel.getDate();
            bookAppointment(myModel, timeSlotAdapter.getSelectedItem(), time, selectedModel.getTimeInMillis());
        });

        imgCall.setOnClickListener(v -> startDialer(myModel.phoneNum));
        imgChat.setOnClickListener(v -> startChat(myModel.phoneNum));

        Glide.with(this)
                .load(myModel.image)
                .placeholder(R.drawable.ic_user)
                .into(image);

        txtName = findViewById(R.id.txtName);
        txtType = findViewById(R.id.txtType);

        txtName.setText(myModel.name);
        txtType.setText(specialization);
    }

    public String slotListToJson(ArrayList<SlotModel> arrayList) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<SlotModel>>() {
        }.getType();
        return gson.toJson(arrayList, type);
    }

    public ArrayList<SlotModel> jsonToSlotList(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<SlotModel>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public ArrayList<SlotModel.TimeModel> jsonToArrayList(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<SlotModel.TimeModel>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public String arrayListToJson(ArrayList<SlotModel.TimeModel> arrayList) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<SlotModel.TimeModel>>() {
        }.getType();
        return gson.toJson(arrayList, type);
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

    public void bookAppointment(DoctorModel docModel, String strTime, String strDate, long timeInMillis) {

        if (strDate.isEmpty()) {
            showToast("Kindly Select Date");
            return;
        }
        if (strTime.isEmpty()) {
            showToast("Kindly Select Time");
            return;
        }
        if (strType.isEmpty()) {
            showToast("Kindly Select Appointment Type");
            return;
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && userVerification(user)) {
            showLDialog();
            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
            DatabaseReference mRef = mDatabase.getReference(Constants.Appointment);
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean isAlreadyHasAppointment = false;
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        AppointmentModel model = child.getValue(AppointmentModel.class);
                        if (model != null
                                && model.time.equalsIgnoreCase(strTime)
                                && model.date.equalsIgnoreCase(strDate)) {
                            isAlreadyHasAppointment = true;
                            break;
                        }
                    }
                    if (!isAlreadyHasAppointment) {
                        String rate = "";
                        if (strType.equalsIgnoreCase("Physical"))
                            rate = docModel.ratePhysical;
                        else
                            rate = docModel.rate;
                        AppointmentModel model = new AppointmentModel("", docModel.id, docModel.name, user.getUid(),
                                pref.getUserName(), strTime, strDate, AppointmentAdapter.STATUS_Pending, strType,
                                rate, "", false);

                        Intent intent = new Intent(RequestAppointmentActivity.this, PaymentActivity.class);
                        intent.putExtra("myModel", model);
                        intent.putExtra("timeInMillis", timeInMillis);
                        startActivity(intent);

                        dismissDialog();
                    } else {
                        dismissDialog();
                        showToast("Doctor has already an appointment at specific Date & time.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    dismissDialog();
                }
            });
        } else {
            showToast("User is Not Verified");
        }
    }
}