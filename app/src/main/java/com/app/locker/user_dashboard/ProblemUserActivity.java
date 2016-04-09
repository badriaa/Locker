package com.app.locker.user_dashboard;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.android.volley.Request;
import com.app.locker.R;
import com.app.locker.appdata.Constants;
import com.app.locker.appdata.GlobalSharedPrefs;
import com.app.locker.networkmodels.ErrorResponseModel;
import com.app.locker.networkmodels.SuccessResponseModel;
import com.soundcloud.android.crop.Crop;

import net.lateralview.simplerestclienthandler.RestClientManager;
import net.lateralview.simplerestclienthandler.base.RequestCallbacks;
import net.lateralview.simplerestclienthandler.base.RequestHandler;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ua.com.crosp.solutions.library.prettytoast.PrettyToast;

/**
 * Created by Mushi on 4/3/2016.
 */
public class ProblemUserActivity extends AppCompatActivity {

    private Button btnSend;
    private ImageButton btnScreenShot;
    private EditText etDescrition;
    private String TAG = "ProblemActivity";
    private String encodedImage = "";
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proplem);

        new GlobalSharedPrefs(this);

        userId = GlobalSharedPrefs.lockerPref.getString("user_id", "0");

        btnSend = (Button) findViewById(R.id.btnSendProblem);
        btnScreenShot = (ImageButton) findViewById(R.id.ibScreenShotProblem);
        etDescrition = (EditText) findViewById(R.id.etProblemDescProblem);

        btnScreenShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crop.pickImage(ProblemUserActivity.this);
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attempSendingProblem();
            }
        });
    }

    private void attempSendingProblem() {
        // Reset errors.
        etDescrition.setError(null);

        // Store values at the time of the login attempt.
        String desc = etDescrition.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(desc)) {
            etDescrition.setError(getString(R.string.error_field_required));
            focusView = etDescrition;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            new SendProblemTask().execute(userId, desc, encodedImage);
        }
    }

    private void convertAndSave(String path) {

        final int DESIRED_WIDTH = 640;
        final BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        sizeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, sizeOptions);
        final float widthSampling = sizeOptions.outWidth / DESIRED_WIDTH;
        sizeOptions.inJustDecodeBounds = false;
        sizeOptions.inSampleSize = (int) widthSampling;
        final Bitmap bm = BitmapFactory.decodeFile(path, sizeOptions);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] byteArrayImage = baos.toByteArray();
        encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

        btnScreenShot.setImageBitmap(bm);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == Activity.RESULT_OK && data != null) {
            Log.e(TAG, "Result Received");
            handleImage(resultCode, data);
        }
    }

    private void handleImage(int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {
            convertAndSave(Crop.getOutput(result).getPath());
        } else if (resultCode == Crop.RESULT_ERROR) {
            PrettyToast.showError(this, "" + Crop.getError(result).getMessage());
        }
    }

    private class SendProblemTask extends AsyncTask<String, Void, String> {
        private SweetAlertDialog progressSignin;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressSignin = new SweetAlertDialog(ProblemUserActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            progressSignin.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            progressSignin.setTitleText("Sending, Please wait..");
            progressSignin.setCancelable(false);
            progressSignin.show();
        }//close onPreExecute

        @Override
        protected String doInBackground(final String... params) {
            String url = Constants.SEND_PROBLEM_URL;
            HashMap<String, String> paramsHash = new HashMap<>();
            paramsHash.put("user_id", params[0]);
            paramsHash.put("description", params[1]);
            paramsHash.put("screen_shot", params[2]);

            RestClientManager.getInstance().makeJsonRequest(Request.Method.POST,
                    url,
                    new RequestHandler<>(new RequestCallbacks<SuccessResponseModel, ErrorResponseModel>() {
                        @Override
                        public void onRequestSuccess(SuccessResponseModel response) {
                            try {
                                progressSignin.dismiss();
                                String status = response.status;
                                if (status.equalsIgnoreCase("success")) {
                                    PrettyToast.showSuccess(getApplicationContext(), response.message);
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
                    }, paramsHash));
            return "res";
        }//close doInBackground
    }
}