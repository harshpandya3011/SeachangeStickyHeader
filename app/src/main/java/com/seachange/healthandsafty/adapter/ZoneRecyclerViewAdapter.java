package com.seachange.healthandsafty.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.parceler.Parcels;

import com.seachange.healthandsafty.activity.CaygoZoneCheckActivity;
import com.seachange.healthandsafty.activity.QRCodeCaptureActivity;
import com.seachange.healthandsafty.activity.ZoneCheckActivity;
import com.seachange.healthandsafty.application.SCApplication;
import com.seachange.healthandsafty.helper.View.ZoneCheckView;
import com.seachange.healthandsafty.model.CaygoSite;
import com.seachange.healthandsafty.model.DBTourCheckModel;
import com.seachange.healthandsafty.model.DBZoneCheckModel;
import com.seachange.healthandsafty.model.SiteZone;
import com.seachange.healthandsafty.R;
import com.seachange.healthandsafty.nfc.ui.NFCMainActivity;
import com.seachange.healthandsafty.utils.UtilStrings;

import java.util.List;

public class ZoneRecyclerViewAdapter extends RecyclerView.Adapter<ZoneRecyclerViewAdapter.ViewHolder> {

    private List<SiteZone> mZones;
    private Context mCtx;
    private String tourName, mCheckTimeId, siteTourId, mCheckTime;
    private Activity mActivity;
    private boolean tourCheck;
    private boolean fromTour, mLastCheck =false;
    private ZoneCheckView checkView;
    private DBTourCheckModel mCurrentTourCheckModel;
    private CaygoSite caygoSite;

    public ZoneRecyclerViewAdapter(List<SiteZone> items, Context c, String name, String checkTimeId, Activity activity, boolean tCheck, String siteTId, boolean fromT, DBTourCheckModel tModel, boolean lastCheck, CaygoSite siteData) {
        mZones = items;
        mCtx = c;
        tourName = name;
        mCheckTimeId = checkTimeId;
        mActivity = activity;
        tourCheck = tCheck;
        siteTourId = siteTId;
        fromTour = fromT;
        mCurrentTourCheckModel = tModel;
        mLastCheck = lastCheck;
        caygoSite = siteData;
    }


    public void reLoadZoneData(List<SiteZone> items, String checkTimeId, DBTourCheckModel tModel, boolean lastCheck) {
        mZones = items;
        mCheckTimeId = checkTimeId;
        mCurrentTourCheckModel = tModel;
        mLastCheck = lastCheck;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.zone_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mItem = mZones.get(position);
        holder.mContentView.setText(mZones.get(position).getZone_name());
        holder.mCheck.setTypeface(SCApplication.FontMaterial());
        holder.mContinue.setVisibility(View.GONE);
        holder.mPause.setVisibility(View.GONE);
        holder.mHazards.setTextColor(ContextCompat.getColor(mCtx, R.color.alertContent));
        holder.mSync.setVisibility(View.GONE);

        final SiteZone siteZone = holder.mItem;


        DBZoneCheckModel tmp = getDBModel(holder.mItem.getZone_id());
        if (tmp !=null) {
            if (tmp.isComplete()) {
                holder.mItem.setChecked(true);
                holder.mItem.setHazards(tmp.getAddressHazardCommandModels().size()
                +tmp.getAddressHazardCommandModelsV2().size());
            }
        }

        if (holder.mItem.isChecked()) {
            holder.mHazards.setText(Integer.toString(holder.mItem.getHazards()));
            holder.mHazards.setBackgroundResource(R.drawable.circle_grey);
            holder.mCheck.setTextColor(ContextCompat.getColor(mCtx, R.color.colorDefaultGreen));
            holder.mCheck.setText(mCtx.getResources().getString(R.string.fa_check));
            holder.mSync.setVisibility(View.VISIBLE);


            if (tmp !=null && !tmp.isSync()) {
                holder.mSync.setImageResource(R.mipmap.baseline_cloud_off_black_48dp);
            } else {
                holder.mSync.setImageResource(R.mipmap.baseline_cloud_done_black_48dp);
            }

        } else if (holder.mItem.getStatus() == 3) {
            holder.mContinue.setVisibility(View.VISIBLE);
            holder.mPause.setVisibility(View.VISIBLE);
            holder.mHazards.setText(Integer.toString(holder.mItem.getHazards()));
            holder.mHazards.setTextColor(ContextCompat.getColor(mCtx, R.color.circle_grey));
            holder.mHazards.setBackgroundResource(R.drawable.circle_textview_not_select);
            holder.mCheck.setTextColor(ContextCompat.getColor(mCtx, R.color.colorDefaultYellow));
            holder.mCheck.setText(mCtx.getResources().getString(R.string.fa_right_arrow));
        } else {
            holder.mHazards.setBackgroundResource(R.drawable.circle_textview_not_select);
            holder.mHazards.setText("");
            holder.mCheck.setTextColor(ContextCompat.getColor(mCtx, R.color.arrow_grey));
            holder.mCheck.setText(mCtx.getResources().getString(R.string.fa_right_arrow));
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (siteZone.isChecked()) {
                    Intent intent = new Intent(mCtx, ZoneCheckActivity.class);
                    intent.putExtra(UtilStrings.ZONE_CHECK_ID, siteZone.getZoneCheckId());
                    intent.putExtra(UtilStrings.DB_SITE_ZONE, Parcels.wrap(tmp));
                    intent.putExtra(UtilStrings.SITE_ZONE, Parcels.wrap(siteZone));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mCtx.startActivity(intent);
                } else {
                    boolean needScan = PreferenceManager.getDefaultSharedPreferences(mCtx).getBoolean("scan_qrcode_switch", true);
                    if (!needScan) {

                        Intent intent = new Intent(mCtx, CaygoZoneCheckActivity.class);
                        intent.putExtra(UtilStrings.SITE_ZONE, Parcels.wrap(siteZone));
                        intent.putExtra(UtilStrings.TOUR_NAME, tourName);
                        intent.putExtra(UtilStrings.CHECK_TIME_ID, mCheckTimeId);
                        intent.putExtra(UtilStrings.CHECK_TIME, mCheckTime);
                        intent.putExtra(UtilStrings.SITE_TOUR_ID, siteTourId);
                        intent.putExtra(UtilStrings.TOUR_CHECK, tourCheck);
                        intent.putExtra(UtilStrings.TOUR_CHECK_DB, Parcels.wrap(mCurrentTourCheckModel));
                        intent.putExtra(UtilStrings.TOUR_LAST_CHECK, mLastCheck);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mCtx.startActivity(intent);
                    } else {
                        if (siteZone.getStatus() == 3) {
                            Intent intent = new Intent(mCtx, CaygoZoneCheckActivity.class);
                            intent.putExtra(UtilStrings.SITE_ZONE, Parcels.wrap(siteZone));
                            intent.putExtra(UtilStrings.TOUR_NAME, tourName);
                            intent.putExtra(UtilStrings.CHECK_TIME_ID, mCheckTimeId);
                            intent.putExtra(UtilStrings.CHECK_TIME, mCheckTime);
                            intent.putExtra(UtilStrings.SITE_TOUR_ID, siteTourId);
                            intent.putExtra(UtilStrings.TOUR_CHECK, tourCheck);
                            intent.putExtra(UtilStrings.TOUR_CHECK_DB, Parcels.wrap(mCurrentTourCheckModel));
                            intent.putExtra(UtilStrings.TOUR_LAST_CHECK, mLastCheck);

                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mCtx.startActivity(intent);
                            if (!tourCheck) {
                                if (checkView != null) {
                                    checkView.ZoneCheckStart();
                                }
                            }
                        } else {
                            if (caygoSite.siteTypeNFC(mCtx)) {
                                showNFCReader(siteZone);
                            } else {
                                showScanPopUp(siteZone);
                            }
                        }
                    }
                }
            }
        });
    }

    private void showScanPopUp(final SiteZone siteZone) {

        MaterialDialog.Builder builder = new MaterialDialog.Builder(mActivity)
                .title("Scan to start")
                .content("Scan QR Code to Start Zone Check")
                .positiveText("OK")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {

                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        if (!tourCheck) {
                            if (checkView != null) {
                                checkView.ZoneCheckStart();
                            }
                        }

                            Intent intent = new Intent(mCtx, QRCodeCaptureActivity.class);
                            intent.putExtra(UtilStrings.SCAN_LEVEL, 1);
                            intent.putExtra(UtilStrings.SITE_ZONE, Parcels.wrap(siteZone));
                            intent.putExtra(UtilStrings.CHECK_TIME_ID, mCheckTimeId);
                            intent.putExtra(UtilStrings.CHECK_TIME, mCheckTime);
                            intent.putExtra(UtilStrings.TOUR_NAME, tourName);
                            intent.putExtra(UtilStrings.SITE_TOUR_ID, siteTourId);
                            intent.putExtra(UtilStrings.TOUR_CHECK, tourCheck);
                            intent.putExtra(UtilStrings.TOUR_LAST_CHECK, mLastCheck);
                            intent.putExtra(UtilStrings.FROM_TOUR, fromTour);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mCtx.startActivity(intent);

                    }

                });

        MaterialDialog dialog = builder.build();
        dialog.show();
    }

    private void showNFCReader(SiteZone siteZone) {
        Intent intent = new Intent(mCtx, NFCMainActivity.class);
        intent.putExtra(UtilStrings.NFC_TYPE, 3);
        intent.putExtra(UtilStrings.SCAN_LEVEL, 1);
        intent.putExtra(UtilStrings.SITE_ZONE, Parcels.wrap(siteZone));
        intent.putExtra(UtilStrings.CHECK_TIME_ID, mCheckTimeId);
        intent.putExtra(UtilStrings.CHECK_TIME, mCheckTime);
        intent.putExtra(UtilStrings.TOUR_NAME, tourName);
        intent.putExtra(UtilStrings.SITE_TOUR_ID, siteTourId);
        intent.putExtra(UtilStrings.TOUR_CHECK, tourCheck);
        intent.putExtra(UtilStrings.TOUR_LAST_CHECK, mLastCheck);
        intent.putExtra(UtilStrings.FROM_TOUR, fromTour);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mCtx.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return mZones.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        final TextView mCheck, mHazards;
        final TextView mContentView, mPause, mContinue;
        SiteZone mItem;
        final ImageView mSync;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = view.findViewById(R.id.zone_title_content);
            mCheck = view.findViewById(R.id.zone_check_icon);
            mHazards = view.findViewById(R.id.zone_hazards);
            mPause = view.findViewById(R.id.zone_title_sub_content);
            mContinue = view.findViewById(R.id.zone_title_content_continue);
            mSync = view.findViewById(R.id.tour_zone_sync_image_manager);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    private DBZoneCheckModel getDBModel(int mZoneId) {
        if(mCurrentTourCheckModel ==  null) return null;
        for (DBZoneCheckModel tmp : mCurrentTourCheckModel.getZoneChecks()) {
//            DBZoneCheckModel c = tmp;
//            int temp = tmp.getZoneId().intValue();
            if (tmp.getZoneId().intValue() == mZoneId) {
                return tmp;
            }
        }
        return null;
    }
}
