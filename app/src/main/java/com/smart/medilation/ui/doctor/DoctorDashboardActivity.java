package com.smart.medilation.ui.doctor;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.smart.medilation.R;
import com.smart.medilation.ui.BaseActivity;

public class DoctorDashboardActivity extends BaseActivity {

    ImageView imageBack, imgLogout;
    BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_dashboard);

        requestPermissions();
        imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(v -> onBackPressed());
        imgLogout = findViewById(R.id.imgLogout);
        imgLogout.setOnClickListener(v -> showLogoutDialog());

        navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_doc_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        navView.setOnItemSelectedListener(item -> {
            if (navController.getCurrentDestination() != null &&
                    navController.getCurrentDestination().getId() == item.getItemId()) {
                return true;
            } else if (R.id.navDocAppointments == item.getItemId()) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("fromDoctor", pref.getIsDocLogin());
                bundle.putBoolean("fromHistory", false);
                navController.navigate(R.id.navDocAppointments);
            } else if (R.id.navDocSlot == item.getItemId()) {
                navController.navigate(R.id.navDocSlot);
            } else if (R.id.navDocHistory == item.getItemId()) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("fromDoctor", pref.getIsDocLogin());
                bundle.putBoolean("fromHistory", true);
                navController.navigate(R.id.navDocHistory, bundle);
            } else if (R.id.navDocProfile == item.getItemId()) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("fromDoctor", pref.getIsDocLogin());
                navController.navigate(R.id.navDocProfile, bundle);
            }
            return true;
        });
    }
}