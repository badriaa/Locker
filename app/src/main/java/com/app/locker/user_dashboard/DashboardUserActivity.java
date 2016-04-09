package com.app.locker.user_dashboard;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.app.locker.R;
import com.app.locker.appdata.GlobalSharedPrefs;
import com.app.locker.commonfragments.FragmentHome;
import com.app.locker.commonfragments.FragmentSignOut;

import it.neokree.googlenavigationdrawer.GAccount;
import it.neokree.googlenavigationdrawer.GSection;
import it.neokree.googlenavigationdrawer.GoogleNavigationDrawer;

/**
 * Created by Mushi on 3/31/2016.
 */
public class DashboardUserActivity extends GoogleNavigationDrawer {

    GAccount account;
    GSection sectionHome, sectionLocker, sectionProfile, sectionSetting, sectionSignout;

    @Override
    public void init(Bundle savedInstanceState) {

        new GlobalSharedPrefs(this);

        String name = GlobalSharedPrefs.lockerPref.getString("name", "");
        String email = GlobalSharedPrefs.lockerPref.getString("user_email", "");

        account = new GAccount(name, email, new ColorDrawable(Color.parseColor("#9e9e9e")),
                getResources().getDrawable(R.drawable.img_dash));
        this.addAccount(account);
        account.setPhoto(getResources().getDrawable(R.drawable.ic_profile));

        // create sections
        sectionHome = this.newSection("Home", new FragmentHome());
        sectionLocker = this.newSection("My Locker", new FragmentLockerUser());
        sectionProfile = this.newSection("Profile", new FragmentProfileUser());
        sectionSetting = this.newSection("Setting", new FragmentSettingUser());
        sectionSignout = this.newSection("Sign Out", new FragmentSignOut());

        // add your sections to the drawer
        this.addSection(sectionHome);
        this.addDivisor();
        this.addSection(sectionLocker);
        this.addSection(sectionProfile);
        this.addSection(sectionSetting);
        this.addBottomSection(sectionSignout);
    }
}