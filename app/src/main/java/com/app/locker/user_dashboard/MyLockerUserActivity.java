package com.app.locker.user_dashboard;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.android.volley.Request;
import com.app.locker.R;
import com.app.locker.appdata.Constants;
import com.app.locker.appdata.GlobalSharedPrefs;
import com.app.locker.networkmodels.ErrorResponseModel;
import com.app.locker.networkmodels.ReservedResponseModel;

import net.lateralview.simplerestclienthandler.RestClientManager;
import net.lateralview.simplerestclienthandler.base.RequestCallbacks;
import net.lateralview.simplerestclienthandler.base.RequestHandler;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ua.com.crosp.solutions.library.prettytoast.PrettyToast;

public class MyLockerUserActivity extends AppCompatActivity {

    private TextView tvLocation, tvEndingDate, tvContravention;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_locker_user);
        new GlobalSharedPrefs(this);

        tvLocation = (TextView) findViewById(R.id.tvLocationMylocker);
        tvEndingDate = (TextView) findViewById(R.id.tvEndingDateMylocker);
        tvContravention = (TextView) findViewById(R.id.tvContraMylocker);

        new GetLockerInfoTask().execute(GlobalSharedPrefs.lockerPref.getString("user_id", ""));
    }

    private class GetLockerInfoTask extends AsyncTask<String, Void, String> {
        private SweetAlertDialog progressSignin;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressSignin = new SweetAlertDialog(MyLockerUserActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            progressSignin.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            progressSignin.setTitleText("Loading, Please wait..");
            progressSignin.setCancelable(false);
            progressSignin.show();
        }//close onPreExecute

        @Override
        protected String doInBackground(final String... params) {
            String url = Constants.GET_LOCKERINFO_URL +
                    "user_id=" + params[0];
            RestClientManager.getInstance().makeJsonRequest(Request.Method.GET,
                    url,
                    new RequestHandler<>(new RequestCallbacks<ReservedResponseModel, ErrorResponseModel>() {
                        @Override
                        public void onRequestSuccess(ReservedResponseModel response) {
                            try {
                                progressSignin.dismiss();
                                String status = response.status;
                                if (status.equalsIgnoreCase("success")) {
                                    if (response.lockers.length > 0) {
                                        String location = String.format("%s\n%s", response.lockers[0].locker.locker_number,
                                                response.lockers[0].locker.floor);
                                        tvLocation.setText(location);
                                        tvEndingDate.setText(response.lockers[0].contract.end_of_booking);
                                    }
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
