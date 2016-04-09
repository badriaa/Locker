package com.app.locker.coord_dashboard.fragments;

import android.app.Activity;
import android.content.DialogInterface;
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
import android.widget.TextView;

import com.android.volley.Request;
import com.app.locker.R;
import com.app.locker.appdata.Constants;
import com.app.locker.appdata.GlobalSharedPrefs;
import com.app.locker.extras.MyDialogCloseListener;
import com.app.locker.networkmodels.ErrorResponseModel;
import com.app.locker.networkmodels.SuccessResponseModel;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

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
public class ConfirmBookingDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private EditText mPasswordView;
    private Button btnSend;
    private TextView paymentDate;
    private TextView paymentDateTwo;
    private String userId;
    private String mBookingId;
    private String mTerm;

    private int year;
    private int month;
    private int day;

    public ConfirmBookingDialogFragment() {
    }

    public ConfirmBookingDialogFragment(String bId, String term) {
        mBookingId = bId;
        mTerm = term;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_confirm_booking, null, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCancelable(false);

        ImageView btnExit = (ImageView) view.findViewById(R.id.btnExitConfDialog);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.gc();
                dismiss();
            }
        });

        userId = GlobalSharedPrefs.lockerPref.getString("user_id", "0");

        mPasswordView = (EditText) view.findViewById(R.id.etPassConfDialog);
        paymentDate = (TextView) view.findViewById(R.id.tvPaymentDateConfDialog);
        paymentDateTwo = (TextView) view.findViewById(R.id.tvPaymentTwoDateConfDialog);
        paymentDateTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        ConfirmBookingDialogFragment.this,
                        year,
                        month,
                        day
                );
                dpd.setThemeDark(true);
                dpd.dismissOnPause(true);
                dpd.showYearPickerFirst(true);
                dpd.setAccentColor(Color.parseColor("#009688"));
                dpd.setTitle("Payment Date");
                dpd.setOnDateSetListener(ConfirmBookingDialogFragment.this);

                dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");

            }
        });
        paymentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        ConfirmBookingDialogFragment.this,
                        year,
                        month,
                        day
                );
                dpd.setThemeDark(true);
                dpd.dismissOnPause(true);
                dpd.showYearPickerFirst(true);
                dpd.setAccentColor(Color.parseColor("#009688"));
                dpd.setTitle("Payment Date");
                dpd.setOnDateSetListener(ConfirmBookingDialogFragment.this);

                dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");

            }
        });


        btnSend = (Button) view.findViewById(R.id.btnSendConfDialog);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptConfirm();
            }
        });

        setCurrentDateOnView();
    }

    // display current date
    public void setCurrentDateOnView() {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
    }

    private void attemptConfirm() {
        // Reset errors.
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String password = mPasswordView.getText().toString().trim();
        String payDate = paymentDate.getText().toString().trim();
        int eYear = 0;
        if (mTerm.equalsIgnoreCase("term"))
            eYear = year + 1;
        else
            eYear = year + 2;
        String endDate = String.format("%d/%d/%d", eYear, month, day);

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }
        if (TextUtils.isEmpty(payDate)) {
            cancel = true;
        }
        if (TextUtils.isEmpty(endDate)) {
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            if (focusView != null)
                focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            new ConfirmBookingTask().execute(userId, mBookingId, payDate, endDate, password);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Activity activity = getActivity();
        if (activity instanceof MyDialogCloseListener)
            ((MyDialogCloseListener) activity).handleDialogClose(dialog);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int yearSelected, int monthOfYear, int dayOfMonth) {
        year = yearSelected;
        month = monthOfYear;
        day = dayOfMonth;
        String date = "" + dayOfMonth + "/" + (++monthOfYear) + "/" + year;
        paymentDate.setText(date);
    }

    private class ConfirmBookingTask extends AsyncTask<String, Void, String> {
        private SweetAlertDialog progressSignin;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressSignin = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
            progressSignin.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            progressSignin.setTitleText("Updating, Please wait..");
            progressSignin.setCancelable(false);
            progressSignin.show();
        }//close onPreExecute

        @Override
        protected String doInBackground(final String... params) {
            String url = Constants.RESERVE_LOCKER_URL;
            HashMap<String, String> paramsHash = new HashMap<>();
            paramsHash.put("co_user_id", params[0]);
            paramsHash.put("booking_id", params[1]);
            paramsHash.put("payment_date", params[2]);
            paramsHash.put("ending_date", params[3]);
            paramsHash.put("locker_password", params[4]);

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
