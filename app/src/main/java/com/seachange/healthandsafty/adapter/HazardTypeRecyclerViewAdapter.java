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
import com.seachange.healthandsafty.fragment.HazardTypeFragment.OnTypeFragmentInteractionListener;

import java.util.ArrayList;
import java.util.List;
import com.seachange.healthandsafty.model.HazardType;
import com.seachange.healthandsafty.R;

public class HazardTypeRecyclerViewAdapter extends RecyclerView.Adapter<HazardTypeRecyclerViewAdapter.ViewHolder> {

    private List<HazardType> mValues;
    private final OnTypeFragmentInteractionListener mListener;
    private Context mCtx;
    private String sourceTitle;
    private int screenWidth;

    public HazardTypeRecyclerViewAdapter(List<HazardType> items, Context context, String source, int width, OnTypeFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        mCtx = context;
        sourceTitle = source;
        this.screenWidth = width;
    }

    public void reloadHazards(ArrayList<HazardType> types) {
        mValues = types;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_hazardtype, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mContentView.setText(mValues.get(position).getType_name());
        holder.mhazardTypeView.setText(sourceTitle);

        int padding_in_dp = 24;
        final float scale = mCtx.getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (padding_in_dp * scale + 0.5f);

        int height = screenWidth/2 - padding_in_px;

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(height, height);
        holder.mBoxView.setLayoutParams(layoutParams);

        if(holder.mItem.isSelected()) {
            holder.mBoxView.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.hazardColor));
            holder.mShadow.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.shadow_select));
        } else {
            holder.mBoxView.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.colorDefaultYellow));
            holder.mShadow.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.shadow_yellow));
        }
        final int pos = position;
        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onHazardTypeInteraction(holder.mItem, pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private final TextView mContentView, mhazardTypeView;
        private final RelativeLayout mShadow;
        private HazardType mItem;
        public final RelativeLayout mBoxView;

        private ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = view.findViewById(R.id.type_content);
            mShadow = view.findViewById(R.id.hazard_type_back_shadow);
            mhazardTypeView = view.findViewById(R.id.hazard_type_source_content);
            mBoxView = view.findViewById(R.id.type_box_view);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
