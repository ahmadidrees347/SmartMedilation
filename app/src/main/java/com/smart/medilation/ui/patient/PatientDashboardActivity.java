package com.smart.medilation.ui.patient;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
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

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    // Get new FCM registration token
                    String token = task.getResult();
                    pref.setToken(token);
                    Log.d("TAG", "FCM registration token: " + token);
                    // Send this token to your server to send push notifications
                });
    }

    @Override
    public void onNavChange(int value) {
        navView.getMenu().getItem(value).setChecked(true);
        navView.getMenu().getItem(value).setCheckable(true);
    }
}