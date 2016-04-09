package com.app.locker.user_dashboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.app.locker.R;
import com.app.locker.user_dashboard.dialogfragment.ChangeLanguageDialogFragment;
import com.app.locker.user_dashboard.dialogfragment.ContactUsDialogFragment;
import com.app.locker.user_dashboard.dialogfragment.FaqDialogFragment;

/**
 * Created by Mushi on 4/1/2016.
 */
public class FragmentSettingUser extends Fragment {

    private Button btnChangeLanguage, btnNotification, btnFAQ, btnContactUs, btnProblem;

    public FragmentSettingUser() {
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
        return inflater.inflate(R.layout.fragment_setting_user, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnChangeLanguage = (Button) view.findViewById(R.id.btnChangeLanguageSetting);
        btnNotification = (Button) view.findViewById(R.id.btnNotificationsLanguageSetting);
        btnFAQ = (Button) view.findViewById(R.id.btnFaqLanguageSetting);
        btnContactUs = (Button) view.findViewById(R.id.btnContactUsLanguageSetting);
        btnProblem = (Button) view.findViewById(R.id.btnProblemLanguageSetting);

        btnChangeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getActivity().getSupportFragmentManager();
                ChangeLanguageDialogFragment dialog = new ChangeLanguageDialogFragment();
                dialog.show(manager, "dialog");
            }
        });
        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NotificationUserActivity.class);
                startActivity(intent);
            }
        });
        btnFAQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FaqDialogFragment dialog = new FaqDialogFragment();
                dialog.show(manager, "dialog");
            }
        });
        btnContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getActivity().getSupportFragmentManager();
                ContactUsDialogFragment dialog = new ContactUsDialogFragment();
                dialog.show(manager, "dialog");
            }
        });
        btnProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProblemUserActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}