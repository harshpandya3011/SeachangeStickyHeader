package com.seachange.healthandsafty.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seachange.healthandsafty.fragment.HazardSourceFragment.OnFragmentInteractionListener;
import com.seachange.healthandsafty.model.HazardType;
import com.seachange.healthandsafty.R;

import java.util.ArrayList;


public class HazardSourceRecyclerViewAdapter extends RecyclerView.Adapter<HazardSourceRecyclerViewAdapter.ViewHolder> {

    private final OnFragmentInteractionListener mListener;
    private int selectedPosition =-1;
    private ArrayList<HazardType> mHazardTypes;
    private Context mCtx;

    public HazardSourceRecyclerViewAdapter(Context c, ArrayList<HazardType> types, OnFragmentInteractionListener listener) {
        mCtx = c;
        mListener = listener;
        mHazardTypes = types;
    }

    public void reloadHazards(ArrayList<HazardType> types) {
        mHazardTypes = types;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_hazardsource, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        if(mHazardTypes.get(position).isSelected()) {
            holder.mBackGround.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.hazardColor));
            holder.mShadow.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.shadow_select));
        } else {
            holder.mBackGround.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.colorDefaultYellow));
            holder.mShadow.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.shadow_yellow));
        }

        holder.mHazardType = mHazardTypes.get(position);
        holder.mContentView.setText(holder.mHazardType.getType_name());
        holder.mSourceTv.setText(holder.mHazardType.getCategory());
        final int pos = position;
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onSourceFragmentType(holder.mHazardType, pos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mHazardTypes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private final TextView mContentView, mSourceTv;
        private final RelativeLayout mBackGround, mShadow;
        private HazardType mHazardType;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = view.findViewById(R.id.source_type_content);
            mBackGround = view.findViewById(R.id.hazard_source_type_back);
            mShadow = view.findViewById(R.id.source_type_back_shadow);
            mSourceTv = view.findViewById(R.id.source_type_source_content);
        }
    }
}
