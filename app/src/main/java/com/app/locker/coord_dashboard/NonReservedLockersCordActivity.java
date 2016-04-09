package com.app.locker.coord_dashboard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
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
import com.app.locker.coord_dashboard.fragments.ConfirmBookingDialogFragment;
import com.app.locker.extras.MyDialogCloseListener;
import com.app.locker.models.NonReserved;
import com.app.locker.networkmodels.ErrorResponseModel;
import com.app.locker.networkmodels.NonReservedResponseModel;
import com.app.locker.networkmodels.SuccessResponseModel;

import net.lateralview.simplerestclienthandler.RestClientManager;
import net.lateralview.simplerestclienthandler.base.RequestCallbacks;
import net.lateralview.simplerestclienthandler.base.RequestHandler;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ua.com.crosp.solutions.library.prettytoast.PrettyToast;

public class NonReservedLockersCordActivity extends AppCompatActivity implements MyDialogCloseListener {

    private ListView listView;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_reserved_lockers_cord);
        new GlobalSharedPrefs(this);

        userId = GlobalSharedPrefs.lockerPref.getString("user_id", "");

        listView = (ListView) findViewById(R.id.listViewNonReserved);
        new GetNonReservedLockersTask().execute(userId);
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        new GetNonReservedLockersTask().execute(userId);
    }

    private class GetNonReservedLockersTask extends AsyncTask<String, Void, String> {
        private SweetAlertDialog progressSignin;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressSignin = new SweetAlertDialog(NonReservedLockersCordActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            progressSignin.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            progressSignin.setTitleText("Loading, Please wait..");
            progressSignin.setCancelable(false);
            progressSignin.show();
        }//close onPreExecute

        @Override
        protected String doInBackground(final String... params) {
            String url = Constants.GET_NON_RESERVED_URL + "user_id=" + params[0];
            RestClientManager.getInstance().makeJsonRequest(Request.Method.GET,
                    url,
                    new RequestHandler<>(new RequestCallbacks<NonReservedResponseModel, ErrorResponseModel>() {
                        @Override
                        public void onRequestSuccess(NonReservedResponseModel response) {
                            try {
                                progressSignin.dismiss();
                                String status = response.status;
                                if (status.equalsIgnoreCase("success")) {
                                    listView.setAdapter(new NonReservedAdapter(getApplicationContext(), response.lockers));
                                }
                                if (response.status.equalsIgnoreCase("failure"))
                                    PrettyToast.showError(NonReservedLockersCordActivity.this, response.message);

                            } catch (Exception e) {
                                PrettyToast.showError(NonReservedLockersCordActivity.this, "Something went wrong, Please try again");
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onRequestError(ErrorResponseModel error) {
                            progressSignin.dismiss();
                            PrettyToast.showError(NonReservedLockersCordActivity.this, "There was a problem connecting to server, Please try again");
                        }
                    }, new HashMap<String, String>()));
            return "";
        }//close doInBackground
    }

    private class NonReservedAdapter extends BaseAdapter {
        NonReserved[] result;
        Context context;
        private LayoutInflater inflater = null;

        public NonReservedAdapter(Context mainActivity, NonReserved[] data) {
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
            final NonReserved file = result[position];
            View rowView;

            if (convertView == null) {
                rowView = inflater.inflate(R.layout.nonreserved_item, null);
                // Do some initialization
            } else {
                rowView = convertView;
            }

            holder.ivPopupMenu = (ImageView) rowView.findViewById(R.id.ivPopupNRItem);
            holder.tvLockerNumber = (TextView) rowView.findViewById(R.id.tvLockerNumberNRItem);
            holder.tvUsername = (TextView) rowView.findViewById(R.id.tvUsernameNRItem);
            holder.tvStudentId = (TextView) rowView.findViewById(R.id.tvStudentIdNRItem);
            holder.tvBookingDate = (TextView) rowView.findViewById(R.id.tvDateNRItem);

            holder.tvLockerNumber.setText(file.locker_number);
            holder.tvUsername.setText(file.user.user_name);
            holder.tvStudentId.setText(file.student_id);
            holder.tvBookingDate.setText(file.date_of_booking);
            holder.ivPopupMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PopupMenu popmenu = new PopupMenu(NonReservedLockersCordActivity.this, holder.ivPopupMenu);
                    popmenu.getMenuInflater().inflate(R.menu.nonreserve_popup_menu, popmenu.getMenu());
                    popmenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.confirm_locker:
                                    FragmentManager manager = getSupportFragmentManager();
                                    ConfirmBookingDialogFragment dialog = new ConfirmBookingDialogFragment(file.id, file.duration);
                                    dialog.show(manager, "dialog");
                                    break;
                                case R.id.delete_locker:
                                    new AlertDialog.Builder(NonReservedLockersCordActivity.this)
                                            .setMessage("are you sure you want to delete this?")
                                            .setCancelable(false)
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    new DeleteBookingTask().execute(userId);
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
        }
    }

    private class DeleteBookingTask extends AsyncTask<String, Void, String> {
        private SweetAlertDialog progressSignin;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressSignin = new SweetAlertDialog(NonReservedLockersCordActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            progressSignin.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            progressSignin.setTitleText("Deleting, Please wait..");
            progressSignin.setCancelable(false);
            progressSignin.show();
        }//close onPreExecute

        @Override
        protected String doInBackground(final String... params) {
            String url = Constants.DELETE_NON_RESERVED_URL;
            HashMap<String, String> paramsHash = new HashMap<>();
            paramsHash.put("user_id", params[0]);

            RestClientManager.getInstance().makeJsonRequest(Request.Method.POST,
                    url,
                    new RequestHandler<>(new RequestCallbacks<SuccessResponseModel, ErrorResponseModel>() {
                        @Override
                        public void onRequestSuccess(SuccessResponseModel response) {
                            try {
                                progressSignin.dismiss();
                                String status = response.status;
                                if (status.equalsIgnoreCase("success")) {
                                    PrettyToast.showSuccess(NonReservedLockersCordActivity.this, response.message);
                                    new GetNonReservedLockersTask().execute(userId);
                                }
                                if (response.status.equalsIgnoreCase("failure"))
                                    PrettyToast.showError(NonReservedLockersCordActivity.this, response.message);

                            } catch (Exception e) {
                                PrettyToast.showError(NonReservedLockersCordActivity.this, "Something went wrong, Please try again");
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onRequestError(ErrorResponseModel error) {
                            progressSignin.dismiss();
                            PrettyToast.showError(NonReservedLockersCordActivity.this, "There was a problem connecting to server, Please try again");
                        }
                    }, paramsHash));
            return "";
        }//close doInBackground
    }
}
