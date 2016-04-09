package com.app.locker.appdata;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Mushi on 3/31/2016.
 */
public class SessionManager {
    // User name (make variable public to access from outside)
    public static final String KEY_USERNAME = "session_username";
    public static final String KEY_TYPE = "session_user_type";
    private static final String IS_LOGIN = "IsLoggedIn";
    // Shared pref file name
    private static final String PREF_NAME = "LockerPreferences";
    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String username, String type) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_USERNAME, username);
        // Storing email in pref
        editor.putString(KEY_TYPE, type);

        // commit changes
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    public boolean checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            return false;
        }
        return true;
    }

    public String getLoginType() {
        return pref.getString(KEY_TYPE, "");
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.putBoolean(IS_LOGIN, false);
        editor.commit();
    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}
