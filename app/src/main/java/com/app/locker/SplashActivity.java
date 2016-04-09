package com.app.locker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.app.locker.appdata.GlobalSharedPrefs;
import com.app.locker.appdata.SessionManager;
import com.app.locker.coord_dashboard.DashboardCordActivity;
import com.app.locker.extras.LoadingTask;
import com.app.locker.user_dashboard.DashboardUserActivity;

import net.lateralview.simplerestclienthandler.RestClientManager;

import ua.com.crosp.solutions.library.prettytoast.PrettyToast;

public class SplashActivity extends AppCompatActivity implements LoadingTask.LoadingTaskFinishedListener {
    private String TAG = "SplashActivity";
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new GlobalSharedPrefs(this);
        session = new SessionManager(this);
        RestClientManager.initialize(getApplicationContext()).enableDebugLog(true);
        PrettyToast.initIcons();

        new LoadingTask(this).execute();
    }

    @Override
    public void onTaskFinished() {
        if (session.checkLogin()) {
            //navigate to Login
            Intent i = null;
            if (session.getLoginType().equalsIgnoreCase("user"))
                i = new Intent(SplashActivity.this, DashboardUserActivity.class);
//            if (session.getLoginType().equalsIgnoreCase("admin"))
//                i = new Intent(SplashActivity.this, DashboardAdminActivity.class);
            if (session.getLoginType().equalsIgnoreCase("coordinator"))
                i = new Intent(SplashActivity.this, DashboardCordActivity.class);

            if (i == null)
                return;
            startActivity(i);
            finish();
        } else {
            Intent i = new Intent(SplashActivity.this, SignInActivity.class);
            startActivity(i);
            finish();
        }
    }
}
