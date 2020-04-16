package com.seachange.healthandsafty.presenter;

import android.content.Context;

import com.android.volley.VolleyError;
import com.seachange.healthandsafty.View.ScheduleView;
import com.seachange.healthandsafty.helper.PreferenceHelper;
import com.seachange.healthandsafty.model.CaygoSite;
import com.seachange.healthandsafty.model.SiteCheck;
import com.seachange.healthandsafty.network.JsonCallBack;
import com.seachange.healthandsafty.network.JsonParser;
import com.seachange.healthandsafty.network.VolleyJsonHelper;
import com.seachange.healthandsafty.utils.UtilStrings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SchedulePresenter {

    private ScheduleView mView;
    private Context mCtx;
    private JsonParser jsonParser;

    public SchedulePresenter(ScheduleView view, Context c) {
        this.mView = view;
        this.mCtx = c;
        jsonParser = new JsonParser();
    }

    public void getSchedulesByDate(String date) {

        JsonCallBack callBack = new JsonCallBack() {

            @Override
            public void callbackJSONObject(JSONObject result) {
                try {
                    if (result.has(UtilStrings.RESPONSE)) {
                        ArrayList<SiteCheck> array = jsonParser.getSiteTourChecks(result.getJSONObject(UtilStrings.RESPONSE));
                        mView.getSchedules(array);
                    } else {
                        mView.getSchedules(new ArrayList<>());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void callbackJsonArray(JSONArray result) {

            }

            @Override
            public void callbackErrorCalled(VolleyError result) {
                mView.requestError(result);
            }
        };
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
            Date requestDate = sdf.parse(date);
            String tmp = format.format(requestDate);
            CaygoSite caygoSite = PreferenceHelper.getInstance(mCtx).getSiteData();

            String TAG = "schedule";
            new VolleyJsonHelper(UtilStrings.CAYGO_ROOT_API + caygoSite.getSite_id() + "/schedule/" + tmp, TAG, callBack, mCtx).getJsonObjectFromVolleyHelper(PreferenceHelper.getInstance(mCtx).requestToken(), "");

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
