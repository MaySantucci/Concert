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
    public static final String USER_NAME = "name";
    public static final String USER_SURNAME = "surname";
    public static final String USER_EMAIL = "email";
    public static final String USER_PUB = "id_pub";
    public static final String USER_GROUP = "id_group";

    public static final String PREF_NAME = "UserPref";
    public static final String IS_LOGIN = "IsLoggedIn";

    //Costruttore
    SessionManager(Context context){
        this._context = context;
        UserPreferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = UserPreferences.edit();
    }

    public void createLoginSession(String name, String surname, String email,String id_group, String id_pub){// Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        editor.putString(USER_NAME, name);
        editor.putString(USER_SURNAME, surname);
        editor.putString(USER_EMAIL, email);
        editor.putString(USER_GROUP, id_group);
        editor.putString(USER_PUB, id_pub);

        // commit changes
        editor.commit();
    }


    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(USER_EMAIL, UserPreferences.getString(USER_EMAIL, null));
        user.put(USER_NAME, UserPreferences.getString(USER_NAME, null));
        user.put(USER_SURNAME, UserPreferences.getString(USER_SURNAME, null));
        user.put(USER_PUB, UserPreferences.getString(USER_PUB, null));
        user.put(USER_GROUP, UserPreferences.getString(USER_GROUP, null));

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
