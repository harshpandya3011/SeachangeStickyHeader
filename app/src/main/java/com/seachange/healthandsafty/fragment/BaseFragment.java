package com.seachange.healthandsafty.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.widget.RelativeLayout;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * Created by kevinsong on 13/09/2017.
 */

public class BaseFragment extends Fragment {

    protected Context mCtx;
    protected AVLoadingIndicatorView mIndicator;
    protected RelativeLayout mProgressView;
    protected boolean isLive;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity()!=null) {
            mCtx = getActivity().getApplicationContext();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isLive = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        isLive = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isLive = false;
    }

    protected void initProgressView() {
        mProgressView.setVisibility(View.GONE);
    }

    protected void showLoadingSpinner() {
        mProgressView.setVisibility(View.VISIBLE);
        mIndicator.smoothToShow();
    }

    protected void hideLoadingSpinner() {
        mIndicator.smoothToHide();
        mProgressView.setVisibility(View.GONE);
    }
}
