package com.seachange.healthandsafty.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.seachange.healthandsafty.R;
import com.seachange.healthandsafty.application.SCApplication;
import com.seachange.healthandsafty.helper.View.AnimatedExpandableListView.AnimatedExpandableListAdapter;
import com.seachange.healthandsafty.model.ScheduleGroup;
import com.seachange.healthandsafty.model.ScheduleTime;
import com.seachange.healthandsafty.model.ZoneCheckTimeRange;
import com.seachange.healthandsafty.utils.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ScheduledZoneListAdapter extends AnimatedExpandableListAdapter {

    private List<ScheduleGroup> expandableListTitle;
    private HashMap<String, List<ZoneCheckTimeRange>> expandableListDetail;
    private Context mCtx;
    private ArrayList<View> mViewHolders;
    private boolean mCopyFrom;

    public ScheduledZoneListAdapter(Context context, boolean copyFrom, List<ScheduleGroup> expandableListTitle,
                                     HashMap<String, List<ZoneCheckTimeRange>> expandableListDetail) {
        this.mCtx = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
        mViewHolders = new ArrayList<>();
        mCopyFrom = copyFrom;
    }

    public void refreshContent(List<ScheduleGroup> groupList) {
        this.expandableListTitle = groupList;
    }

    @Override
    public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder viewHolder = new ChildViewHolder();

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mCtx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            convertView = layoutInflater.inflate(R.layout.schedule_zone_list_item, parent, false);
            viewHolder.mTitleView = convertView.findViewById(R.id.schedule_zone_item_title);
            viewHolder.mSubTitleView = convertView.findViewById(R.id.schedule_zone_item__sub_title);
            viewHolder.mBackGround = convertView.findViewById(R.id.schedule_child_list_main_content);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ChildViewHolder) convertView.getTag();
        }

        ScheduleGroup groupItem = expandableListTitle.get(groupPosition);
        ZoneCheckTimeRange item = (ZoneCheckTimeRange) getChild(groupPosition, childPosition);
        if (groupItem.getWithSchedule()) {
            String timeString = getTimeString(item.getStartTime()) + " - " + getTimeString(item.getEndTime());
            if (item.isEndTimeIsNextDay()) {
                timeString = timeString + "(" + new DateUtil().getDaySubString(groupItem.getTabDay()) + ")";
            }

            viewHolder.mTitleView.setText(timeString);

            if (item.getIntervalInMinutes() > 59) {
                viewHolder.mSubTitleView.setText(getHourFrequency(item.getIntervalInMinutes()) + "h");
            } else {
                viewHolder.mSubTitleView.setText(item.getIntervalInMinutes().toString() + "m");
            }
        } else {
            viewHolder.mTitleView.setText("No Schedule");
            viewHolder.mSubTitleView.setText("");
        }

        return convertView;
    }

    private String getTimeString(String timeString) {
        String time = "";
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            if(timeString !=null) {
                Date date = format.parse(timeString);
                time = mFormat.format(date);
            }
        }catch (ParseException ex) {
            ex.printStackTrace();
        }
        return time;
    }

    private int getHourFrequency(int minutes) {
        return minutes / 60;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        if(mCopyFrom) {
            return expandableListDetail.get(this.expandableListTitle.get(groupPosition).getTitle()).size();
        } else {
            return 0;
        }
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

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition).getTitle())
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        GroupViewHolder holder = new GroupViewHolder();

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mCtx.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            convertView = layoutInflater.inflate(R.layout.schedule_zone_group_item, parent, false);
            holder.mTitleView = convertView.findViewById(R.id.scheduled_zone_title_content);
            holder.mTickIcon = convertView.findViewById(R.id.schedule_setup_tick_icon);
            holder.mBackGround = convertView.findViewById(R.id.schedule_list_main_content);
            convertView.setTag(holder);
        }else {
            holder = (GroupViewHolder) convertView.getTag();
        }

        //hide tick icon
        holder.mTickIcon.setVisibility(View.GONE);

        ScheduleGroup item = expandableListTitle.get(listPosition);
        holder.mTitleView.setTextColor(ContextCompat.getColor(mCtx, R.color.alertTitle));

        //copy from item ui
        if (mCopyFrom) {
            if (item.getWithSchedule()) {
                if (item.getSelected()) {
                    holder.mBackGround.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.back_grey));
                    holder.mTickIcon.setVisibility(View.VISIBLE);
                } else {
                    holder.mBackGround.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.sysWhite));
                    holder.mTickIcon.setVisibility(View.GONE);
                }
            } else {
                holder.mTitleView.setTextColor(ContextCompat.getColor(mCtx, R.color.alertContent));
                holder.mBackGround.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.sysWhite));
                holder.mTickIcon.setVisibility(View.GONE);
            }
        } else {
            if (item.getSelected()) {
                holder.mBackGround.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.back_grey));
                holder.mTickIcon.setVisibility(View.VISIBLE);
            } else {
                holder.mBackGround.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.sysWhite));
                holder.mTickIcon.setVisibility(View.GONE);
            }
        }

        //copy to ui
        holder.mTickIcon.setTypeface(SCApplication.FontMaterial());


        //if select set background show arrow icon
        //if copy from unable multi selection
        //if copy to allow only one selection


        holder.mTitleView.setText(expandableListTitle.get(listPosition).getTitle());
        return convertView;
    }

    private static class GroupViewHolder {
        private TextView mTitleView, mTickIcon;
        private RelativeLayout mBackGround;
    }

    private static class ChildViewHolder {
        private TextView mTitleView, mSubTitleView;
        private LinearLayout mBackGround;
    }

        @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
