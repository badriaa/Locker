package com.app.locker.user_dashboard;

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
import com.app.locker.models.Notification;
import com.app.locker.networkmodels.ErrorResponseModel;
import com.app.locker.networkmodels.NotificationResponseModel;

import net.lateralview.simplerestclienthandler.RestClientManager;
import net.lateralview.simplerestclienthandler.base.RequestCallbacks;
import net.lateralview.simplerestclienthandler.base.RequestHandler;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ua.com.crosp.solutions.library.prettytoast.PrettyToast;

/**
 * Created by Mushi on 4/3/2016.
 */
public class NotificationUserActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        new GlobalSharedPrefs(this);

        listView = (ListView) findViewById(R.id.listViewNotifications);
        new GetNotificationsInfoTask().execute(GlobalSharedPrefs.lockerPref.getString("user_id", ""));
    }

    private class GetNotificationsInfoTask extends AsyncTask<String, Void, String> {
        private SweetAlertDialog progressSignin;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressSignin = new SweetAlertDialog(NotificationUserActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            progressSignin.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            progressSignin.setTitleText("Loading, Please wait..");
            progressSignin.setCancelable(false);
            progressSignin.show();
        }//close onPreExecute

        @Override
        protected String doInBackground(final String... params) {
            String url = Constants.GET_NOTIFICATIONS_URL + "user_id=" + params[0];
            RestClientManager.getInstance().makeJsonRequest(Request.Method.GET,
                    url,
                    new RequestHandler<>(new RequestCallbacks<NotificationResponseModel, ErrorResponseModel>() {
                        @Override
                        public void onRequestSuccess(NotificationResponseModel response) {
                            try {
                                progressSignin.dismiss();
                                String status = response.status;
                                if (status.equalsIgnoreCase("success")) {
                                    listView.setAdapter(new NotificationsAdapter(getApplicationContext(), response.notifications));
                                }
                                if (response.status.equalsIgnoreCase("failure"))
                                    PrettyToast.showError(NotificationUserActivity.this, response.message);

                            } catch (Exception e) {
                                PrettyToast.showError(NotificationUserActivity.this, "Something went wrong, Please try again");
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onRequestError(ErrorResponseModel error) {
                            progressSignin.dismiss();
                            PrettyToast.showError(NotificationUserActivity.this, "There was a problem connecting to server, Please try again");
                        }
                    }, new HashMap<String, String>()));
            return "";
        }//close doInBackground
    }

    private class NotificationsAdapter extends BaseAdapter {
        Notification[] result;
        Context context;
        private LayoutInflater inflater = null;

        public NotificationsAdapter(Context mainActivity, Notification[] data) {
            // TODO Auto-generated constructor stub
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
            Holder holder = new Holder();
            View rowView;

            if (convertView == null) {
                rowView = (View) inflater.inflate(R.layout.notification_item, null);
                // Do some initialization
            } else {
                rowView = convertView;
            }

            holder.tv = (TextView) rowView.findViewById(R.id.tvNotifItem);
            holder.iv = (ImageView) rowView.findViewById(R.id.ivPriorityNotifItem);
            holder.tv.setText(result[position].description);
            String pt = result[position].priorty;
            if (pt.equalsIgnoreCase("low"))
                holder.iv.setImageResource(R.drawable.ic_notif_low);
            if (pt.equalsIgnoreCase("medium"))
                holder.iv.setImageResource(R.drawable.ic_notif_med);
            if (pt.equalsIgnoreCase("high"))
                holder.iv.setImageResource(R.drawable.ic_notif_high);

            return rowView;
        }

        public class Holder {
            TextView tv;
            ImageView iv;
        }
    }
}
