package com.smart.medilation.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.smart.medilation.BuildConfig;
import com.smart.medilation.ui.dialog.LoadingDialog;
import com.smart.medilation.ui.login.SelectionActivity;
import com.smart.medilation.utils.PrefManager;

public class BaseActivity extends AppCompatActivity {
    public LoadingDialog loadingDialog;
    public PrefManager pref;

    private FirebaseAuth mAuth;

    public interface BottomMenuInterface {
        void onNavChange(int value);
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
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }


    protected void showToast(String strMsg) {
        Toast.makeText(this, strMsg , Toast.LENGTH_SHORT).show();
    }
    protected void showLDialog() {
        if (loadingDialog == null)
            loadingDialog = new LoadingDialog(this);
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();
        try {
            loadingDialog.show();
        } catch (Exception ignored){}
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
