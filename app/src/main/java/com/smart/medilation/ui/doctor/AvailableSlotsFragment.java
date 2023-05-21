package com.smart.medilation.ui.doctor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smart.medilation.R;
import com.smart.medilation.adapters.AvailableSlotAdapter;
import com.smart.medilation.adapters.SlotAdapter;
import com.smart.medilation.model.DoctorModel;
import com.smart.medilation.model.SlotModel;
import com.smart.medilation.ui.BaseFragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AvailableSlotsFragment extends BaseFragment {

    private FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    DoctorModel doctorData;

    SlotAdapter daysAdapter;
    AvailableSlotAdapter timeSlotAdapter;

    List<SlotModel> slotList = new ArrayList<>();
    List<String> daysList = new ArrayList<>();

    Button btnUpdate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_available_slots, container, false);
    }

    public void setDefaultList() {
        slotList.clear();
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
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerTime = view.findViewById(R.id.recyclerTime);
        recyclerTime.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        RecyclerView recyclerDay = view.findViewById(R.id.recyclerDay);
        recyclerDay.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        daysList = getDays();
        daysAdapter = new SlotAdapter(requireContext(), slotList, (SlotModel slotModel, int pos) -> {

            daysAdapter.timeSlot = slotList.get(pos).getDay();
            daysAdapter.notifyDataSetChanged();
            timeSlotAdapter = new AvailableSlotAdapter(requireContext(),
                    slotList.get(pos).getSlots(), (position) ->
                    slotList.get(pos).setSlots(slotModel.arrayListToJson(timeSlotAdapter.getSelectedItem())));
            recyclerTime.setAdapter(timeSlotAdapter);
        });
        recyclerDay.setAdapter(daysAdapter);

        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(v -> {
            if(mAuth != null && mAuth.getCurrentUser() != null) {
                doctorData.timeSlots = arrayListToJson(daysAdapter.getSelectedSlots());
                String userId = mAuth.getCurrentUser().getUid();
                mRef.child(userId)
                        .setValue(doctorData)
                        .addOnCompleteListener(task -> {
                            showToast("Updated Record Successfully");
                            dismissDialog();
                        })
                        .addOnFailureListener(task -> {
                            showToast("Msg " + task.getMessage());
                            dismissDialog();
                        });
            }
        });

        showLDialog();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference("Doctor");

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    DoctorModel doctor = child.getValue(DoctorModel.class);
                    if (doctor != null && doctor.id.equalsIgnoreCase(pref.getUserId())) {
                        doctorData = doctor;
                        if (doctorData.timeSlots != null) {
                            slotList.clear();
                            slotList.addAll(jsonToArrayList(doctorData.timeSlots));
                            if(!slotList.isEmpty() && daysAdapter.listener != null) {
                                daysAdapter.listener.onDayClick(slotList.get(0),0);
                            }
                            daysAdapter.notifyDataSetChanged();
                        } else {
                            setDefaultList();
                            daysAdapter.notifyDataSetChanged();
                        }
                        dismissDialog();
                        break;
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
    public ArrayList<SlotModel> jsonToArrayList(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<SlotModel>>() {
        }.getType();
        return gson.fromJson(json, type);
    }
    private List<String> getDays() {
        String[] timeSlot = getResources().getStringArray(R.array.days);
        return Arrays.asList(timeSlot);
    }
}