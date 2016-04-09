package com.app.locker.networkmodels;

import com.app.locker.models.NonReserved;

/**
 * Created by Mushi on 4/5/2016.
 */
public class NonReservedResponseModel {
    public String status;
    public String message;
    public NonReserved[] lockers;
}
