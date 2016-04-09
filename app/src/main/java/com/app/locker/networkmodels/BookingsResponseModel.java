package com.app.locker.networkmodels;

import com.app.locker.models.Locker;

/**
 * Created by Mushi on 4/5/2016.
 */
public class BookingsResponseModel {
    public String status;
    public String message;
    public Locker[] bookings;
}
