package com.seachange.healthandsafty.helper;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;


@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ConnectivityJob extends JobService {

    private static String LOG_TAG = "connectivity";
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private BroadcastReceiver connectivityChange;

    @Override
    public boolean onStartJob(JobParameters job) {
        Logger.info(LOG_TAG + "Job created");
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            connectivityManager.registerNetworkCallback(new NetworkRequest.Builder().build(), networkCallback = new ConnectivityManager.NetworkCallback(){
                // -Snip-
            });
        }else{
            registerReceiver(connectivityChange = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    handleConnectivityChange(!intent.hasExtra("noConnectivity"), intent.getIntExtra("networkType", -1));
                }
            }, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork == null) {
            Logger.info(LOG_TAG + "No active network.");
        }else{
            // Some logic..
            Logger.info(LOG_TAG + "active network.");

        }
        Logger.info(LOG_TAG + "Done with onStartJob");
        return true;
    }


    @Override
    public boolean onStopJob(JobParameters job) {
        if(networkCallback != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)connectivityManager.unregisterNetworkCallback(networkCallback);
        else if(connectivityChange != null)unregisterReceiver(connectivityChange);
        return true;
    }

    private void handleConnectivityChange(NetworkInfo networkInfo){
        // Calls handleConnectivityChange(boolean connected, int type)
    }

    private void handleConnectivityChange(boolean connected, int type){
        // Calls handleConnectivityChange(boolean connected, ConnectionType connectionType)
    }

    private void handleConnectivityChange(boolean connected, ConnectionType connectionType){
        // Logic based on the new connection
    }

    private enum ConnectionType{
        MOBILE,WIFI,VPN,OTHER;
    }

}
