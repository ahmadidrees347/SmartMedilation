package com.smart.medilation.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "smartMediation";

    private static final String IsAdminLogin = "IsAdminLogin";
    private static final String IsDocLogin = "IsDocLogin";
    private static final String Login = "Login";
    private static final String USERNAME = "USERNAME";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogIn(boolean login) {
        editor.putBoolean(Login, login);
        editor.commit();
    }

    public void setUserName(String name) {
        editor.putString(USERNAME, name);
        editor.commit();
    }

    public String getUserName() {
        return pref.getString(USERNAME, "");
    }

    public boolean getLogin() {
        return pref.getBoolean(Login, false);
    }

    public void setIsDocLogin(boolean isDoc) {
        editor.putBoolean(IsDocLogin, isDoc);
        editor.commit();
    }

    public boolean getIsDocLogin() {
        return pref.getBoolean(IsDocLogin, false);
    }

    public void setIsAdminLogin(boolean isAdminLogin) {
        editor.putBoolean(IsAdminLogin, isAdminLogin);
        editor.commit();
    }

    public boolean getIsAdminLogin() {
        return pref.getBoolean(IsAdminLogin, false);
    }

}
