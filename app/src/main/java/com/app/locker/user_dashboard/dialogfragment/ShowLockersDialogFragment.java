package com.app.locker.user_dashboard.dialogfragment;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.app.locker.R;
import com.app.locker.appdata.Constants;
import com.app.locker.appdata.GlobalSharedPrefs;
import com.app.locker.extras.RecyclerItemClickListener;
import com.app.locker.models.Locker;
import com.app.locker.networkmodels.BookingsResponseModel;
import com.app.locker.networkmodels.ErrorResponseModel;

import net.lateralview.simplerestclienthandler.RestClientManager;
import net.lateralview.simplerestclienthandler.base.RequestCallbacks;
import net.lateralview.simplerestclienthandler.base.RequestHandler;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ua.com.crosp.solutions.library.prettytoast.PrettyToast;

/**
 * Created by Mushi on 4/2/2016.
 */
public class ShowLockersDialogFragment extends DialogFragment {

    private String mBlock, mFloor;
    private HorizontalGridView gvLockersOne, gvLockersTwo, gvLockersThree, gvLockersFour;
    private TextView tvBlock;

    LockerModel[] lockersOne;
    LockerModel[] lockersTwo;
    LockerModel[] lockersThree;
    LockerModel[] lockersFour;
    String base = "S-A-";

    public ShowLockersDialogFragment() {
    }

    public ShowLockersDialogFragment(String block, String floor) {
        mBlock = block;
        mFloor = floor;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_show_lockers, null, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lockersOne = new LockerModel[20];
        lockersTwo = new LockerModel[20];
        lockersThree = new LockerModel[20];
        lockersFour = new LockerModel[20];

        tvBlock = (TextView) view.findViewById(R.id.tvBlockNumLockerDialog);
        gvLockersOne = (HorizontalGridView) view.findViewById(R.id.gridViewShowLockersOne);
        gvLockersTwo = (HorizontalGridView) view.findViewById(R.id.gridViewShowLockersTwo);
        gvLockersThree = (HorizontalGridView) view.findViewById(R.id.gridViewShowLockersThree);
        gvLockersFour = (HorizontalGridView) view.findViewById(R.id.gridViewShowLockersFour);


        if (mFloor.equalsIgnoreCase("Grandfloor")) {
            base = "S-A-";
        }
        if (mFloor.equalsIgnoreCase("Secondfloor")) {
            base = "S-A-";
        }
        if (mFloor.equalsIgnoreCase("Thirdfloor")) {
            base = "S-A-";
        }


        tvBlock.setText(mBlock);


        gvLockersOne.addOnItemTouchListener(
                new RecyclerItemClickListener(getDialog().getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        if (!lockersOne[position].status.equalsIgnoreCase("booked")) {
                            GlobalSharedPrefs.mSelectedLocker = lockersOne[position].locker_number;
                            getDialog().dismiss();
                        }
                    }
                })
        );
        gvLockersTwo.addOnItemTouchListener(
                new RecyclerItemClickListener(getDialog().getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        if (!lockersOne[position].status.equalsIgnoreCase("booked")) {
                            GlobalSharedPrefs.mSelectedLocker = lockersTwo[position].locker_number;
                            getDialog().dismiss();
                        }
                    }
                })
        );
        gvLockersThree.addOnItemTouchListener(
                new RecyclerItemClickListener(getDialog().getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        if (!lockersOne[position].status.equalsIgnoreCase("booked")) {
                            GlobalSharedPrefs.mSelectedLocker = lockersThree[position].locker_number;
                            getDialog().dismiss();
                        }
                    }
                })
        );
        gvLockersFour.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        if (!lockersOne[position].status.equalsIgnoreCase("booked")) {
                            GlobalSharedPrefs.mSelectedLocker = lockersFour[position].locker_number;
                            getDialog().dismiss();
                        }
                    }
                })
        );

        new GetLockerInfoTask().execute(mFloor);
    }

    private class GetLockerInfoTask extends AsyncTask<String, Void, String> {
        private SweetAlertDialog progressSignin;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressSignin = new SweetAlertDialog(getDialog().getContext(), SweetAlertDialog.PROGRESS_TYPE);
            progressSignin.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            progressSignin.setTitleText("Loading Lockers, Please wait..");
            progressSignin.setCancelable(false);
            progressSignin.show();
        }//close onPreExecute

        private boolean checkLockerStatus(String lockNum, Locker[] lockers) {
            for (Locker locker : lockers) {
                if (locker.locker_number.equalsIgnoreCase(lockNum))
                    return true;
            }
            return false;
        }

        @Override
        protected String doInBackground(final String... params) {
            String url = Constants.GET_LOCKERS_URL +
                    "floor=" + params[0];
            RestClientManager.getInstance().makeJsonRequest(Request.Method.GET,
                    url,
                    new RequestHandler<>(new RequestCallbacks<BookingsResponseModel, ErrorResponseModel>() {
                        @Override
                        public void onRequestSuccess(BookingsResponseModel response) {
                            try {
                                progressSignin.dismiss();
                                String status = response.status;
                                if (status.equalsIgnoreCase("success")) {

                                    Locker[] lockersFetched = response.bookings;

                                    for (int i = 0; i < 20; i++) {
                                        LockerModel tmp = new LockerModel();
                                        tmp.locker_number = base + (i + 1);
                                        if (checkLockerStatus(base + (i + 1), lockersFetched))
                                            tmp.status = "booked";
                                        else
                                            tmp.status = "available";
                                        lockersOne[i] = tmp;
                                    }
                                    gvLockersOne.setLayoutManager(new GridLayoutManager(getContext(), 10, GridLayoutManager.HORIZONTAL, false));
                                    gvLockersOne.setAdapter(new CustomAdapter(getContext(), lockersOne));


                                    for (int i = 0; i < 20; i++) {
                                        LockerModel tmp = new LockerModel();
                                        tmp.locker_number = base + (i + 21);
                                        if (checkLockerStatus(base + (i + 21), lockersFetched))
                                            tmp.status = "booked";
                                        else
                                            tmp.status = "available";
                                        lockersTwo[i] = tmp;
                                    }
                                    gvLockersTwo.setLayoutManager(new GridLayoutManager(getContext(), 10, GridLayoutManager.HORIZONTAL, false));
                                    gvLockersTwo.setAdapter(new CustomAdapter(getContext(), lockersTwo));


                                    for (int i = 0; i < 20; i++) {
                                        LockerModel tmp = new LockerModel();
                                        tmp.locker_number = base + (i + 41);
                                        if (checkLockerStatus(base + (i + 41), lockersFetched))
                                            tmp.status = "booked";
                                        else
                                            tmp.status = "available";
                                        lockersThree[i] = tmp;
                                    }
                                    gvLockersThree.setLayoutManager(new GridLayoutManager(getContext(), 10, GridLayoutManager.HORIZONTAL, false));
                                    gvLockersThree.setAdapter(new CustomAdapter(getContext(), lockersThree));


                                    for (int i = 0; i < 20; i++) {
                                        LockerModel tmp = new LockerModel();
                                        tmp.locker_number = base + (i + 61);
                                        if (checkLockerStatus(base + (i + 61), lockersFetched))
                                            tmp.status = "booked";
                                        else
                                            tmp.status = "available";
                                        lockersFour[i] = tmp;
                                    }
                                    gvLockersFour.setLayoutManager(new GridLayoutManager(getContext(), 10, GridLayoutManager.HORIZONTAL, false));
                                    gvLockersFour.setAdapter(new CustomAdapter(getContext(), lockersFour));

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
                            if (getContext() != null) {
                                progressSignin.dismiss();
                                PrettyToast.showError(getContext(), "There was a problem connecting to server, Please try again");
                            }
                        }
                    }, new HashMap<String, String>()));
            return "";
        }//close doInBackground
    }

    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
        private Context context;
        private LockerModel[] lockers;

        public CustomAdapter(Context context, LockerModel[] locker) {
            this.context = context;
            this.lockers = locker;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final TextView tv;
            public final RelativeLayout layout;

            public ViewHolder(View view) {
                super(view);
                tv = (TextView) view.findViewById(R.id.tvLockerNumberItem);
                layout = (RelativeLayout) view.findViewById(R.id.layoutLockerItem);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(this.context).inflate(R.layout.locker_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.tv.setText(lockers[position].locker_number);
            if (lockers[position].status.equalsIgnoreCase("booked"))
                holder.layout.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_locker_booked));
            else
                holder.layout.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_locker_available));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return this.lockers.length;
        }
    }

    private class LockerModel {
        public String locker_number = "";
        public String status = "";

        public LockerModel() {
            locker_number = "";
            String status = "";
        }
    }
}