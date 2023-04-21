package com.smart.medilation.ui;

import android.content.Context;
import android.content.Intent;
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
import com.smart.medilation.R;
import com.smart.medilation.adapters.GridSpacingItemDecoration;
import com.smart.medilation.ui.dialog.LoadingDialog;
import com.smart.medilation.ui.doctor.DoctorDashboardActivity;
import com.smart.medilation.ui.login.SelectionActivity;
import com.smart.medilation.utils.PrefManager;

public class BaseFragment extends Fragment {
    public LoadingDialog loadingDialog;
    public PrefManager pref;

    private FirebaseAuth mAuth;


    public BaseActivity.BottomMenuInterface bottomMenuInterface = null;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            bottomMenuInterface = (BaseActivity.BottomMenuInterface) context;
        } catch (Exception ignored){
        }
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
}
