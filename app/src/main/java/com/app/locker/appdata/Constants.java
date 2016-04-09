package com.app.locker.appdata;

/**
 * Created by Mushi on 3/31/2016.
 */
public class Constants {
    public static final String BASE_URL = "http://169.254.73.101:80/locker/webservice/";
//    public static final String BASE_URL = "http://192.168.76.11:80/locker/webservice/";

    public static final String SIGN_UP_URL = BASE_URL + "sign_up?";
    public static final String SIGN_IN_URL = BASE_URL + "sign_in?";
    public static final String FORGOT_PASS_URL = BASE_URL + "forgot_password?";
    public static final String GET_LOCKERS_URL = BASE_URL + "get_all_bookings?";
    public static final String BOOK_LOCKER_URL = BASE_URL + "book_locker?";
    public static final String GET_USERINFO_URL = BASE_URL + "get_user_info?";
    public static final String UPDATE_USERINFO_URL = BASE_URL + "update_user_info?";
    public static final String SEND_PROBLEM_URL = BASE_URL + "send_problem?";
    public static final String GET_PROBLEM_URL = BASE_URL + "get_problems?";
    public static final String DELETE_PROBLEM_URL = BASE_URL + "delete_problem?";
    public static final String GET_NOTIFICATIONS_URL = BASE_URL + "get_all_notifications?";
    public static final String SEND_NOTIFICATIONS_URL = BASE_URL + "send_notification?";
    public static final String GET_NON_RESERVED_URL = BASE_URL + "get_non_reserved_lockers?";
    public static final String DELETE_NON_RESERVED_URL = BASE_URL + "delete_non_reserved_lockers?";
    public static final String GET_RESERVED_URL = BASE_URL + "get_reserved_lockers?";
    public static final String MOVE_RESERVED_URL = BASE_URL + "move_reserved_locker?";
    public static final String GET_LOCKERINFO_URL = BASE_URL + "get_locker_info?";
    public static final String RESERVE_LOCKER_URL = BASE_URL + "reserve_locker?";
    public static final String GET_USERS_URL = BASE_URL + "get_all_users?";
    public static final String DELETE_USERS_URL = BASE_URL + "delete_users?";
    public static final String GET_REPORT_FLOOR_URL = BASE_URL + "search_by_floor?";
    public static final String GET_REPORT_LEVEL_URL = BASE_URL + "search_by_level?";
}
