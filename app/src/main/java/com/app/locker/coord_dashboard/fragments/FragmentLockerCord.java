package com.app.locker.coord_dashboard.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.app.locker.R;
import com.app.locker.coord_dashboard.NonReservedLockersCordActivity;
import com.app.locker.coord_dashboard.ReservedLockersCordActivity;

/**
 * Created by Mushi on 4/4/2016.
 */
public class FragmentLockerCord extends Fragment {

    private Button btnReservedLockers, btnNonReservedLockers;

    public FragmentLockerCord() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_locker_cord, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnReservedLockers = (Button) view.findViewById(R.id.btnReservedLockers);
        btnReservedLockers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ReservedLockersCordActivity.class);
                startActivity(intent);
            }
        });
        btnNonReservedLockers = (Button) view.findViewById(R.id.btnNonReservedLockers);
        btnNonReservedLockers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NonReservedLockersCordActivity.class);
                startActivity(intent);
            }
        });

    }
}
