package com.app.locker.appdata;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Mushi on 3/31/2016.
 */
public class GlobalSharedPrefs {
    // sharedPrefrence
    private static final String PREFS_NAME = "LockerPreferences";
    public static SharedPreferences lockerPref;
    public static String mSelectedLocker;

    public GlobalSharedPrefs(Context con) {
        lockerPref = con.getSharedPreferences(PREFS_NAME, 0);
    }
}
