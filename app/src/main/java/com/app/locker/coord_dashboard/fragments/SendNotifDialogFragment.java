package com.app.locker.coord_dashboard.fragments;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.android.volley.Request;
import com.app.locker.R;
import com.app.locker.appdata.Constants;
import com.app.locker.appdata.GlobalSharedPrefs;
import com.app.locker.networkmodels.ErrorResponseModel;
import com.app.locker.networkmodels.SuccessResponseModel;

import net.lateralview.simplerestclienthandler.RestClientManager;
import net.lateralview.simplerestclienthandler.base.RequestCallbacks;
import net.lateralview.simplerestclienthandler.base.RequestHandler;

import java.util.Calendar;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ua.com.crosp.solutions.library.prettytoast.PrettyToast;

/**
 * Created by Mushi on 4/3/2016.
 */
public class SendNotifDialogFragment extends DialogFragment {

    private EditText etDescription;
    private Spinner spinnerPriority;
    private Button btnSend;
    private String userId;

    public SendNotifDialogFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_send_notifications, null, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCancelable(false);

        ImageView btnExit = (ImageView) view.findViewById(R.id.btnExitNotifDialog);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.gc();
                dismiss();
            }
        });

        userId = GlobalSharedPrefs.lockerPref.getString("user_id", "0");

        etDescription = (EditText) view.findViewById(R.id.etNotifDescNotifDialog);
        spinnerPriority = (Spinner) view.findViewById(R.id.spinnerPriorityNotifDialog);
        btnSend = (Button) view.findViewById(R.id.btnSendNotifDialog);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSendNotification();
            }
        });
    }

    private void attemptSendNotification() {
        // Reset errors.
        etDescription.setError(null);

        // Store values at the time of the login attempt.
        String desc = etDescription.getText().toString().trim();
        String priority = spinnerPriority.getSelectedItem().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(desc)) {
            etDescription.setError(getString(R.string.error_field_required));
            focusView = etDescription;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(priority)) {
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            if (focusView != null)
                focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            final Calendar c = Calendar.getInstance();
            final String date = String.format("%d/%d/%d", c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            new SendNotificationTask().execute(userId, desc, priority, date);
        }
    }

    private class SendNotificationTask extends AsyncTask<String, Void, String> {
        private SweetAlertDialog progressSignin;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressSignin = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
            progressSignin.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            progressSignin.setTitleText("Sending, Please wait..");
            progressSignin.setCancelable(false);
            progressSignin.show();
        }//close onPreExecute

        @Override
        protected String doInBackground(final String... params) {
            String url = Constants.SEND_NOTIFICATIONS_URL;
            HashMap<String, String> paramsHash = new HashMap<>();
            paramsHash.put("user_id", params[0]);
            paramsHash.put("description", params[1]);
            paramsHash.put("priority", params[2]);
            paramsHash.put("date", params[3]);

            RestClientManager.getInstance().makeJsonRequest(Request.Method.POST,
                    url,
                    new RequestHandler<>(new RequestCallbacks<SuccessResponseModel, ErrorResponseModel>() {
                        @Override
                        public void onRequestSuccess(SuccessResponseModel response) {
                            try {
                                progressSignin.dismiss();
                                String status = response.status;
                                if (status.equalsIgnoreCase("success")) {
                                    PrettyToast.showSuccess(getContext(), response.message);
                                    getDialog().dismiss();
                                }
                                if (response.status.equalsIgnoreCase("failure"))
                                    PrettyToast.showError(getContext(), response.message);

                            } catch (Exception e) {
                                PrettyToast.showError(getContext(), "Something went wrong, Please try again");
                                e.printStackTrace();
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
            return "res";
        }//close doInBackground
    }
}
