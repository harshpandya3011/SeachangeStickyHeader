package com.seachange.healthandsafty.adapter;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.CountDownTimer;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.seachange.healthandsafty.application.SCApplication;
import com.seachange.healthandsafty.helper.HazardObserver;
import com.seachange.healthandsafty.helper.HomeData;
import com.seachange.healthandsafty.helper.Logger;
import com.seachange.healthandsafty.helper.View.AnimatedExpandableListView.AnimatedExpandableListAdapter;
import com.seachange.healthandsafty.model.CheckTime;
import com.seachange.healthandsafty.model.DBZoneCheckModel;
import com.seachange.healthandsafty.model.SiteZone;
import com.seachange.healthandsafty.R;

/**
 * Created by kevinsong on 23/10/2017.
 */

public class HomeExpandListViewAdapter extends AnimatedExpandableListAdapter {

    private List<String> expandableListTitle;
    private HashMap<String, List<SiteZone>> expandableListDetail;
    private ArrayList<CheckTime> mChecks;
    private Context mCtx;
    private String nextCheckTime;
    private int mCurrentDay;
    private HomeData homeData;
    private SimpleDateFormat sdf;
    private ArrayList<View> mViewHolders;
    private ArrayList<DBZoneCheckModel> mDBList;

    public HomeExpandListViewAdapter(Context context, List<String> expandableListTitle,
                                     HashMap<String, List<SiteZone>> expandableListDetail, ArrayList<CheckTime> checks,
                                     String nextTime, HomeData mData, int day) {
        this.mCtx = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
        this.mChecks = checks;
        this.nextCheckTime = nextTime;
        this.homeData = mData;
        this.mCurrentDay = day;
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        mViewHolders = new ArrayList<>();
        mDBList = new ArrayList<>();
    }

    public HomeExpandListViewAdapter(Context context, List<String> expandableListTitle,
                                     HashMap<String, List<SiteZone>> expandableListDetail, ArrayList<CheckTime> checks,
                                     String nextTime, HomeData mData, int day, ArrayList<DBZoneCheckModel> mList) {
        this.mCtx = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
        this.mChecks = checks;
        this.nextCheckTime = nextTime;
        this.homeData = mData;
        this.mCurrentDay = day;
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        mViewHolders = new ArrayList<>();
        mDBList = mList;
    }

    public void refresh(Context context, List<String> expandableListTitle,
                        HashMap<String, List<SiteZone>> expandableListDetail, ArrayList<CheckTime> checks,
                        String nextTime, HomeData mData, int day) {

        this.mCtx = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
        this.mChecks = checks;
        this.nextCheckTime = nextTime;
        this.homeData = mData;
        this.mCurrentDay = day;
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    }

    public void reloadDBChecks(ArrayList<DBZoneCheckModel> list) {
        mDBList = list;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getRealChildView(int listPosition, final int expandedListPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

        SiteZone siteZone = (SiteZone) getChild(listPosition, expandedListPosition);
        ChildViewHolder viewHolder = new ChildViewHolder();

        final CheckTime checkTime = mChecks.get(listPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mCtx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            convertView = layoutInflater.inflate(R.layout.list_item, parent, false);

            viewHolder.mTitle = convertView.findViewById(R.id.schedule_zone_child_title_content);
            viewHolder.mCheck = convertView.findViewById(R.id.schedule_zone_child_check_icon);
            viewHolder.mHazards = convertView.findViewById(R.id.schedule_zone_child_hazards);
            viewHolder.middleRel = convertView.findViewById(R.id.schedule_zone_middle_rel);
            viewHolder.bottomRel = convertView.findViewById(R.id.schedule_zone_bottom_rel);
            viewHolder.mPause = convertView.findViewById(R.id.schedule_zone_title_sub_content);
            viewHolder.mContinue = convertView.findViewById(R.id.schedule_zone_title_content_continue);
            viewHolder.mSync = convertView.findViewById(R.id.home_sync_image);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ChildViewHolder) convertView.getTag();
        }

        if (isLastChild) {
            viewHolder.bottomRel.setVisibility(View.VISIBLE);
            viewHolder.middleRel.setVisibility(View.GONE);
        } else {
            viewHolder.bottomRel.setVisibility(View.GONE);
            viewHolder.middleRel.setVisibility(View.VISIBLE);
        }

        viewHolder.mCheck.setTypeface(SCApplication.FontMaterial());
        viewHolder.mTitle.setText(siteZone.getZone_name());

        //hide continue and pause
        viewHolder.mContinue.setVisibility(View.GONE);
        viewHolder.mPause.setVisibility(View.GONE);

        final DBZoneCheckModel tmp = checkIfZonecheckInDB(checkTime, Integer.toString(siteZone.getZone_id()));
        viewHolder.mHazards.setTextColor(ContextCompat.getColor(mCtx, R.color.alertContent));
        if (tmp != null) {

            if (tmp.isComplete()) {
                viewHolder.mSync.setVisibility(View.VISIBLE);
                siteZone.setChecked(true);
                int hazardsCount = 0;
                if (tmp.getAddressHazardCommandModelsV2() != null) {
                    hazardsCount += tmp.getAddressHazardCommandModelsV2().size();
                }
                if (tmp.getAddressHazardCommandModels() != null) {
                    hazardsCount += tmp.getAddressHazardCommandModels().size();
                }
                siteZone.setHazards(hazardsCount);
            }
        } else {
            viewHolder.mSync.setVisibility(View.GONE);
        }

        viewHolder.mSync.setVisibility(View.GONE);

        if (siteZone.isChecked()) {
            viewHolder.mHazards.setText(Integer.toString(siteZone.getHazards()));
            viewHolder.mHazards.setBackgroundResource(R.drawable.circle_grey);
            viewHolder.mCheck.setTextColor(ContextCompat.getColor(mCtx, R.color.colorDefaultGreen));
            viewHolder.mCheck.setText(mCtx.getResources().getString(R.string.fa_check));

            //test
            viewHolder.mSync.setVisibility(View.VISIBLE);

            if (tmp != null && !tmp.isSync()) {
                viewHolder.mSync.setImageResource(R.mipmap.baseline_cloud_off_black_48dp);
            } else {
                viewHolder.mSync.setImageResource(R.mipmap.baseline_cloud_done_black_48dp);
            }
        }
        //updated version
        else if (checkTime.getStatus() == 2) {
            if (siteZone.getStatus() == 3) {
                viewHolder.mContinue.setVisibility(View.VISIBLE);
                viewHolder.mPause.setVisibility(View.VISIBLE);
                viewHolder.mHazards.setText(Integer.toString(siteZone.getHazards()));
                viewHolder.mHazards.setTextColor(ContextCompat.getColor(mCtx, R.color.circle_grey));
                viewHolder.mHazards.setBackgroundResource(R.drawable.circle_textview_not_select);
                viewHolder.mCheck.setTextColor(ContextCompat.getColor(mCtx, R.color.colorDefaultYellow));
                viewHolder.mCheck.setText(mCtx.getResources().getString(R.string.fa_right_arrow));
            } else {
                viewHolder.mHazards.setBackgroundResource(R.drawable.circle_textview_not_select);
                viewHolder.mHazards.setText("");
                viewHolder.mCheck.setTextColor(ContextCompat.getColor(mCtx, R.color.arrow_grey));
                viewHolder.mCheck.setText(mCtx.getResources().getString(R.string.fa_right_arrow));
            }
        } else {
            viewHolder.mHazards.setBackgroundResource(R.drawable.circle_textview_not_select);
            viewHolder.mHazards.setText("");
            viewHolder.mCheck.setTextColor(ContextCompat.getColor(mCtx, R.color.arrow_grey));
            viewHolder.mCheck.setText("-");
        }

        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return expandableListDetail.get(this.expandableListTitle.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @SuppressLint("NewApi")
    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        GroupViewHolder holder = new GroupViewHolder();

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mCtx.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            convertView = layoutInflater.inflate(R.layout.list_group, parent, false);

            holder.managerTour = convertView.findViewById(R.id.schedule_home_tour_view);
            holder.previousRel = convertView.findViewById(R.id.schedule_previous_group_rel);
            holder.mMangerSelectIcon = convertView.findViewById(R.id.schedule_manager_check_icon);
            holder.mTitleExpandIcon = convertView.findViewById(R.id.schedule_zone_check_icon);
            holder.mTourHazards = convertView.findViewById(R.id.schedule_manager_hazards);
            holder.mTourDate = convertView.findViewById(R.id.schedule_zone_title_content);
            holder.mTourDateEnd = convertView.findViewById(R.id.schedule_zone_title_content_sub);
            holder.mTitleView = convertView.findViewById(R.id.schedule_zone_title_content_view);
            holder.mManagerTourDate = convertView.findViewById(R.id.schedule_tour_home_tour_date);
            holder.mTourDateCurrent = convertView.findViewById(R.id.schedule_zone_title_content_current);
            holder.mTourDateCurrentEnd = convertView.findViewById(R.id.schedule_zone_title_content_current_sub);
            holder.mCurrentTitleView = convertView.findViewById(R.id.schedule_zone_title_content_current_view);
            holder.mComplianceProgress = convertView.findViewById(R.id.schedule_current_compliance_progressbar);
            holder.mCompliance = convertView.findViewById(R.id.schedule_current_check_compliance);
            holder.mSubTitle = convertView.findViewById(R.id.schedule_current_sub_title);
            holder.currentTopRel = convertView.findViewById(R.id.schedule_current_progress_rel);
            holder.mProgressView = convertView.findViewById(R.id.schedule_current_tour_hazard_count);
            holder.mProgressBar = convertView.findViewById(R.id.schedule_current_progressBar);
            holder.mCheckHazards = convertView.findViewById(R.id.schedule_zone_hazards);
            holder.expandView = convertView.findViewById(R.id.expand_group_rel);
            holder.managerExpandView = convertView.findViewById(R.id.manager_expand_group_rel);
            holder.mDivider = convertView.findViewById(R.id.expand_group_divider);
            holder.mTimer = convertView.findViewById(R.id.schedule_current__chronometer);

            convertView.setTag(holder);
            mViewHolders.add(convertView);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }

        if (holder.countDownTimer != null) {
            holder.countDownTimer.cancel();
            holder.countDownTimer = null;
        }
        holder.mMangerSelectIcon.setTextColor(ContextCompat.getColor(mCtx, R.color.arrow_grey));
        holder.mMangerSelectIcon.setText(mCtx.getResources().getString(R.string.fa_right_arrow));
        holder.mMangerSelectIcon.setTypeface(SCApplication.FontMaterial());
        holder.mTitleExpandIcon.setTextColor(ContextCompat.getColor(mCtx, R.color.arrow_grey));
        holder.mTitleExpandIcon.setTypeface(SCApplication.FontMaterial());
        holder.mTitleExpandIcon.setVisibility(View.GONE);
        holder.managerTour.setVisibility(View.GONE);

        //need to be tested
        final CheckTime checkTime = mChecks.get(listPosition);
        checkTime.processCheckTime();

        holder.mTourDate.setText(checkTime.getCheckTime());
        holder.mTourDateCurrent.setText(checkTime.getCheckTime());

        holder.mTourDate.setText(checkTime.getCheckStartTime());
        holder.mTourDateEnd.setText(String.format(" - %s", checkTime.getCheckEndTime()));

        holder.mTourDateCurrent.setText(checkTime.getCheckStartTime());
        holder.mTourDateCurrentEnd.setText(String.format(" - %s", checkTime.getCheckEndTime()));

        if (checkTime.getNumOfHazardsIdentified() != null) {
            int hazards = checkTime.getNumOfHazardsIdentified();
            holder.mCheckHazards.setText(Integer.toString(hazards));
        } else {
            holder.mCheckHazards.setText("");
        }

        try {
            if (checkTime.getStatus() == 2) {
                holder.mComplianceProgress.setProgress(100);
                holder.mCompliance.setText("");
            } else if (checkTime.getStatus() < 2) {
                int per = 0;
                if (checkTime.getPercentage() != null) {
                    per = checkTime.getPercentage();
                }
                holder.mComplianceProgress.setProgress(per);
                holder.mCompliance.setText(String.format("%s%%", Integer.toString(per)));
                Logger.info("previous check times" + checkTime.getPercentage());
            } else {
                holder.mComplianceProgress.setProgress(0);
                holder.mCompliance.setText("0%");
            }

            //there is no current check, could be from the day before of after.
            if (mCurrentDay == 1) {
                int per = 0;
                if (checkTime.getPercentage() != null) {
                    per = checkTime.getPercentage();
                }
                holder.mComplianceProgress.setProgress(per);
                holder.mCompliance.setText(String.format("%s%%", Integer.toString(per)));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (checkTime.getStatus() == 2) {
            holder.mCompliance.setVisibility(View.GONE);
            holder.mComplianceProgress.setVisibility(View.GONE);
            holder.mTitleExpandIcon.setVisibility(View.VISIBLE);
            holder.mCheckHazards.setBackgroundResource(R.drawable.circle_grey);
        } else if (checkTime.getStatus() < 2 || homeData.afterLastCheck || mCurrentDay == 1) {
            holder.mCompliance.setVisibility(View.VISIBLE);
            holder.mComplianceProgress.setVisibility(View.VISIBLE);
            holder.mTitleExpandIcon.setVisibility(View.VISIBLE);

            int per = 0;
            if (checkTime.getPercentage() != null) {
                per = checkTime.getPercentage();
            }

            if (per == 0) {
                holder.mCheckHazards.setBackgroundResource(R.drawable.circle_textview_not_select);
            } else {
                holder.mCheckHazards.setBackgroundResource(R.drawable.circle_grey);
            }

        } else {
            holder.mCompliance.setVisibility(View.GONE);
            holder.mComplianceProgress.setVisibility(View.GONE);
            holder.mTitleExpandIcon.setVisibility(View.GONE);
            holder.expandView.setVisibility(View.GONE);
            holder.mCheckHazards.setText("");
            holder.mCheckHazards.setBackgroundResource(R.drawable.circle_textview_not_select);
        }

        //updated version
        if (checkTime.getStatus() == 2) {
            holder.mCompliance.setVisibility(View.GONE);
            holder.mComplianceProgress.setVisibility(View.GONE);
            holder.mTitleExpandIcon.setVisibility(View.VISIBLE);
            holder.mCheckHazards.setBackgroundResource(R.drawable.circle_grey);
        } else if (checkTime.getStatus() == 1 || homeData.afterLastCheck || mCurrentDay == 1) {
            holder.mCompliance.setVisibility(View.VISIBLE);
            holder.mComplianceProgress.setVisibility(View.VISIBLE);
            holder.mTitleExpandIcon.setVisibility(View.VISIBLE);

            int per = 0;
            if (checkTime.getPercentage() != null) {
                per = checkTime.getPercentage();
            }

            if (per == 0) {
                holder.mCheckHazards.setBackgroundResource(R.drawable.circle_textview_not_select);
            } else {
                holder.mCheckHazards.setBackgroundResource(R.drawable.circle_grey);
            }
        } else {
            holder.mCompliance.setVisibility(View.GONE);
            holder.mComplianceProgress.setVisibility(View.GONE);
            holder.mTitleExpandIcon.setVisibility(View.GONE);
            holder.expandView.setVisibility(View.GONE);
            holder.mCheckHazards.setText("");
            holder.mCheckHazards.setBackgroundResource(R.drawable.circle_textview_not_select);
        }

        //divider and check time positions
        if (checkTime.getStatus() == 2) {
            holder.mDivider.setVisibility(View.GONE);
            holder.mCurrentTitleView.setVisibility(View.VISIBLE);
            holder.mTitleView.setVisibility(View.GONE);
        } else {
            holder.mDivider.setVisibility(View.VISIBLE);
            holder.mCurrentTitleView.setVisibility(View.GONE);
            holder.mTitleView.setVisibility(View.VISIBLE);
        }

        //current check expand state
        if (isExpanded && checkTime.getStatus() <= 2) {
            holder.mTitleExpandIcon.setText(mCtx.getResources().getString(R.string.fa_chevron_up));
            holder.expandView.setVisibility(View.VISIBLE);
            //current check view
            if (checkTime.getStatus() == 2) {
                holder.mSubTitle.setVisibility(View.VISIBLE);
                holder.currentTopRel.setVisibility(View.VISIBLE);
                holder.mCheckHazards.setText(Integer.toString(checkTime.getCurrentHazards()));

                String progress = checkTime.getCurrentCheckedZones() + "/" + checkTime.getScheduledCheckses().size() + " Zones checked";
                holder.mProgressView.setText(progress);
                holder.mProgressBar.setMax(checkTime.getScheduledCheckses().size() * 100);

                ObjectAnimator animation = ObjectAnimator.ofInt(holder.mProgressBar, "progress", checkTime.getCurrentCheckedZones() * 100);
                animation.setDuration(500);
                animation.setInterpolator(new DecelerateInterpolator());
                animation.start();

                try {
                    Date mDate = sdf.parse(checkTime.getEnd_time());
                    Calendar calendar = Calendar.getInstance();
                    Date currentDate = calendar.getTime();

                    long diffInMs = mDate.getTime() - currentDate.getTime();
                    long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);

                    int seconds = (int) diffInSec;
                    int hours = seconds / (60 * 60);
                    int tempMint = (seconds - (hours * 60 * 60));
                    int minutes = tempMint / 60;
                    seconds = tempMint - (minutes * 60);

                    if (hours > 0) {
                        holder.mTimer.setText(String.format("%s:%s:%s", String.format("%02d", hours), String.format("%02d", minutes), String.format("%02d", seconds)));
                    } else {
                        holder.mTimer.setText(String.format("%s:%s", String.format("%02d", minutes), String.format("%02d", seconds)));
                    }

                    final GroupViewHolder holderTmp = holder;
                    if (holder.countDownTimer != null) {
                        holder.countDownTimer.cancel();
                    }

                    holder.countDownTimer = new CountDownTimer(diffInMs, 1000) { //Sets 10 second remaining

                        public void onTick(long millisUntilFinished) {
                            int seconds = (int) (millisUntilFinished / 1000);
                            int hours = seconds / (60 * 60);
                            int tempMint = (seconds - (hours * 60 * 60));
                            int minutes = tempMint / 60;
                            seconds = tempMint - (minutes * 60);
                            if (hours > 0) {
                                holderTmp.mTimer.setText(String.format("%s:%s:%s", String.format("%02d", hours), String.format("%02d", minutes), String.format("%02d", seconds)));
                            } else {
                                holderTmp.mTimer.setText(String.format("%s:%s", String.format("%02d", minutes), String.format("%02d", seconds)));
                            }
                        }

                        public void onFinish() {
                            holderTmp.mTimer.setText("00:00");
                            HazardObserver.getInstance().setScheduledReceived(true);
                            Logger.info("timer reached end");
                        }
                    }.start();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                holder.mSubTitle.setVisibility(View.GONE);
                holder.currentTopRel.setVisibility(View.GONE);
            }

        } else {
            if (isExpanded) {

                holder.mTitleExpandIcon.setText(mCtx.getResources().getString(R.string.fa_chevron_up));
                if (getRealChildrenCount(listPosition) > 0)
                    holder.expandView.setVisibility(View.VISIBLE);
            } else {
                holder.mTitleExpandIcon.setText(mCtx.getResources().getString(R.string.fa_chevron_down));
                holder.expandView.setVisibility(View.GONE);
            }
            holder.currentTopRel.setVisibility(View.GONE);

            if (checkTime.getStatus() == 2) {
                holder.mSubTitle.setVisibility(View.VISIBLE);
                holder.mCheckHazards.setText(Integer.toString(checkTime.getCurrentHazards()));
            } else {
                holder.mSubTitle.setVisibility(View.GONE);
            }
        }
        holder.mTitleExpandIcon.setVisibility(View.GONE);
        return convertView;
    }

    private static class GroupViewHolder {
        private RelativeLayout managerTour, previousRel;
        private TextView mMangerSelectIcon, mTitleExpandIcon, mTourHazards, mTourDate, mTourDateEnd, mCompliance, mSubTitle, mProgressView, mCheckHazards, mTourDateCurrent, mTourDateCurrentEnd, mTimer, mManagerTourDate;
        private RelativeLayout expandView, managerExpandView, mTitleView, mCurrentTitleView;
        private ProgressBar mComplianceProgress, mProgressBar;
        private LinearLayout currentTopRel;
        private View mDivider;
        private CountDownTimer countDownTimer;
    }

    private DBZoneCheckModel checkIfZonecheckInDB(CheckTime checkTime, String zoneId) {
        for (int i = 0; i < mDBList.size(); i++) {
            if (mDBList.get(i).isCurrentZoneCheck(zoneId, checkTime.getCheck_id())) {
                return mDBList.get(i);
            }
        }
        return null;
    }

    private static class ChildViewHolder {
        private RelativeLayout middleRel, bottomRel;
        private TextView mTitle, mCheck, mTourHazards, mHazards, mContinue, mPause;
        private ImageView mSync;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }

    public void stopTimer() {
        if (mViewHolders != null) {
            for (int i = 0; i < mViewHolders.size(); i++) {
                GroupViewHolder vh = (GroupViewHolder) mViewHolders.get(i).getTag();
                if (vh != null) {
                    if (vh.countDownTimer != null) {
                        vh.countDownTimer.cancel();
                        vh.countDownTimer = null;
                    }
                }
            }
        }
    }
}

