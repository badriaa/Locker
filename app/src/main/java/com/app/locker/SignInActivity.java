package com.app.locker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.app.locker.appdata.Constants;
import com.app.locker.appdata.GlobalSharedPrefs;
import com.app.locker.appdata.SessionManager;
import com.app.locker.coord_dashboard.DashboardCordActivity;
import com.app.locker.networkmodels.ErrorResponseModel;
import com.app.locker.networkmodels.UserResponseModel;
import com.app.locker.user_dashboard.DashboardUserActivity;

import net.lateralview.simplerestclienthandler.RestClientManager;
import net.lateralview.simplerestclienthandler.base.RequestCallbacks;
import net.lateralview.simplerestclienthandler.base.RequestHandler;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ua.com.crosp.solutions.library.prettytoast.PrettyToast;

public class SignInActivity extends AppCompatActivity {
    private EditText mUsernameView, mPasswordView;
    private SessionManager sessionManager;

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null)
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void setupUI(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(SignInActivity.this);
                    return false;
                }
            });
        }
        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        new GlobalSharedPrefs(this);

        sessionManager = new SessionManager(this);

        mUsernameView = (EditText) findViewById(R.id.etUsernameSignIn);
        mPasswordView = (EditText) findViewById(R.id.etPasswordSignIn);
        Button btnSignIn = (Button) findViewById(R.id.btnSubmitSignIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLoginSimple();
            }
        });

        TextView tvSignUp = (TextView) findViewById(R.id.tvSignupSignIn);
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                // Closing all the Activities
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // Add new Flag to start new Activity
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        TextView tvForgot = (TextView) findViewById(R.id.tvForgotPassSignIn);
        tvForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
                // Closing all the Activities
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // Add new Flag to start new Activity
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });


        setupUI(findViewById(R.id.rootLayoutSignIn));
    }

    public void attemptLoginSimple() {
        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mUsernameView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            new SignInTask().execute(email, password);
        }
    }

    private class SignInTask extends AsyncTask<String, Void, String> {
        private SweetAlertDialog progressSignin;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressSignin = new SweetAlertDialog(SignInActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            progressSignin.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            progressSignin.setTitleText("Signing In, Please wait..");
            progressSignin.setCancelable(false);
            progressSignin.show();
        }//close onPreExecute

        @Override
        protected String doInBackground(final String... params) {
            String url = Constants.SIGN_IN_URL +
                    "username=" + params[0] +
                    "&password=" + params[1];
            RestClientManager.getInstance().makeJsonRequest(Request.Method.GET,
                    url,
                    new RequestHandler<>(new RequestCallbacks<UserResponseModel, ErrorResponseModel>() {
                        @Override
                        public void onRequestSuccess(UserResponseModel response) {
                            try {
                                progressSignin.dismiss();
                                String status = response.status;
                                if (status.equalsIgnoreCase("success")) {

                                    GlobalSharedPrefs.lockerPref.edit().putString(
                                            "username", response.user_detail.user_name).commit();
                                    GlobalSharedPrefs.lockerPref.edit().putString(
                                            "user_id", response.user_detail.id).commit();
                                    GlobalSharedPrefs.lockerPref.edit().putString(
                                            "name", response.user_detail.name).commit();
                                    GlobalSharedPrefs.lockerPref.edit().putString(
                                            "last_name", response.user_detail.last_name).commit();
                                    GlobalSharedPrefs.lockerPref.edit().putString(
                                            "user_email", response.user_detail.user_email).commit();
                                    GlobalSharedPrefs.lockerPref.edit().putString(
                                            "user_phone", response.user_detail.phone).commit();
                                    GlobalSharedPrefs.lockerPref.edit().putString(
                                            "user_type", response.user_detail.user_type).commit();

                                    // creating a session and fire up dashboard
                                    sessionManager.createLoginSession(response.user_detail.user_name, response.user_detail.user_type);
                                    String userType = response.user_detail.user_type;
                                    Intent i = null;
                                    if (userType.equalsIgnoreCase("user"))
                                        i = new Intent(SignInActivity.this, DashboardUserActivity.class);
//                                    if (userType.user_type.equalsIgnoreCase("admin"))
//                                        i = new Intent(SignInActivity.this, DashboardAdminActivity.class);
                                    if (userType.equalsIgnoreCase("coordinator"))
                                        i = new Intent(SignInActivity.this, DashboardCordActivity.class);
                                    // Closing all the Activities
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    //   Add new Flag to start new Activity
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    finish();

                                }
                                if (response.status.equalsIgnoreCase("failure"))
                                    PrettyToast.showError(getApplicationContext(), response.message);

                            } catch (Exception e) {
                                PrettyToast.showError(getApplicationContext(), "Something went wrong, Please try again");
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onRequestError(ErrorResponseModel error) {
                            if (getApplicationContext() != null) {
                                progressSignin.dismiss();
                                PrettyToast.showError(getApplicationContext(), "There was a problem connecting to server, Please try again");
                            }
                        }
                    }, new HashMap<String, String>()));
            return "";
        }//close doInBackground
    }
}