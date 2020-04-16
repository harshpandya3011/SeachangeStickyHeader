package com.seachange.healthandsafty.View;

import com.android.volley.VolleyError;

public interface RequestErrorView {
    void requestSucceed();
    void requestError(VolleyError result);
}
