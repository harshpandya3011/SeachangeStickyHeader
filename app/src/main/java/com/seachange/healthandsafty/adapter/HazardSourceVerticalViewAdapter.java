package com.seachange.healthandsafty.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import com.seachange.healthandsafty.fragment.HazardSourceFragment.OnFragmentInteractionListener;
import com.seachange.healthandsafty.model.HazardSource;
import com.seachange.healthandsafty.R;

/**
 * Created by kevinsong on 18/09/2017.
 */

public class HazardSourceVerticalViewAdapter extends RecyclerView.Adapter<HazardSourceVerticalViewAdapter.ViewHolder> {

    private final OnFragmentInteractionListener mListener;
    private ArrayList<HazardSource> mHazardSource;
    private Context mCtx;
    private int screenWidth;

    public HazardSourceVerticalViewAdapter(Context c, ArrayList<HazardSource> source, int width, OnFragmentInteractionListener listener) {
        mCtx = c;
        mListener = listener;
        mHazardSource = source;
        screenWidth = width;
    }

    @Override
    public HazardSourceVerticalViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_hazard_source_vertical, parent, false);
        return new HazardSourceVerticalViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final HazardSourceVerticalViewAdapter.ViewHolder holder, int position) {
        holder.mHazardSource = mHazardSource.get(position);
        holder.mContentView.setText(mHazardSource.get(position).getSource_name());

        int padding_in_dp = 24;
        final float scale = mCtx.getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (padding_in_dp * scale + 0.5f);

        int height = screenWidth/2 - padding_in_px;

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(height, height);
        holder.mBoxView.setLayoutParams(layoutParams);


        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onSourceFragmentSource(holder.mHazardSource, holder.frontView, holder.backGround, holder.mContentView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mHazardSource.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public HazardSource mHazardSource;
        private final View backGround, frontView;
        public final RelativeLayout mBoxView;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = view.findViewById(R.id.source_vertical_content);
            frontView = view.findViewById(R.id.source_front_view);
            backGround = view.findViewById(R.id.source_background);
            mBoxView = view.findViewById(R.id.source_box_view);
        }
    }
}
