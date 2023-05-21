package com.smart.medilation.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smart.medilation.R;
import com.smart.medilation.adapters.GridSpacingItemDecoration;
import com.smart.medilation.model.SlotModel;
import com.smart.medilation.notification.FcmNotificationSender;
import com.smart.medilation.ui.dialog.LoadingDialog;
import com.smart.medilation.ui.doctor.DoctorDashboardActivity;
import com.smart.medilation.ui.login.SelectionActivity;
import com.smart.medilation.utils.PrefManager;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class BaseFragment extends Fragment {
    public LoadingDialog loadingDialog;
    public PrefManager pref;

    private FirebaseAuth mAuth;
    public FcmNotificationSender fcmNotification;

    protected void sendNotification(String topic, String title, String message) {
        if (fcmNotification == null)
            fcmNotification = new FcmNotificationSender();
        fcmNotification.sendNotificationToTopic(requireContext(), topic, title, message);
    }
    public BaseActivity.BottomMenuInterface bottomMenuInterface = null;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            bottomMenuInterface = (BaseActivity.BottomMenuInterface) context;
        } catch (Exception ignored) {
        }
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

        return false;
    }

    protected void setGridManager(RecyclerView recycler) {
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 3);
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pref = new PrefManager(requireContext());
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    protected void showToast(String strMsg) {
        Toast.makeText(requireContext(), strMsg, Toast.LENGTH_SHORT).show();
    }

    protected void showLDialog() {
        if (loadingDialog == null)
            loadingDialog = new LoadingDialog(requireContext());
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
            loadingDialog = new LoadingDialog(requireContext());
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();
    }

    protected void showLogoutDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireContext());
        alertDialogBuilder.setTitle("Logout!");
        alertDialogBuilder
                .setMessage("Are you sure, you want to logout?")
                .setCancelable(true)
                .setPositiveButton("YES", (dialog, id_) -> {
                    mAuth.signOut();

                    pref.setUserName("");
                    pref.setUserId("");
                    pref.setUserImage("");
                    pref.setIsDocLogin(false);
                    pref.setLogIn(false);
                    pref.setIsAdminLogin(false);
                    Intent login = new Intent(requireContext(), SelectionActivity.class);
                    startActivity(login);
                    getActivity().finishAffinity();
                })
                .setNegativeButton("NO", (dialog, id_) -> dialog.cancel());
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public ArrayList<SlotModel> jsonToArrayList(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<SlotModel>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public String arrayListToJson(ArrayList<SlotModel> arrayList) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<SlotModel>>() {
        }.getType();
        return gson.toJson(arrayList, type);
    }
}
