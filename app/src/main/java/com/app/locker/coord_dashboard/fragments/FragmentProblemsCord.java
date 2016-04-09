package com.app.locker.coord_dashboard.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.app.locker.models.Problem;
import com.app.locker.networkmodels.ErrorResponseModel;
import com.app.locker.networkmodels.ProblemsResponseModel;
import com.app.locker.networkmodels.SuccessResponseModel;

import net.lateralview.simplerestclienthandler.RestClientManager;
import net.lateralview.simplerestclienthandler.base.RequestCallbacks;
import net.lateralview.simplerestclienthandler.base.RequestHandler;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ua.com.crosp.solutions.library.prettytoast.PrettyToast;

/**
 * Created by Mushi on 4/4/2016.
 */
public class FragmentProblemsCord extends Fragment {

    private ListView listView;
    private String userId;

    public FragmentProblemsCord() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_problems_cord, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userId = GlobalSharedPrefs.lockerPref.getString("user_id", "");

        listView = (ListView) view.findViewById(R.id.listViewProblemsCord);
        new GetProblemsTask().execute(userId);
    }

    private class GetProblemsTask extends AsyncTask<String, Void, String> {
        private SweetAlertDialog progressSignin;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressSignin = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
            progressSignin.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            progressSignin.setTitleText("Loading, Please wait..");
            progressSignin.setCancelable(false);
            progressSignin.show();
        }//close onPreExecute

        @Override
        protected String doInBackground(final String... params) {
            String url = Constants.GET_PROBLEM_URL + "user_id=" + params[0];

            RestClientManager.getInstance().makeJsonRequest(Request.Method.GET,
                    url,
                    new RequestHandler<>(new RequestCallbacks<ProblemsResponseModel, ErrorResponseModel>() {
                        @Override
                        public void onRequestSuccess(ProblemsResponseModel response) {
                            try {
                                progressSignin.dismiss();
                                String status = response.status;
                                if (status.equalsIgnoreCase("success")) {
                                    listView.setAdapter(new ProblemsAdapter(getContext(), response.problems));
                                }
                                if (response.status.equalsIgnoreCase("failure"))
                                    PrettyToast.showError(getContext(), response.message);

                            } catch (Exception e) {
                                PrettyToast.showError(getContext(), "Something went wrong, Please try again");
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onRequestError(ErrorResponseModel error) {
                            progressSignin.dismiss();
                            PrettyToast.showError(getContext(), "There was a problem connecting to server, Please try again");
                        }
                    }, new HashMap<String, String>()));
            return "res";
        }//close doInBackground
    }

    private class ProblemsAdapter extends BaseAdapter {
        Problem[] result;
        Context context;
        private LayoutInflater inflater = null;

        public ProblemsAdapter(Context mainActivity, Problem[] data) {
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
            final Holder holder = new Holder();
            final Problem file = result[position];
            View rowView;

            if (convertView == null) {
                rowView = inflater.inflate(R.layout.problem_item, null);
                // Do some initialization
            } else {
                rowView = convertView;
            }

            holder.tvProblem = (TextView) rowView.findViewById(R.id.tvProbItem);
            holder.ivPopup = (ImageView) rowView.findViewById(R.id.ivPopupProblemItem);
            holder.tvProblem.setText(file.description);
            holder.ivPopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PopupMenu popmenu = new PopupMenu(getActivity(), holder.ivPopup);
                    popmenu.getMenuInflater().inflate(R.menu.problem_popup_menu, popmenu.getMenu());
                    popmenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.delete_problem_locker:
                                    new AlertDialog.Builder(getActivity())
                                            .setMessage("are you sure you want to delete this problem?")
                                            .setCancelable(false)
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    new DeleteProblemTask().execute(userId, file.id);
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
            TextView tvProblem;
            ImageView ivPopup;
        }
    }

    private class DeleteProblemTask extends AsyncTask<String, Void, String> {
        private SweetAlertDialog progressSignin;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressSignin = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
            progressSignin.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            progressSignin.setTitleText("Deleting, Please wait..");
            progressSignin.setCancelable(false);
            progressSignin.show();
        }//close onPreExecute

        @Override
        protected String doInBackground(final String... params) {
            String url = Constants.DELETE_PROBLEM_URL;
            HashMap<String, String> paramsHash = new HashMap<>();
            paramsHash.put("user_id", params[0]);
            paramsHash.put("prob_id", params[1]);

            RestClientManager.getInstance().makeJsonRequest(Request.Method.POST,
                    url,
                    new RequestHandler<>(new RequestCallbacks<SuccessResponseModel, ErrorResponseModel>() {
                        @Override
                        public void onRequestSuccess(SuccessResponseModel response) {
                            try {
                                progressSignin.dismiss();
                                String status = response.status;
                                if (status.equalsIgnoreCase("success")) {
                                    PrettyToast.showSuccess(getActivity(), response.message);
                                    new GetProblemsTask().execute(userId);
                                }
                                if (response.status.equalsIgnoreCase("failure"))
                                    PrettyToast.showError(getActivity(), response.message);

                            } catch (Exception e) {
                                PrettyToast.showError(getActivity(), "Something went wrong, Please try again");
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onRequestError(ErrorResponseModel error) {
                            progressSignin.dismiss();
                            PrettyToast.showError(getActivity(), "There was a problem connecting to server, Please try again");
                        }
                    }, paramsHash));
            return "";
        }//close doInBackground
    }
}
