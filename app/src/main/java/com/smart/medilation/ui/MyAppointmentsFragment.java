package com.smart.medilation.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smart.medilation.R;
import com.smart.medilation.adapters.AppointmentAdapter;
import com.smart.medilation.model.AppointmentModel;
import com.smart.medilation.model.DoctorModel;
import com.smart.medilation.ui.dialog.ReviewDialog;
import com.smart.medilation.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyAppointmentsFragment extends BaseFragment implements AppointmentAdapter.ClickListener {
    TextView txtNoApp;
    Boolean fromDoctor = false;
    Boolean fromHistory = false;
    RecyclerView recyclerAppointments;
    AppointmentAdapter appointmentAdapter;
    List<AppointmentModel> appointmentList = new ArrayList<>();

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference mRef;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fromHistory = getArguments().getBoolean("fromHistory", false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_appointments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fromDoctor = pref.getIsDocLogin();
        showLDialog();

        txtNoApp = view.findViewById(R.id.txtNoApp);

        recyclerAppointments = view.findViewById(R.id.recyclerAppointments);
        recyclerAppointments.setLayoutManager(new GridLayoutManager(requireContext(), 1));
        appointmentAdapter = new AppointmentAdapter(requireContext(), appointmentList, this);
        appointmentAdapter.isFromDoctor = fromDoctor;
        appointmentAdapter.isFromHistory = fromHistory;
        recyclerAppointments.setAdapter(appointmentAdapter);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference(Constants.Appointment);
        getAllData();
    }

    private void getAllData() {
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointmentList.clear();
                String userID = mAuth.getCurrentUser().getUid();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    AppointmentModel user = child.getValue(AppointmentModel.class);
                    if (user != null &&
                            (user.doctorId.equalsIgnoreCase(userID) ||
                                    user.patientId.equalsIgnoreCase(userID))) {
                        if (fromHistory && !user.status.equalsIgnoreCase(AppointmentAdapter.STATUS_Pending)) {
                            appointmentList.add(user);
                        }
                        if (!fromHistory && (user.status.equalsIgnoreCase(AppointmentAdapter.STATUS_Pending) ||
                                user.status.equalsIgnoreCase(AppointmentAdapter.STATUS_Schedule) ||
                                user.status.equalsIgnoreCase(AppointmentAdapter.STATUS_Started))) {
                            appointmentList.add(user);
                        }
                    }
                }
                appointmentAdapter.notifyDataSetChanged();
                dismissDialog();
                checkList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dismissDialog();
            }
        });
    }


    private void getDoctors(String docId, DoctorModel.RatingModel ratingModel) {
        showLDialog();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mRef = mDatabase.getReference("Doctor");
        mRef.keepSynced(false);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DoctorModel myDocModel = null;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    DoctorModel doc = child.getValue(DoctorModel.class);
                    if (doc != null && Objects.equals(doc.id, docId)) {
                        myDocModel = doc;
                    }
                }
                if (myDocModel != null) {
                    ArrayList<DoctorModel.RatingModel> list = myDocModel.jsonToArrayList(myDocModel.rating);
                    list.add(ratingModel);
                    myDocModel.rating = myDocModel.arrayListToJson(list);
                    mRef.child(myDocModel.id)
                            .setValue(myDocModel)
                            .addOnCompleteListener(task -> {
                                dismissDialog();
                            })
                            .addOnFailureListener(task -> dismissDialog());
                } else {
                    dismissDialog();
                }
                Log.e("data*", "onDataChange dismissDialog");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("data*", "onCancelled dismissDialog");
                dismissDialog();
            }
        });
    }

    @Override
    public void onAppointmentClick(AppointmentModel model, String status, int position) {
        showLDialog();
        String msg = "Your Appointment is schedule at time slot: "
                + model.time + ", on " + model.date + " with " + model.doctorName;
        String msgStart = "Your Appointment is started with " + model.doctorName;
        String msgPatient = "Your Appointment is schedule at time slot: "
                + model.time + ", on " + model.date + " with " + model.patientName;
        String doctorCancelMsg = "Your Appointment is cancelled by " + model.doctorName;
        String patientCancelMsg = "Your Appointment is cancelled by " + model.patientName;
        String completedMsg = "Your Appointment is Successfully Completed.";
        if (fromDoctor) {
            if (Objects.equals(status, AppointmentAdapter.STATUS_Complete)) {
                sendNotification(model.patientId, "Appointment Completed", completedMsg);
            } else if (Objects.equals(status, AppointmentAdapter.STATUS_Cancel)) {
                sendNotification(model.patientId, "Appointment Cancelled", doctorCancelMsg);
            } else if (Objects.equals(status, AppointmentAdapter.STATUS_Schedule)) {
                sendNotification(model.patientId, "Appointment Scheduled", msg);
            } else if (Objects.equals(status, AppointmentAdapter.STATUS_Started)) {
                sendNotification(model.patientId, "Appointment Started", msgStart);
            }
        } else {
            if (Objects.equals(status, AppointmentAdapter.STATUS_Complete)) {
                sendNotification(model.doctorId, "Appointment Completed", completedMsg);
            } else if (Objects.equals(status, AppointmentAdapter.STATUS_Cancel)) {
                sendNotification(model.doctorId, "Appointment Cancelled", patientCancelMsg);
            } else if (Objects.equals(status, AppointmentAdapter.STATUS_Schedule)) {
                sendNotification(model.doctorId, "Appointment Scheduled", msgPatient);
            } else if (Objects.equals(status, AppointmentAdapter.STATUS_Started)) {
                sendNotification(model.doctorId, "Appointment Scheduled", msgStart);
            }
        }
        model.status = status;
        mRef.child(model.id)
                .setValue(model)
                .addOnCompleteListener(task -> {
                    if (fromHistory) {
                        getAllData();
                    } else {
                        dismissDialog();
                        appointmentList.remove(position);
                        appointmentAdapter.notifyDataSetChanged();
                        checkList();

                        if (Objects.equals(status, AppointmentAdapter.STATUS_Complete)) {
                            ReviewDialog dialog = new ReviewDialog(requireContext(), (text, rating) -> {
                                getDoctors(model.doctorId, new DoctorModel.RatingModel(rating, text));
                            });
                            dialog.show();
                        }
                    }
                })
                .addOnFailureListener(task -> dismissDialog());

        showToast("Successfully updated user");

        checkList();
    }

    private void checkList() {
        if (appointmentList.isEmpty()) {
            txtNoApp.setVisibility(View.VISIBLE);
        } else {
            txtNoApp.setVisibility(View.GONE);
        }
    }
}