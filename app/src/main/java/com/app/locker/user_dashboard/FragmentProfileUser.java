package com.app.locker.user_dashboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.app.locker.R;
import com.app.locker.appdata.Constants;
import com.app.locker.appdata.GlobalSharedPrefs;
import com.app.locker.appdata.SessionManager;
import com.app.locker.networkmodels.ErrorResponseModel;
import com.app.locker.networkmodels.UserResponseModel;

import net.lateralview.simplerestclienthandler.RestClientManager;
import net.lateralview.simplerestclienthandler.base.RequestCallbacks;
import net.lateralview.simplerestclienthandler.base.RequestHandler;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ua.com.crosp.solutions.library.prettytoast.PrettyToast;

/**
 * Created by Mushi on 4/1/2016.
 */
public class FragmentProfileUser extends Fragment {

    private Button btnSave, btnEdit;
    private EditText mUsernameView,
            mNameView,
            mLastNameView,
            mEmailView,
            mAlterEmailView,
            mPhoneView,
            mAlterPhoneView,
            mPasswordView;
    private boolean mEditFlag = false;
    private SessionManager sessionManager;
    private String userId;

    public FragmentProfileUser() {
        // Required empty public constructor
    }

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
                    hideSoftKeyboard(getActivity());
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_user, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(getActivity());
        userId = GlobalSharedPrefs.lockerPref.getString("user_id", "1");

        mUsernameView = (EditText) view.findViewById(R.id.etUserNameMyProfile);
        mNameView = (EditText) view.findViewById(R.id.etNameMyProfile);
        mLastNameView = (EditText) view.findViewById(R.id.etLastNameMyProfile);
        mEmailView = (EditText) view.findViewById(R.id.etEmailMyProfile);
        mAlterEmailView = (EditText) view.findViewById(R.id.etAlterEmailMyProfile);
        mPhoneView = (EditText) view.findViewById(R.id.etPhoneMyProfile);
        mAlterPhoneView = (EditText) view.findViewById(R.id.etAlterPhoneMyProfile);
        mPasswordView = (EditText) view.findViewById(R.id.etPasswordMyProfile);

        btnEdit = (Button) view.findViewById(R.id.btnEditProfileUser);
        btnSave = (Button) view.findViewById(R.id.btnSaveProfileUser);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditFlag = true;
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLoginSimple();
            }
        });

        setupUI(view.findViewById(R.id.rootLayoutMyProfile));

        new GetUserInfoTask().execute(userId);
    }

    private void attemptLoginSimple() {
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
        final String username = mUsernameView.getText().toString().trim();
        final String name = mNameView.getText().toString().trim();
        final String lastname = mLastNameView.getText().toString().trim();
        final String email = mEmailView.getText().toString().trim();
        final String alteremail = mAlterEmailView.getText().toString().trim();
        final String phone = mPhoneView.getText().toString().trim();
        final String alterphone = mAlterPhoneView.getText().toString().trim();
        final String password = mPasswordView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;


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
            new AlertDialog.Builder(getActivity())
                    .setMessage("are you sure you want to save this information?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            new UpdateUserInfoTask().execute(username, name, lastname, email, alteremail, phone, alterphone, password, userId);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
//        if (mEditFlag) {
//            new AlertDialog.Builder(getActivity())
//                    .setMessage("are you sure you want to discard changes?")
//                    .setCancelable(false)
//                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            FragmentProfileUser.super.onDetach();
//                        }
//                    })
//                    .setNegativeButton("No", null)
//                    .show();
//        } else
//            super.onDetach();
        super.onDetach();
    }


    private class UpdateUserInfoTask extends AsyncTask<String, Void, String> {
        private SweetAlertDialog progressSignin;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressSignin = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
            progressSignin.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            progressSignin.setTitleText("Updating Info, Please wait..");
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
            paramsHash.put("user_id", params[8]);

            String url = Constants.UPDATE_USERINFO_URL;
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
                                    mEditFlag = false;

                                    PrettyToast.showSuccess(getContext(), "Information Updated Successfully.");

                                } else if (response.status.equalsIgnoreCase("user already exist")) {
                                    PrettyToast.showError(getContext(), "This username already exist");
                                } else {
                                    PrettyToast.showError(getContext(), "There was a problem, Please try again");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                PrettyToast.showError(getContext(), "Something went wrong, Please try again");
                            }
                        }

                        @Override
                        public void onRequestError(ErrorResponseModel error) {
                            if (getContext() != null) {
                                progressSignin.dismiss();
                                PrettyToast.showError(getContext(), "There was a problem connecting to server, Please try again");
                            }
                        }
                    }, paramsHash));
            return "";
        }//close doInBackground
    }

    private class GetUserInfoTask extends AsyncTask<String, Void, String> {
        private SweetAlertDialog progressSignin;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressSignin = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
            progressSignin.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            progressSignin.setTitleText("Getting Info, Please wait..");
            progressSignin.setCancelable(false);
            progressSignin.show();
        }//close onPreExecute

        @Override
        protected String doInBackground(final String... params) {

            String url = Constants.GET_USERINFO_URL + "user_id=" + params[0];
            RestClientManager.getInstance().makeJsonRequest(Request.Method.GET,
                    url,
                    new RequestHandler<>(new RequestCallbacks<UserResponseModel, ErrorResponseModel>() {
                        @Override
                        public void onRequestSuccess(UserResponseModel response) {
                            try {
                                progressSignin.dismiss();
                                String status = response.status;
                                if (status.equalsIgnoreCase("success")) {
                                    mUsernameView.setText(response.user_detail.user_name);
                                    mNameView.setText(response.user_detail.name);
                                    mLastNameView.setText(response.user_detail.last_name);
                                    mEmailView.setText(response.user_detail.user_email);
                                    mAlterEmailView.setText(response.user_detail.alternate_email);
                                    mPhoneView.setText(response.user_detail.phone);
                                    mAlterPhoneView.setText(response.user_detail.alternative_phone);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                PrettyToast.showError(getContext(), "Something went wrong, Please try again");
                            }
                        }

                        @Override
                        public void onRequestError(ErrorResponseModel error) {
                            if (getContext() != null) {
                                progressSignin.dismiss();
                            }
                        }
                    }, new HashMap<String, String>()));
            return "";
        }//close doInBackground
    }
}
