package com.example.santu.nearme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {

    //Shared Preferences
    SharedPreferences UserPreferences;

    //Editor to edit shared preferences
    SharedPreferences.Editor  editor;

    //SharedPreferences PRIV_MODE
    int PRIVATE_MODE = 0;

    //Context
    Context _context;

    //Variables that I want store
    private static final String USER_NAME = "name";
    private static final String USER_SURNAME = "surname";
    private static final String USER_EMAIL = "email";

    private static final String PREF_NAME = "UserPref";
    private static final String IS_LOGIN = "IsLoggedIn";

    //Costruttore
    SessionManager(Context context){
        this._context = context;
        UserPreferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = UserPreferences.edit();
    }

    public void createLoginSession(String name, String surname, String email){// Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(USER_NAME, name);

        // Storing name in pref
        editor.putString(USER_SURNAME, name);

        // Storing email in pref
        editor.putString(USER_EMAIL, email);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(USER_NAME, UserPreferences.getString(USER_NAME, null));

        // user email id
        user.put(USER_EMAIL, UserPreferences.getString(USER_EMAIL, null));

        // return user
        return user;
    }

    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }
    }

    public boolean isLoggedIn(){
        return UserPreferences.getBoolean(IS_LOGIN, false);
    }

    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

}
