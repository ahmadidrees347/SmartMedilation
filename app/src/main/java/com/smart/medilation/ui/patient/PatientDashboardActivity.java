package com.smart.medilation.ui.patient;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.smart.medilation.R;
import com.smart.medilation.ui.BaseActivity;

public class PatientDashboardActivity extends BaseActivity implements BaseActivity.BottomMenuInterface {

    ImageView imageBack, imgLogout;
    BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_dashboard);

        imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(v -> onBackPressed());
        imgLogout = findViewById(R.id.imgLogout);
        imgLogout.setOnClickListener(v -> showLogoutDialog());

        navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        navView.setOnItemSelectedListener(item -> {
            if (navController.getCurrentDestination() != null &&
                    navController.getCurrentDestination().getId() == item.getItemId()) {
                return true;
            } else if (R.id.navHome == item.getItemId()) {
                navController.navigate(R.id.navHome);
            } else if (R.id.navDomain == item.getItemId()) {
                navController.navigate(R.id.navDomain);
            } else if (R.id.navMyAppointments == item.getItemId()) {
                navController.navigate(R.id.navMyAppointments);
            } else if (R.id.navProfile == item.getItemId()) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("fromDoctor", false);
                navController.navigate(R.id.navProfile, bundle);
            }
            return true;
        });
    }

    @Override
    public void onNavChange() {
        navView.getMenu().getItem(1).setChecked(true);
        navView.getMenu().getItem(1).setCheckable(true);
    }
}