package com.app.locker.user_dashboard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.android.volley.Request;
import com.app.locker.R;
import com.app.locker.appdata.Constants;
import com.app.locker.appdata.GlobalSharedPrefs;
import com.app.locker.networkmodels.ErrorResponseModel;
import com.app.locker.networkmodels.SuccessResponseModel;
import com.app.locker.user_dashboard.dialogfragment.ShowLockersDialogFragment;

import net.lateralview.simplerestclienthandler.RestClientManager;
import net.lateralview.simplerestclienthandler.base.RequestCallbacks;
import net.lateralview.simplerestclienthandler.base.RequestHandler;

import java.util.Calendar;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ua.com.crosp.solutions.library.prettytoast.PrettyToast;

public class BookLockerUserActivity extends AppCompatActivity {

    private Spinner spinnerCollage, spinnerDept, spinnerLevel, spinnerFloor, spinnerBlock;
    private Button btnBooking, btnChooseLocker;
    private RadioButton term, twoTerm;
    private EditText mStudentView;
    private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_locker_user);
        new GlobalSharedPrefs(this);
        userid = GlobalSharedPrefs.lockerPref.getString("user_id", "");
        GlobalSharedPrefs.mSelectedLocker = null;

        mStudentView = (EditText) findViewById(R.id.etStudentIdBookLocker);
        spinnerCollage = (Spinner) findViewById(R.id.spinnerCollageBooklocker);
        spinnerDept = (Spinner) findViewById(R.id.spinnerDepartBooklocker);
        spinnerLevel = (Spinner) findViewById(R.id.spinnerLevelBooklocker);
        spinnerFloor = (Spinner) findViewById(R.id.spinnerFloorBooklocker);
        spinnerBlock = (Spinner) findViewById(R.id.spinnerBlockBooklocker);
        term = (RadioButton) findViewById(R.id.checkBoxTerm);
        twoTerm = (RadioButton) findViewById(R.id.checkBox2Term);

        btnBooking = (Button) findViewById(R.id.btnBookingBooklocker);
        btnBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptToBookLocker();
            }
        });
        btnChooseLocker = (Button) findViewById(R.id.btnChooseBooklocker);
        btnChooseLocker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getSupportFragmentManager();
                ShowLockersDialogFragment dialog = new ShowLockersDialogFragment((String) spinnerBlock.getSelectedItem(),
                        (String) spinnerFloor.getSelectedItem());
                dialog.show(manager, "dialog");
            }
        });
    }

    private void attemptToBookLocker() {

        mStudentView.setError(null);
        // Store values at the time of the login attempt.
        final String lockerNum = GlobalSharedPrefs.mSelectedLocker;
        final String studentId = mStudentView.getText().toString().trim();
        String temp = null;
        if (term.isChecked())
            temp = "term";
        else
            temp = "two term";
        final String duration = temp;
        final String collage = spinnerCollage.getSelectedItem().toString().trim();
        final String dept = spinnerDept.getSelectedItem().toString().trim();
        final String floor = spinnerFloor.getSelectedItem().toString().trim();
        final String level = spinnerLevel.getSelectedItem().toString().trim();

        final Calendar c = Calendar.getInstance();
        final String bookingDate = String.format("%d/%d/%d", c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(studentId)) {
            mStudentView.setError(getString(R.string.error_field_required));
            focusView = mStudentView;
            cancel = true;
        }
        if (TextUtils.isEmpty(lockerNum)) {
            cancel = true;
        }
        if (TextUtils.isEmpty(duration)) {
            cancel = true;
        }
        if (TextUtils.isEmpty(collage)) {
            cancel = true;
        }
        if (TextUtils.isEmpty(dept)) {
            cancel = true;
        }
        if (TextUtils.isEmpty(level)) {
            cancel = true;
        }
        if (TextUtils.isEmpty(floor)) {
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            if (focusView != null)
                focusView.requestFocus();
        } else {
            // show the eula license agreement
            String title = "شروط تأجير خزائن الطالبات";
            //Includes the updates as well so users know what changed.
            String message = agreementText;

            new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("I Accept", new Dialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Mark this version as read.
                            new BookLockerTask().execute(
                                    lockerNum,
                                    studentId,
                                    duration,
                                    collage,
                                    dept,
                                    level,
                                    floor,
                                    bookingDate);
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new Dialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Close the activity as they have declined the EULA
                            dialog.dismiss();
                        }
                    }).show();
        }
    }

    private class BookLockerTask extends AsyncTask<String, Void, String> {
        private SweetAlertDialog progressSignin;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressSignin = new SweetAlertDialog(BookLockerUserActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            progressSignin.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            progressSignin.setTitleText("Booking in progress, Please wait..");
            progressSignin.setCancelable(false);
            progressSignin.show();
        }//close onPreExecute

        @Override
        protected String doInBackground(final String... params) {
            String url = Constants.BOOK_LOCKER_URL;
            HashMap<String, String> paramsHash = new HashMap<>();

            paramsHash.put("user_id", userid);
            paramsHash.put("locker_number", params[0]);
            paramsHash.put("student_id", params[1]);
            paramsHash.put("duration", params[2]);
            paramsHash.put("collage", params[3]);
            paramsHash.put("department", params[4]);
            paramsHash.put("level", params[5]);
            paramsHash.put("floor", params[6]);
            paramsHash.put("date", params[7]);

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

    private final String agreementText = "روط تأجير خزائن الطالبات\n" +
            "1.\tيبدأ التأجير من التاريخ المذكور وينتهي بنهاية المدة المحددة في العقد ويعد قابل للتجديد باتفاق الطرفين.\n" +
            "2.\tرسم تأجير الخزنة (100 ريال) للسنة الدراسية الواحدة (50 ريال) للفصل الدراسي الواحد، وينطبق على الجزء من مدة الفصل الدراسي الواحد ما ينطبق على كامل مدته في احتساب رسوم التأجير.\n" +
            "3.\tيعد المبلغ المدفوع عن قيمة الإيجار مسترد لمدة أسبوع من تاريخ توقيع العقد بشرط إعادة العقد للموظفة المسئولة.\n" +
            "4.\tتدفع الطالبة عند الاستئجار مبلغ إضافي (50 ريال) كتأمين مسترد من المخالصة ، وتخصم منه أي غرامات تفرض على الطالبة بسبب سوء استخدام الخزنة أو ضياع الرقم السري.\n" +
            "5.\tفي حال استنفاد مبلغ التأمين خلال مدة العقد تلزم الطالبة بدفع تأمين جديد (50 ريال).\n" +
            "6.\tتلتزم الطالبة بالمحافظة على الخزنة والرقم السري ومراعاة أنظمة الجامعة والكلية في طريقة استخدام الخزنة ويحق لإدارة صندوق الطالبات فسخ العقد في حال وجود أي مخالفة للأنظمة المذكورة.\n" +
            "7.\tتلتزم الطالبة بعدم استخدام الخزنة لحفظ أشياء غير مصرح بدخولها للجامعة .\n" +
            "8.\tاستبدال البطارية خلال فترة العقد من مسؤولية الطالبة .\n" +
            "9.\tلا يجوز اشتراك أكثر من طالبة بالخزنة وفي حال استخدام الخزنة من قبل عدة طالبات تكون الطالبة (الطرف الثاني) صاحبة الخزنة هي المسئولة مسئولية تامة أمام إدارة الصندوق.\n" +
            "10.\tتلتزم الطالبة بإبلاغ الموظفة المسئولة عن الخزائن في حال فقدان الرقم السري للخزنة وإدارة الصندوق الحق في إلزام الطالبة بدفع غرامة (50 ريال) بعد ثلاث حالات فقدان للرقم السري.\n" +
            "11.\tينتهي العقد بمجرد انقطاع الطالبة عن الدراسة أو تسليم الخزنة لإدارة الصندوق وإجراء المخالصة .\n" +
            "12.\tلا تعد إدارة صندوق الطالبات مسئولة بأي شكل من الأشكال عن تبعات إهمال أو تساهل الطالبة (الطرف الثاني) في إفشاء الرقم السري لاستخدام الخزنة لأي طرف ثالث .\n" +
            "13.\tلا يتم إجراء المخالصة وإخلاء طرف الطالبة ورد التأمين من قبل إدارة صندوق الطالبات إلا بعد تسليم الخزنة والرقم السري .\n" +
            "14.\tفي حالة عدم تسليم الخزينة في الوقت المحدد سوف يتم مصادرة التأمين .\n" +
            "15.\tلا يحق للطالبة استئجار أكثر من خزنة واحدة باسمها.\n";
}
