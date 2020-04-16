package com.seachange.healthandsafty.utils;

import android.preference.PreferenceManager;

import com.seachange.healthandsafty.BuildConfig;
import com.seachange.healthandsafty.application.SCApplication;

/**
 * Created by kevinsong on 12/09/2017.
 */

public class UtilStrings {

    //api
    private static final String ROOT_SERVER = getRoot();

    public static final String SOURCE_API = ROOT_SERVER + "/api/hazardlibrary";
    public static final String USER_API = ROOT_SERVER + "/api//users/user";
    public static final String SITE_CAYGO = ROOT_SERVER + "/api/caygo/";

    public static final String USERS_TENANTS_API = ROOT_SERVER + "/api/users/tenants/";
    public static final String USERS_API = ROOT_SERVER + "/api/users";
    public static final String LOGIN_API = ROOT_SERVER + "/token";
    public static final String CAYGO_ROOT_API = ROOT_SERVER + "/api/caygo/";
    public static final String CHECK_EMAIL_API = ROOT_SERVER + "/api/users/doesemailexist";
    public static final String GET_USER_API = ROOT_SERVER + "/api/users/";
    public static final String RESET_PASSWORD_USER = ROOT_SERVER + "/api/users/passwordresetrequest/";
    public static final String RESET_PASSWORD = ROOT_SERVER + "/api/users/password";
    public static final String SITE_JSA_API = ROOT_SERVER + "/api/jsa/958";
    public static final String RA_RISK_API = ROOT_SERVER + "/api/risk-library/risks";
    public static final String RA_RISK_CATEGORIES_API = ROOT_SERVER + "/api/risk-library/risk-categories/";
    public static final String RA_RISK_HAZARDS_API = ROOT_SERVER + "/api/risk-assessments/d5042fa9-7011-44c6-9bad-02e469c7ff9c/hazards";
    public static final String RA_RISK_ASSESSMENT = ROOT_SERVER + "/api/risk-assessors/1/risk-assessments";
    public static final String RA_POST_HAZARD = ROOT_SERVER + "/api/sites/";

     //preferences
    public static final String PREFERENCES_NAME = "seachange_caygo";
    public static final String PREFERENCES_CURRENT_HAZARDS = "current_hazards";
    public static final String PREFERENCES_SOURCE_AND_TYPE = "source_data";
    public static final String PREFERENCES_SOURCE_ETAG = "source_etag";
    public static final String PREFERENCES_FREQUENT_ETAG = "frequent_etag";
    public static final String PREFERENCES_SITE_ETAG = "site_etag";
    public static final String PREFERENCES_SCHEDULE_ETAG = "schedule_etag";
    public static final String PREFERENCES_STATS_ETAG = "site_etag";
    public static final String PREFERENCES_JSA_ETAG = "jsa_etag";
    public static final String PREFERENCES_USER_LOGGEDIN = "user_logged_in";
    public static final String PREFERENCES_USER_PASSCODE = "user_passcode";
    public static final String PREFERENCES_USER_HASHEDCODE = "user_hashcode";
    public static final String PREFERENCES_USER_OPTION = "user_setting_option";
    public static final String PREFERENCES_SCHEDULE_STATUS = "schedule_status";
    public static final String PREFERENCES_NFC_ZONES = "nfc_zone_list";
    public static final String PREFERENCES_NFC_STATUS_CHANGED = "nfc_status_change";
    public static final String PREFERENCES_NFC_ZONES_DATA = "nfc_zone_list_data";
    public static final String PREFERENCES_NFC_SYNC_ZONES_DATA = "nfc_zone_sync_list_data";
    public static final String PREFERENCES_USER_NAME = "user_name";
    public static final String PREFERENCES_SCHEDULES = "seachange_schedules";


    public static final String PREFERENCES_FREQUENT_HAZARDS = "source_frequent_data";
    public static final String PREFERENCES_SITE_ZONE = "site_zones";
    public static final String PREFERENCES_CAYGO_SITE = "site_data";
    public static final String PREFERENCES_SITE_SCHEDULE = "site_schedule";
    public static final String PREFERENCES_STATS = "stats_data";
    public static final String PREFERENCES_JSA = "jsa_data";

    public static final String PREFERENCES_Token = "token";
    public static final String PREFERENCES_ZONE_CHECK_UUID = "zone_check_uuid";
    public static final String PREFERENCES_TOUR_CHECK_UUID = "tour_check_uuid";
    public static final String PREFERENCES_CAYGO_USER = "caygo_user";
    public static final String PREFERENCES_CAYGO_CHECK_TIME_SPEND = "caygo_time_spend";
    public static final String PREFERENCES_QRCODE = "saved_qrcode";
    public static final String PREFERENCES_REFRESH_TOKEN = "refresh_token";

    //DB
    public static final String PREFERENCES_CURRENT_DB_CHECK = "current_db_check";

    public static final String PREFERENCES_TOUR_CURRENT_DB_CHECK = "current_tour_db_check";


    //strings
    public static final String OBJECT_HAZARD_SOURCE = "hazard_source";
    public static final String OBJECT_USER = "user_object";
    public static final String ETag = "etag";
    public static final String STATUS_CODE = "statusCode";
    public static final String RESPONSE = "response";
    public static final String ZONE_ID = "zone_id";
    public static final String ZONE_NAME = "zone_name";
    public static final String ZONES = "zones";
    public static final String SITE_ZONE = "current_zone";
    public static final String MANAGER_HOME = "manager_home";
    public static final String TOUR_NAME = "tour_name";
    public static final String CHECK_TIME_ID = "check_time_id";
    public static final String CHECK_TIME = "check_time";
    public static final String SCHEDULED_CHECK_TIME = "scheduled_check_time";
    public static final String DB_SITE_ZONE = "db_current_zone";

    public static final String SITE_TOUR_ID = "site_tour_id";
    public static final String TOUR_CHECK = "site_tour_check";
    public static final String TOUR_LAST_CHECK = "site_tour_last_check";
    public static final String FROM_TOUR = "from_tour";
    public static final String ZONE_CHECK_ID = "zone_check_id";
    public static final String USER_EDIT =  "user_edit";
    public static final String NEW_PASSCODE = "new_passcode";
    public static final String AUTH_BASIC= "jQ#L7$F53A$R@W#5";
    public static final String AUTH_CLIENT= "seachangeAndroid";
    public static final String TOUR_CHECK_DB = "site_tour_check_db";

    public static final String SITE_SCHEDULED_TOUR_ID = "scheduled_site_tour_id";
    public static final String SITE_TOUR_HAZARDS = "site_tour_hazards";
    public static final String MANAGER_TOUR = "tour_name";
    public static final String QRCODE = "qrCode";
    public static final String NFC_TYPE = "nfc_type";
    public static final String NFC_POINT = "nfc_point";



    //log message util
    public static final String LOG_ZONE_CHECK= "CAYGO_ZONE_check";
    public static final String LOG_ZONE_CHECK_TIME= "CAO: ";
    public static final String LOG_ZONE_CHECK_START= "CAO-START: ";

    public static final String LOG_TOUR_CHECK= "CAYGO_TOUR";
    public static final String LOG_RISK_ASSESSMENT = "Risk_Assessment";
    public static final String LOG_TOUR_DB_CHECK= "CAYGO_TOUR_DB";


    public static final String RISK_CATEGORY = "RiskCategory";
    public static final String SCAN_LEVEL = "scan_level";
    public static final String USER_EMAIL = "user_email";
    public static final String FROM_SPLASH = "from_splash";
    public static final String FROM_LOGIN = "from_login";
    public static final String SPLASH_WITH_DATA = "splash_with_data";
    public static final String SPLASH_STATUS_CODE = "splash_status_code";
    public static final String RA_FLOW = "ra_flow";
    public static final String RA_IMAGES = "ra_images";
    public static final String TOUR_QR_CODE = "tour_qr_fail";
    public static final String HOME_REFRESH = "home_refresh";

    public static final String NFC_SETUP_FAILED = "nfc_setup_fail";

    //offine
    public static final String OFFLINE_SYNC_TIME = "sync_time";

    private static String getRoot() {
        if (BuildConfig.DEBUG) {
            return "http://seachangedev.azurewebsites.net";
        } else {
            return "https://seachangeproduction.azurewebsites.net";
        }
    }
}
