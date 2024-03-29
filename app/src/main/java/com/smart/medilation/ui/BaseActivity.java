package com.smart.medilation.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.smart.medilation.BuildConfig;
import com.smart.medilation.R;
import com.smart.medilation.adapters.GridSpacingItemDecoration;
import com.smart.medilation.model.DateModel;
import com.smart.medilation.notification.FcmNotificationSender;
import com.smart.medilation.ui.dialog.LoadingDialog;
import com.smart.medilation.ui.login.SelectionActivity;
import com.smart.medilation.utils.PrefManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BaseActivity extends AppCompatActivity {
    public LoadingDialog loadingDialog;
    public PrefManager pref;
    public FcmNotificationSender fcmNotification;
    public static float LAT = 33.546881292649026f;
    public static float LONG = 73.0027194965758f;

    public interface BottomMenuInterface {
        void onNavChange(int value);
    }


    protected void openLocationInGoogleMaps() {
        startActivity(new Intent(this, MapActivity.class));
        Uri gmmIntentUri = Uri.parse("geo:" + LAT + "," + LONG);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

//        if (mapIntent.resolveActivity(getPackageManager()) != null) {
//            startActivity(mapIntent);
//        }
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

        return false;
    }

    protected boolean userVerification(FirebaseUser user) {
        if (BuildConfig.DEBUG)
            return true;
        return user.isEmailVerified();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = new PrefManager(this);
    }

    protected void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                boolean notificationPermission = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermission = ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.POST_NOTIFICATIONS);
                }
                if (notificationPermission ||
                        ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.ACCESS_FINE_LOCATION) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    requestPermissions();
                } else {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.fromParts("package", getPackageName(), null));
                    startActivity(intent);
                }
            }
        }
    }

    protected List<DateModel> getNextDays() {
        List<DateModel> dateList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance(); // Get a calendar instance
        for (int i = 0; i < 10; i++) {
            Date date = calendar.getTime(); // Get the current date
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
            String dateString = dateFormat.format(date);

            String dayOfWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date);
//            if (!dayOfWeek.equalsIgnoreCase("Saturday") &&
//                    !dayOfWeek.equalsIgnoreCase("Sunday"))
            dateList.add(new DateModel(dayOfWeek, dateString, date.getTime()));
            Log.d("Next 7 Dates and Days", dateString + " - " + date.getTime() + " (" + dayOfWeek + ")");
            calendar.add(Calendar.DAY_OF_YEAR, 1); // Add a day to the calendar
        }
        return dateList;
    }

    protected List<String> getTimeSlots() {
        String[] timeSlot = getResources().getStringArray(R.array.timeSlot);
        return Arrays.asList(timeSlot);
    }

    protected void setGridManager(RecyclerView recycler) {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        });
        int spacing = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
        recycler.addItemDecoration(new GridSpacingItemDecoration(3, spacing, false));
        recycler.setLayoutManager(layoutManager);
    }

    protected void showToast(String strMsg) {
        Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();
    }


    protected void sendNotification(String topic, String title, String message) {
        if (fcmNotification == null)
            fcmNotification = new FcmNotificationSender();
        fcmNotification.sendNotificationToTopic(this, topic, title, message);
    }


    protected void sendNotification(String topic, String title, String message, long timeInMillis) {
        if (fcmNotification == null)
            fcmNotification = new FcmNotificationSender();
        FcmNotificationSender.scheduleNotification(this, topic, title, message, timeInMillis);
    }


    protected void showLDialog() {
        if (loadingDialog == null)
            loadingDialog = new LoadingDialog(this);
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();
        try {
            if (isInternetAvailable())
                loadingDialog.show();
        } catch (Exception ignored) {
        }
    }

    protected void dismissDialog() {
        if (loadingDialog == null)
            loadingDialog = new LoadingDialog(this);
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();
    }

    protected void showLogoutDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BaseActivity.this);
        alertDialogBuilder.setTitle("Logout!");
        alertDialogBuilder
                .setMessage("Are you sure, you want to logout?")
                .setCancelable(true)
                .setPositiveButton("YES", (dialog, id_) -> {
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    mAuth.signOut();
                    pref.setUserName("");
                    pref.setUserId("");
                    pref.setUserImage("");
                    pref.setIsDocLogin(false);
                    pref.setLogIn(false);
                    pref.setIsAdminLogin(false);
                    Intent login = new Intent(getApplicationContext(), SelectionActivity.class);
                    startActivity(login);
                    finishAffinity();
                })
                .setNegativeButton("NO", (dialog, id_) -> dialog.cancel());
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
