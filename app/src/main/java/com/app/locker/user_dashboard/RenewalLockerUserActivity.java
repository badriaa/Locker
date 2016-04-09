package com.app.locker.user_dashboard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.app.locker.R;

public class RenewalLockerUserActivity extends AppCompatActivity {

    private Button btnFinish, btnEditLocation;
    private RadioButton radioTermOne, radioTermTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renewal_locker_user);

        btnEditLocation = (Button) findViewById(R.id.btnEditRenewalLocker);
        btnFinish = (Button) findViewById(R.id.btnFinishRenewalLocker);
        radioTermOne = (RadioButton) findViewById(R.id.radioButtonTermRenewalLocker);
        radioTermTwo = (RadioButton) findViewById(R.id.radioButtonTermTwoRenewalLocker);

        btnEditLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
