package com.app.locker.user_dashboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.app.locker.R;

/**
 * Created by Mushi on 4/1/2016.
 */
public class FragmentLockerUser extends Fragment {

    public FragmentLockerUser() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mylockerpage, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton btnBookLocker = (ImageButton) view.findViewById(R.id.btnBookLockerMyLocker);
        ImageButton btnRenewlLocker = (ImageButton) view.findViewById(R.id.btnRenewalLockerMyLocker);
        ImageButton btnMyLocker = (ImageButton) view.findViewById(R.id.btnLockerMyLocker);

        btnBookLocker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), BookLockerUserActivity.class);
                startActivity(intent);
            }
        });
        btnRenewlLocker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), RenewalLockerUserActivity.class);
                startActivity(intent);
            }
        });
        btnMyLocker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MyLockerUserActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}