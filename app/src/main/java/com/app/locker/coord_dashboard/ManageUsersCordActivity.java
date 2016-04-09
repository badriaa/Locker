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
import com.app.locker.models.UserInfo;
import com.app.locker.networkmodels.ErrorResponseModel;
import com.app.locker.networkmodels.SuccessResponseModel;
import com.app.locker.networkmodels.UsersResponseModel;

import net.lateralview.simplerestclienthandler.RestClientManager;
import net.lateralview.simplerestclienthandler.base.RequestCallbacks;
import net.lateralview.simplerestclienthandler.base.RequestHandler;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ua.com.crosp.solutions.library.prettytoast.PrettyToast;

public class ManageUsersCordActivity extends AppCompatActivity {

    private ListView listView;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users_cord);
        new GlobalSharedPrefs(this);
        userId = GlobalSharedPrefs.lockerPref.getString("user_id", "");

        listView = (ListView) findViewById(R.id.listViewUsers);
        new GetUsersTask().execute(userId);
    }

    private class GetUsersTask extends AsyncTask<String, Void, String> {
        private SweetAlertDialog progressSignin;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressSignin = new SweetAlertDialog(ManageUsersCordActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            progressSignin.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            progressSignin.setTitleText("Loading, Please wait..");
            progressSignin.setCancelable(false);
            progressSignin.show();
        }//close onPreExecute

        @Override
        protected String doInBackground(final String... params) {
            String url = Constants.GET_USERS_URL + "user_id=" + params[0];
            RestClientManager.getInstance().makeJsonRequest(Request.Method.GET,
                    url,
                    new RequestHandler<>(new RequestCallbacks<UsersResponseModel, ErrorResponseModel>() {
                        @Override
                        public void onRequestSuccess(UsersResponseModel response) {
                            try {
                                progressSignin.dismiss();
                                String status = response.status;
                                if (status.equalsIgnoreCase("success")) {
                                    listView.setAdapter(new UsersAdapter(getApplicationContext(), response.users));
                                }
                                if (response.status.equalsIgnoreCase("failure"))
                                    PrettyToast.showError(ManageUsersCordActivity.this, response.message);

                            } catch (Exception e) {
                                PrettyToast.showError(ManageUsersCordActivity.this, "Something went wrong, Please try again");
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onRequestError(ErrorResponseModel error) {
                            progressSignin.dismiss();
                            PrettyToast.showError(ManageUsersCordActivity.this, "There was a problem connecting to server, Please try again");
                        }
                    }, new HashMap<String, String>()));
            return "";
        }//close doInBackground
    }

    private class UsersAdapter extends BaseAdapter {
        UserInfo[] result;
        Context context;
        private LayoutInflater inflater = null;

        public UsersAdapter(Context mainActivity, UserInfo[] data) {
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
            final UserInfo file = result[position];
            View rowView;

            if (convertView == null) {
                rowView = inflater.inflate(R.layout.users_item, null);
                // Do some initialization
            } else {
                rowView = convertView;
            }

            holder.ivPopupMenu = (ImageView) rowView.findViewById(R.id.ivPopupUserItem);
            holder.tvName = (TextView) rowView.findViewById(R.id.tvNameUserItem);
            holder.tvUsername = (TextView) rowView.findViewById(R.id.tvUsernameUserItem);
            holder.tvEmail = (TextView) rowView.findViewById(R.id.tvEmailUserItem);
            holder.tvPhone = (TextView) rowView.findViewById(R.id.tvPhoneUserItem);

            holder.tvName.setText(file.name + " " + file.last_name);
            holder.tvUsername.setText(file.user_name);
            holder.tvEmail.setText(file.user_email);
            holder.tvPhone.setText(file.phone);
            holder.ivPopupMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PopupMenu popmenu = new PopupMenu(ManageUsersCordActivity.this, holder.ivPopupMenu);
                    popmenu.getMenuInflater().inflate(R.menu.users_popup_menu, popmenu.getMenu());
                    popmenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.delete_user_locker:
                                    new AlertDialog.Builder(ManageUsersCordActivity.this)
                                            .setMessage("are you sure you want to delete this?")
                                            .setCancelable(false)
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    new DeleteUserTask().execute(userId, file.id);
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
            TextView tvName;
            TextView tvUsername;
            TextView tvEmail;
            TextView tvPhone;
        }
    }

    private class DeleteUserTask extends AsyncTask<String, Void, String> {
        private SweetAlertDialog progressSignin;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressSignin = new SweetAlertDialog(ManageUsersCordActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            progressSignin.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            progressSignin.setTitleText("Deleting, Please wait..");
            progressSignin.setCancelable(false);
            progressSignin.show();
        }//close onPreExecute

        @Override
        protected String doInBackground(final String... params) {
            String url = Constants.DELETE_USERS_URL;
            HashMap<String, String> paramsHash = new HashMap<>();
            paramsHash.put("co_user_id", params[0]);
            paramsHash.put("user_id", params[1]);

            RestClientManager.getInstance().makeJsonRequest(Request.Method.POST,
                    url,
                    new RequestHandler<>(new RequestCallbacks<SuccessResponseModel, ErrorResponseModel>() {
                        @Override
                        public void onRequestSuccess(SuccessResponseModel response) {
                            try {
                                progressSignin.dismiss();
                                String status = response.status;
                                if (status.equalsIgnoreCase("success")) {
                                    PrettyToast.showSuccess(ManageUsersCordActivity.this, response.message);
                                    new GetUsersTask().execute(userId);
                                }
                                if (response.status.equalsIgnoreCase("failure"))
                                    PrettyToast.showError(ManageUsersCordActivity.this, response.message);

                            } catch (Exception e) {
                                PrettyToast.showError(ManageUsersCordActivity.this, "Something went wrong, Please try again");
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onRequestError(ErrorResponseModel error) {
                            progressSignin.dismiss();
                            PrettyToast.showError(ManageUsersCordActivity.this, "There was a problem connecting to server, Please try again");
                        }
                    }, paramsHash));
            return "";
        }//close doInBackground
    }

}
