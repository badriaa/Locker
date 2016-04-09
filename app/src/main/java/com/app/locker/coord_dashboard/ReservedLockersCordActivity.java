package com.app.locker.coord_dashboard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.app.locker.models.Reserved;
import com.app.locker.networkmodels.ErrorResponseModel;
import com.app.locker.networkmodels.ReservedResponseModel;
import com.app.locker.networkmodels.SuccessResponseModel;

import net.lateralview.simplerestclienthandler.RestClientManager;
import net.lateralview.simplerestclienthandler.base.RequestCallbacks;
import net.lateralview.simplerestclienthandler.base.RequestHandler;

import java.util.Calendar;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ua.com.crosp.solutions.library.prettytoast.PrettyToast;

public class ReservedLockersCordActivity extends AppCompatActivity {

    private ListView listView;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserved_lockers_cord);
        new GlobalSharedPrefs(this);

        userId = GlobalSharedPrefs.lockerPref.getString("user_id", "");

        listView = (ListView) findViewById(R.id.listViewReserved);
        new GetReservedLockersTask().execute(userId);
    }

    private class GetReservedLockersTask extends AsyncTask<String, Void, String> {
        private SweetAlertDialog progressSignin;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressSignin = new SweetAlertDialog(ReservedLockersCordActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            progressSignin.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            progressSignin.setTitleText("Loading, Please wait..");
            progressSignin.setCancelable(false);
            progressSignin.show();
        }//close onPreExecute

        @Override
        protected String doInBackground(final String... params) {
            String url = Constants.GET_RESERVED_URL + "user_id=" + params[0];
            RestClientManager.getInstance().makeJsonRequest(Request.Method.GET,
                    url,
                    new RequestHandler<>(new RequestCallbacks<ReservedResponseModel, ErrorResponseModel>() {
                        @Override
                        public void onRequestSuccess(ReservedResponseModel response) {
                            try {
                                progressSignin.dismiss();
                                String status = response.status;
                                if (status.equalsIgnoreCase("success")) {
                                    listView.setAdapter(new ReservedAdapter(getApplicationContext(), response.lockers));
                                }
                                if (response.status.equalsIgnoreCase("failure"))
                                    PrettyToast.showError(ReservedLockersCordActivity.this, response.message);

                            } catch (Exception e) {
                                PrettyToast.showError(ReservedLockersCordActivity.this, "Something went wrong, Please try again");
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onRequestError(ErrorResponseModel error) {
                            progressSignin.dismiss();
                            PrettyToast.showError(ReservedLockersCordActivity.this, "There was a problem connecting to server, Please try again");
                        }
                    }, new HashMap<String, String>()));
            return "";
        }//close doInBackground
    }

    private class ReservedAdapter extends BaseAdapter {
        Reserved[] result;
        Context context;
        private LayoutInflater inflater = null;

        public ReservedAdapter(Context mainActivity, Reserved[] data) {
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
            final Reserved file = result[position];
            View rowView;

            if (convertView == null) {
                rowView = inflater.inflate(R.layout.reserved_item, null);
                // Do some initialization
            } else {
                rowView = convertView;
            }

            holder.ivPopupMenu = (ImageView) rowView.findViewById(R.id.ivPopupRItem);
            holder.tvLockerNumber = (TextView) rowView.findViewById(R.id.tvLockerNumberRItem);
            holder.tvUsername = (TextView) rowView.findViewById(R.id.tvUsernameRItem);
            holder.tvStudentId = (TextView) rowView.findViewById(R.id.tvStudentIdRItem);
            holder.tvBookingDate = (TextView) rowView.findViewById(R.id.tvBookingDateRItem);
            holder.tvEndingDate = (TextView) rowView.findViewById(R.id.tvEndingDateRItem);

            holder.tvLockerNumber.setText(file.locker.locker_number);
            holder.tvUsername.setText(file.user.user_name);
            holder.tvStudentId.setText(file.student_id);
            holder.tvBookingDate.setText(file.contract.date_of_booking);
            holder.tvEndingDate.setText(file.contract.end_of_booking);

            holder.ivPopupMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PopupMenu popmenu = new PopupMenu(ReservedLockersCordActivity.this, holder.ivPopupMenu);
                    popmenu.getMenuInflater().inflate(R.menu.reserve_popup_menu, popmenu.getMenu());
                    popmenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.move_locker:
                                    new AlertDialog.Builder(ReservedLockersCordActivity.this)
                                            .setMessage("are you sure you want to move this locker?")
                                            .setCancelable(false)
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {

                                                    final Calendar c = Calendar.getInstance();
                                                    final String bookingDate = String.format("%d/%d/%d",
                                                            c.get(Calendar.YEAR),
                                                            c.get(Calendar.MONTH),
                                                            c.get(Calendar.DAY_OF_MONTH));
                                                    new MoveBookingTask().execute(userId, file.id, file.contract.id, bookingDate);
                                                }
                                            })
                                            .setNegativeButton("No", null)
                                            .show();
                                    break;
                            }
                            return true;
                        }
                    });
                    popmenu.show();
                }
            });

            return rowView;
        }

        public class Holder {
            ImageView ivPopupMenu;
            TextView tvLockerNumber;
            TextView tvUsername;
            TextView tvStudentId;
            TextView tvBookingDate;
            TextView tvEndingDate;
        }
    }

    private class MoveBookingTask extends AsyncTask<String, Void, String> {
        private SweetAlertDialog progressSignin;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressSignin = new SweetAlertDialog(ReservedLockersCordActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            progressSignin.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            progressSignin.setTitleText("Moving, Please wait..");
            progressSignin.setCancelable(false);
            progressSignin.show();
        }//close onPreExecute

        @Override
        protected String doInBackground(final String... params) {
            String url = Constants.MOVE_RESERVED_URL;
            HashMap<String, String> paramsHash = new HashMap<>();
            paramsHash.put("co_user_id", params[0]);
            paramsHash.put("res_id", params[1]);
            paramsHash.put("con_id", params[2]);
            paramsHash.put("date", params[3]);

            RestClientManager.getInstance().makeJsonRequest(Request.Method.POST,
                    url,
                    new RequestHandler<>(new RequestCallbacks<SuccessResponseModel, ErrorResponseModel>() {
                        @Override
                        public void onRequestSuccess(SuccessResponseModel response) {
                            try {
                                progressSignin.dismiss();
                                String status = response.status;
                                if (status.equalsIgnoreCase("success")) {
                                    PrettyToast.showSuccess(ReservedLockersCordActivity.this, response.message);
                                    new GetReservedLockersTask().execute(userId);
                                }
                                if (response.status.equalsIgnoreCase("failure"))
                                    PrettyToast.showError(ReservedLockersCordActivity.this, response.message);

                            } catch (Exception e) {
                                PrettyToast.showError(ReservedLockersCordActivity.this, "Something went wrong, Please try again");
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onRequestError(ErrorResponseModel error) {
                            progressSignin.dismiss();
                            PrettyToast.showError(ReservedLockersCordActivity.this, "There was a problem connecting to server, Please try again");
                        }
                    }, paramsHash));
            return "";
        }//close doInBackground
    }
}
