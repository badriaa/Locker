package com.app.locker.user_dashboard.dialogfragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;

import com.app.locker.R;

/**
 * Created by Mushi on 4/3/2016.
 */
public class ChangeLanguageDialogFragment extends DialogFragment {

    private Button btnFinish;
    private RadioButton rbEnglish, rbArabic;

    public ChangeLanguageDialogFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_chhangethelanguage, null, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnFinish = (Button) view.findViewById(R.id.btnFinishLanguageDialog);
        rbEnglish = (RadioButton) view.findViewById(R.id.rbEnglishLanguageDialog);
        rbEnglish = (RadioButton) view.findViewById(R.id.rbArabicLanguageDialog);

    }
}
