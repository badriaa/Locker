package com.app.locker.coord_dashboard;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.app.locker.R;
import com.app.locker.appdata.Constants;
import com.app.locker.appdata.GlobalSharedPrefs;
import com.app.locker.models.ReportLocker;
import com.app.locker.networkmodels.ErrorResponseModel;
import com.app.locker.networkmodels.ReportSuccessResponseModel;

import net.lateralview.simplerestclienthandler.RestClientManager;
import net.lateralview.simplerestclienthandler.base.RequestCallbacks;
import net.lateralview.simplerestclienthandler.base.RequestHandler;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ua.com.crosp.solutions.library.prettytoast.PrettyToast;

public class ViewFloorReportCordActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_floor_report_cord);

        new GlobalSharedPrefs(this);
        String userid = GlobalSharedPrefs.lockerPref.getString("user_id", "");
        String floor = getIntent().getStringExtra("floor");

        listView = (ListView) findViewById(R.id.listViewReserved);
        new GetFloorLockersTask().execute(userid, floor);
    }

    private class GetFloorLockersTask extends AsyncTask<String, Void, String> {
        private SweetAlertDialog progressSignin;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressSignin = new SweetAlertDialog(ViewFloorReportCordActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            progressSignin.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            progressSignin.setTitleText("Loading, Please wait..");
            progressSignin.setCancelable(false);
            progressSignin.show();
        }//close onPreExecute

        @Override
        protected String doInBackground(final String... params) {
            String url = Constants.GET_REPORT_FLOOR_URL + "user_id=" + params[0] + "&floor=" + params[1];
            RestClientManager.getInstance().makeJsonRequest(Request.Method.GET,
                    url,
                    new RequestHandler<>(new RequestCallbacks<ReportSuccessResponseModel, ErrorResponseModel>() {
                        @Override
                        public void onRequestSuccess(ReportSuccessResponseModel response) {
                            try {
                                progressSignin.dismiss();
                                String status = response.status;
                                if (status.equalsIgnoreCase("success")) {
                                    listView.setAdapter(new LockersAdapter(getApplicationContext(), response.lockers));
                                }
                                if (response.status.equalsIgnoreCase("failure"))
                                    PrettyToast.showError(ViewFloorReportCordActivity.this, response.message);

                            } catch (Exception e) {
                                PrettyToast.showError(ViewFloorReportCordActivity.this, "Something went wrong, Please try again");
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onRequestError(ErrorResponseModel error) {
                            progressSignin.dismiss();
                            PrettyToast.showError(ViewFloorReportCordActivity.this, "There was a problem connecting to server, Please try again");
                        }
                    }, new HashMap<String, String>()));
            return "";
        }//close doInBackground
    }

    private class LockersAdapter extends BaseAdapter {
        ReportLocker[] result;
        Context context;
        private LayoutInflater inflater = null;

        public LockersAdapter(Context mainActivity, ReportLocker[] data) {
            result = data;
            context = mainActivity;
            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return result.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Holder holder = new Holder();
            final ReportLocker file = result[position];
            View rowView;

            if (convertView == null) {
                rowView = inflater.inflate(R.layout.report_item, null);
                // Do some initialization
            } else {
                rowView = convertView;
            }

            holder.ivStatus = (ImageView) rowView.findViewById(R.id.ivStatusReport);
            holder.tvLockerNumber = (TextView) rowView.findViewById(R.id.tvLockerReportItem);
            holder.tvStudentId = (TextView) rowView.findViewById(R.id.tvStudentReportItem);
            holder.tvDuration = (TextView) rowView.findViewById(R.id.tvDurReportItem);
            holder.tvCollage = (TextView) rowView.findViewById(R.id.tvCollageReportItem);

            holder.tvLockerNumber.setText(file.locker_number);
            holder.tvStudentId.setText(file.student_id);
            holder.tvDuration.setText(file.duration);
            holder.tvCollage.setText(file.collage);

            if (file.status.equalsIgnoreCase("paid"))
                holder.ivStatus.setImageResource(R.drawable.ic_notif_low);
            else
                holder.ivStatus.setImageResource(R.drawable.ic_notif_high);

            return rowView;
        }

        public class Holder {
            ImageView ivStatus;
            TextView tvLockerNumber;
            TextView tvStudentId;
            TextView tvDuration;
            TextView tvCollage;
        }
    }
}
