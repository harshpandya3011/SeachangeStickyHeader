package com.seachange.healthandsafty.network;

/**
 * Created by kevinsong on 06/07/16.
 */
import com.android.volley.VolleyError;

public interface VolleyPostRequestListener {
    public void requestStarted();
    public void requestCompleted(String response);
    public void requestEndedWithError(VolleyError error);
}