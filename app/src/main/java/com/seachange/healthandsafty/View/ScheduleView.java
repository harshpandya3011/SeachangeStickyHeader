package com.seachange.healthandsafty.View;

import com.android.volley.VolleyError;
import com.seachange.healthandsafty.model.SiteCheck;

import java.util.ArrayList;

public interface ScheduleView {
    void getSchedules(ArrayList<SiteCheck> arrayList);
    void requestError(VolleyError result);
}
