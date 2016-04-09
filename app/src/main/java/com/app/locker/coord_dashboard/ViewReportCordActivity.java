package com.app.locker.coord_dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.app.locker.R;

public class ViewReportCordActivity extends AppCompatActivity {
    private Spinner spinnerLevel, spinnerFloor;
    private Button btnLevel, btnFloor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report_cord);

        spinnerFloor = (Spinner) findViewById(R.id.spinnerFloorViewReport);
        spinnerLevel = (Spinner) findViewById(R.id.spinnerLevelViewReport);

        btnFloor = (Button) findViewById(R.id.btnFloorViewReport);
        btnFloor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewReportCordActivity.this, ViewFloorReportCordActivity.class);
                intent.putExtra("floor", (String) spinnerFloor.getSelectedItem());
                startActivity(intent);
            }
        });
        btnLevel = (Button) findViewById(R.id.btnLevelViewReport);
        btnLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewReportCordActivity.this, ViewLevelReportCordActivity.class);
                intent.putExtra("level", (String) spinnerLevel.getSelectedItem());
                startActivity(intent);
            }
        });
    }
}
