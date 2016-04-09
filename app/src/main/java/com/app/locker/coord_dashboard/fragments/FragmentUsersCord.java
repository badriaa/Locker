package com.app.locker.coord_dashboard.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.app.locker.R;
import com.app.locker.coord_dashboard.ManageUsersCordActivity;
import com.app.locker.coord_dashboard.ViewReportCordActivity;

/**
 * Created by Mushi on 4/4/2016.
 */
public class FragmentUsersCord extends Fragment {

    private Button btnNotification, btnManage, btnViewReport;

    public FragmentUsersCord() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_users_cord, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnNotification = (Button) view.findViewById(R.id.btnNotificationUsersCord);
        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getActivity().getSupportFragmentManager();
                SendNotifDialogFragment dialog = new SendNotifDialogFragment();
                dialog.show(manager, "dialog");
            }
        });
        btnManage = (Button) view.findViewById(R.id.btnManageUsersCord);
        btnManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ManageUsersCordActivity.class);
                startActivity(intent);
            }
        });
        btnViewReport = (Button) view.findViewById(R.id.btnViewReportCord);
        btnViewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ViewReportCordActivity.class);
                startActivity(intent);
            }
        });


    }
}