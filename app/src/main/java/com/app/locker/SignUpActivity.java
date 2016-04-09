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
import android.widget.Spinner;
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

public class SignUpActivity extends AppCompatActivity {
    private EditText mUsernameView,
            mNameView,
            mLastNameView,
            mEmailView,
            mAlterEmailView,
            mPhoneView,
            mAlterPhoneView,
            mPasswordView;
    private SessionManager sessionManager;
    private Spinner spinner;

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
                    hideSoftKeyboard(SignUpActivity.this);
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
        setContentView(R.layout.activity_sign_up);

        new GlobalSharedPrefs(this);
        sessionManager = new SessionManager(this);

        spinner = (Spinner) findViewById(R.id.spinnerSignUp);
        mUsernameView = (EditText) findViewById(R.id.etUserNameSignUp);
        mNameView = (EditText) findViewById(R.id.etNameSignUp);
        mLastNameView = (EditText) findViewById(R.id.etLastNameSignUp);
        mEmailView = (EditText) findViewById(R.id.etEmailSignUp);
        mAlterEmailView = (EditText) findViewById(R.id.etAlterEmailSignUp);
        mPhoneView = (EditText) findViewById(R.id.etPhoneSignUp);
        mAlterPhoneView = (EditText) findViewById(R.id.etAlterPhoneSignUp);
        mPasswordView = (EditText) findViewById(R.id.etPasswordSignUp);

        Button btnSignIn = (Button) findViewById(R.id.btnSubmitSignUp);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLoginSimple();
            }
        });

        TextView tvSignUp = (TextView) findViewById(R.id.tvSigninSignUp);
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                // Closing all the Activities
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // Add new Flag to start new Activity
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });


        setupUI(findViewById(R.id.rootLayoutSignUp));
    }

    public void attemptLoginSimple() {
        // Reset errors.
        mUsernameView.setError(null);
        mNameView.setError(null);
        mLastNameView.setError(null);
        mEmailView.setError(null);
        mAlterEmailView.setError(null);
        mPhoneView.setError(null);
        mAlterPhoneView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString().trim();
        String name = mNameView.getText().toString().trim();
        String lastname = mLastNameView.getText().toString().trim();
        String email = mEmailView.getText().toString().trim();
        String alteremail = mAlterEmailView.getText().toString().trim();
        String phone = mPhoneView.getText().toString().trim();
        String alterphone = mAlterPhoneView.getText().toString().trim();
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
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(alteremail)) {
            mAlterEmailView.setError(getString(R.string.error_field_required));
            focusView = mAlterEmailView;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(lastname)) {
            mLastNameView.setError(getString(R.string.error_field_required));
            focusView = mLastNameView;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(phone)) {
            mPhoneView.setError(getString(R.string.error_field_required));
            focusView = mPhoneView;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(alterphone)) {
            mAlterPhoneView.setError(getString(R.string.error_field_required));
            focusView = mAlterPhoneView;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            new SignUpTask().execute(username, name, lastname, email, alteremail, phone, alterphone, password, spinner.getSelectedItem().toString());
        }
    }

    private class SignUpTask extends AsyncTask<String, Void, String> {
        private SweetAlertDialog progressSignin;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressSignin = new SweetAlertDialog(SignUpActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            progressSignin.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            progressSignin.setTitleText("Signing Up, Please wait..");
            progressSignin.setCancelable(false);
            progressSignin.show();
        }//close onPreExecute

        @Override
        protected String doInBackground(final String... params) {
            HashMap<String, String> paramsHash = new HashMap<>();
            paramsHash.put("username", params[0]);
            paramsHash.put("name", params[1]);
            paramsHash.put("last_name", params[2]);
            paramsHash.put("email", params[3]);
            paramsHash.put("alter_email", params[4]);
            paramsHash.put("phone", params[5]);
            paramsHash.put("alter_phone", params[6]);
            paramsHash.put("password", params[7]);
            paramsHash.put("user_type", params[8]);

            String url = Constants.SIGN_UP_URL;
            RestClientManager.getInstance().makeJsonRequest(Request.Method.POST,
                    url,
                    new RequestHandler<>(new RequestCallbacks<UserResponseModel, ErrorResponseModel>() {
                        @Override
                        public void onRequestSuccess(UserResponseModel response) {
                            try {
                                progressSignin.dismiss();
                                String status = response.status;
                                if (status.equalsIgnoreCase("success")) {

                                    GlobalSharedPrefs.lockerPref.edit().putString(
                                            "user_id", response.user_detail.id).commit();
                                    GlobalSharedPrefs.lockerPref.edit().putString(
                                            "username", response.user_detail.user_name).commit();
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
                                    Intent i = null;
                                    if (response.user_detail.user_type.equalsIgnoreCase("user"))
                                        i = new Intent(SignUpActivity.this, DashboardUserActivity.class);
//                                    if (response.user_detail[0].user_type.equalsIgnoreCase("admin"))
//                                        i = new Intent(SignUpActivity.this, DashboardAdminActivity.class);
                                    if (response.user_detail.user_type.equalsIgnoreCase("coordinator"))
                                        i = new Intent(SignUpActivity.this, DashboardCordActivity.class);
                                    // Closing all the Activities
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    // Add new Flag to start new Activity
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    finish();

                                } else if (response.status.equalsIgnoreCase("user already exist")) {
                                    PrettyToast.showError(getApplicationContext(), "This username already exist");
                                } else {
                                    PrettyToast.showError(getApplicationContext(), "There was a problem signing up, Please try again");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                PrettyToast.showError(getApplicationContext(), "Something went wrong, Please try again");
                            }
                        }

                        @Override
                        public void onRequestError(ErrorResponseModel error) {
                            if (getApplicationContext() != null) {
                                progressSignin.dismiss();
                                PrettyToast.showError(getApplicationContext(), "There was a problem connecting to server, Please try again");
                            }
                        }
                    }, paramsHash));
            return "";
        }//close doInBackground
    }
}