package com.seachange.healthandsafty.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.pwittchen.swipe.library.Swipe;
import com.github.pwittchen.swipe.library.SwipeListener;

import org.jetbrains.annotations.Nullable;
import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import com.seachange.healthandsafty.activity.HazardTypeActivity;
import com.seachange.healthandsafty.helper.DBTourCheckHelper;
import com.seachange.healthandsafty.helper.DBZoneCheckHelper;
import com.seachange.healthandsafty.helper.tourcheck.DBTourCheckManager;
import com.seachange.healthandsafty.helper.tourcheck.DBTourCheckManagerView;
import com.seachange.healthandsafty.model.DBTourCheckModel;
import com.seachange.healthandsafty.model.DBZoneCheckModel;
import com.seachange.healthandsafty.model.ZoneCheckHazard;
import com.seachange.healthandsafty.adapter.HazardTypeRecyclerViewAdapter;
import com.seachange.healthandsafty.application.SCApplication;
import com.seachange.healthandsafty.helper.HazardObserver;
import com.seachange.healthandsafty.helper.Logger;
import com.seachange.healthandsafty.helper.PreferenceHelper;
import com.seachange.healthandsafty.model.HazardSource;
import com.seachange.healthandsafty.model.HazardType;
import com.seachange.healthandsafty.R;
import com.seachange.healthandsafty.utils.UtilStrings;

public class HazardTypeFragment extends BaseFragment implements View.OnClickListener, DBTourCheckManagerView {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 2, screenWidth;
    private OnTypeFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private HazardTypeRecyclerViewAdapter mHazardTypeAdapter;
    private int mSourceTypeSelected = 0;
    private Button mAddHazard;
    private HazardSource mHazardSource;
    private TextView mSourceTitle;
    private RelativeLayout mTopView;
    private NestedScrollView scrollView;
    private ArrayList<HazardType> mHazardTypes;
    private TextView mSelectedHazards, mDownArrow;
    private ArrayList<ZoneCheckHazard> mSelectHazards;
    private DBZoneCheckHelper mDBChecker;
    private boolean isTourCheck = false;
    private int mZoneId;
    private DBTourCheckManager mDBTourCheckManager;
    private DBTourCheckModel mCurrentDBTourCheck;
    private DBTourCheckHelper mDBTourCheckHelper;

    @SuppressWarnings("unused")
    public static HazardTypeFragment newInstance(int columnCount) {
        HazardTypeFragment fragment = new HazardTypeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        screenWidth = this.getResources().getDisplayMetrics().widthPixels;
        initListener();
        initData();
        mSelectHazards = new ArrayList<>();

        if (isTourCheck) {
            mDBTourCheckHelper = new  DBTourCheckHelper(mCtx);
            mCurrentDBTourCheck = mDBTourCheckHelper.getCurrentTourCheck();
            mDBTourCheckManager = new DBTourCheckManager(this, ((HazardTypeActivity)getActivity()));
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.updateAddButton();
        mAddHazard.setOnClickListener(this);
        mSourceTitle.setText(mHazardSource.getSource_name());
        mHazardTypeAdapter = new HazardTypeRecyclerViewAdapter(mHazardTypes, mCtx, mHazardSource.getSource_name(), screenWidth, mListener);
        mRecyclerView.setAdapter(mHazardTypeAdapter);
        mDBChecker = new DBZoneCheckHelper(mCtx);

        final Swipe swipe = new Swipe();
        swipe.setListener(new SwipeListener() {

            @Override
            public void onSwipingLeft(final MotionEvent event) {
                Logger.info("swipe left");
            }

            @Override
            public void onSwipedLeft(final MotionEvent event) {

            }

            @Override
            public void onSwipingRight(final MotionEvent event) {

            }

            @Override
            public void onSwipedRight(final MotionEvent event) {

            }

            @Override
            public void onSwipingUp(final MotionEvent event) {
                scrollView.requestDisallowInterceptTouchEvent(false);
            }

            @Override
            public void onSwipedUp(final MotionEvent event) {
                scrollView.requestDisallowInterceptTouchEvent(false);

            }

            @Override
            public void onSwipingDown(final MotionEvent event) {
                backPressed();
            }

            @Override
            public void onSwipedDown(final MotionEvent event) {

            }
        });

        YoYo.with(Techniques.Bounce)
                .duration(1000)
                .repeat(0)
                .playOn(mDownArrow);

        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

            if (mDownArrow.getVisibility() == View.VISIBLE) mDownArrow.setVisibility(View.GONE);
        });

        mTopView.setOnTouchListener((v, event) -> {
            scrollView.requestDisallowInterceptTouchEvent(true);
            swipe.dispatchTouchEvent(event);
            return true;
        });
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void backPressed() {
        if (getActivity() !=null) {
            getActivity().onBackPressed();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hazardtype_list, container, false);

        mRecyclerView = view.findViewById(R.id.hazard_type_list);
        mAddHazard = view.findViewById(R.id.type_add_button);
        mSourceTitle = view.findViewById(R.id.source_type_content_title);
        mTopView = view.findViewById(R.id.topView);
        scrollView = view.findViewById(R.id.type_scrollview);
        mSelectedHazards = view.findViewById(R.id.type_selected_hazards);
        mDownArrow = view.findViewById(R.id.type_down_arrow);
        mDownArrow.setTypeface(SCApplication.FontMaterial());

        Context context = view.getContext();
        mRecyclerView.setNestedScrollingEnabled(false);

        if (mColumnCount <= 1) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        return view;
    }

    private void initListener() {
        mListener = (hazardType, position) -> {

            boolean add;
            if(mHazardTypes.get(position).isSelected()){
                mHazardTypes.get(position).setSelected(false);
                mSourceTypeSelected --;
                add = false;
            } else {
                mHazardTypes.get(position).setSelected(true);
                mSourceTypeSelected ++;
                add = true;
            }
            updateSelectedHazards(add, hazardType);
            mHazardTypeAdapter.reloadHazards(mHazardTypes);
            mHazardTypeAdapter.notifyDataSetChanged();
            updateAddButton();

        };
    }

    private void initData() {
        mHazardSource = Parcels.unwrap(getActivity().getIntent().getParcelableExtra(UtilStrings.OBJECT_HAZARD_SOURCE));
        isTourCheck = getActivity().getIntent().getExtras().getBoolean(UtilStrings.TOUR_CHECK);
        mZoneId = getActivity().getIntent().getExtras().getInt(UtilStrings.ZONE_ID);
        mHazardTypes = mHazardSource.getHazardTypes();
    }

    private void updateSelectedHazards(Boolean add, HazardType hazardType) {

        if (isTourCheck) {
            if (add) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                String time = df.format(Calendar.getInstance().getTime());
                String hazardId = UUID.randomUUID().toString();
                ZoneCheckHazard hazard = new ZoneCheckHazard();

                hazard.setHazardId(hazardId);
                hazard.setTimeFound(time);
                hazard.setTypeId(hazardType.getType_id());
                hazard.setTypeName(hazardType.getType_name());
                hazard.setCategoryName(mHazardSource.getSource_name());
                hazard.setRiskName("Slip/Trip/Fall");
                hazard.setResolved(true);
                mSelectHazards.add(hazard);

            } else {
                for (int i = 0; i < mSelectHazards.size(); i++) {
                    ZoneCheckHazard tmp = mSelectHazards.get(i);
                    Logger.info("Hazards number: Hazards type id: " + hazardType.getType_id() + "check type id  " + tmp.getTypeId());
                    if (tmp.getTypeId().equals(hazardType.getType_id())) {
                        mSelectHazards.remove(tmp);
                        Logger.info("Hazards number: remove callled");
                    }
                }
            }
        }else {
            if (add) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                String time = df.format(Calendar.getInstance().getTime());
                String hazardId = UUID.randomUUID().toString();

                ZoneCheckHazard hazard = new ZoneCheckHazard();
                hazard.setHazardId(hazardId);
                hazard.setTimeFound(time);
                hazard.setTypeId(hazardType.getType_id());
                hazard.setTypeName(hazardType.getType_name());
                hazard.setCategoryName(mHazardSource.getSource_name());
                hazard.setRiskName("Slip/Trip/Fall");
                hazard.setResolved(true);

                mSelectHazards.add(hazard);
            } else {
                for (int i = 0; i < mSelectHazards.size(); i++) {
                    ZoneCheckHazard tmp = mSelectHazards.get(i);
                    if (tmp.getTypeId().equals(hazardType.getType_id())) {
                        mSelectHazards.remove(tmp);
                        Logger.info("Hazards number: remove callled");
                    }
                }
            }
        }
    }

    private void updateAddButton() {
        if (mSourceTypeSelected == 0){
            mAddHazard.setEnabled(false);
            mSelectedHazards.setVisibility(View.GONE);
        } else {
            mAddHazard.setEnabled(true);
            mSelectedHazards.setVisibility(View.VISIBLE);
            mSelectedHazards.setText(String.format(Locale.getDefault(),"%d Selected", mSourceTypeSelected));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.type_add_button:
                addHazardsToDB();
                break;
        }
    }

    private void addHazardsToDB() {
        if (mSelectHazards.size()>0){

            if (isTourCheck) {
                tourAddHazardToDb();
            } else {
                mDBChecker.onAddHazards(mSelectHazards);
            }

            PreferenceHelper.getInstance(mCtx).addHazardsWithNumber(getCurrentHazards());
            HazardObserver.getInstance().setHazardChanged(true);
            if (getActivity() != null) {
                ((HazardTypeActivity) getActivity()).setLoading(false);
                getActivity().finish();
            }
            Logger.info("DBBUILDER: add hazards on type: " + mDBChecker.getCheckForDB());
        }
    }

    private void tourAddHazardToDb() {
        DBZoneCheckModel tmpModel = mCurrentDBTourCheck.getExistingTourCheck(mZoneId);
        if (tmpModel != null) {
            for (int i = 0; i < mCurrentDBTourCheck.getZoneChecks().size(); i++) {
                DBZoneCheckModel tmp = mCurrentDBTourCheck.getZoneChecks().get(i);
                if (tmp.getZoneId() == tmpModel.getZoneId()) {
                    mCurrentDBTourCheck.getZoneChecks().get(i).addHazardsZoneCheckCommands(mSelectHazards);
                }
            }
        }
        mDBTourCheckHelper.saveCurrentTourCheck(mCurrentDBTourCheck);
        mDBTourCheckManager.updateOrEnterTourChecks(mCurrentDBTourCheck, ((HazardTypeActivity)getActivity()).mApplication);
    }


    private int getCurrentHazards() {
        int hazards = 0;
        for (int i=0;i<mHazardTypes.size();i++) {
            if (mHazardTypes.get(i).isSelected()) {
                hazards++;
            }
        }
        return hazards;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void allTourChecksInDB(@Nullable ArrayList<DBTourCheckModel> mList) {

    }

    @Override
    public void getTourCheckByIdInDB(@Nullable DBTourCheckModel mTour) {

    }

    public interface OnTypeFragmentInteractionListener {
        void onHazardTypeInteraction(HazardType hazardType, int position);
    }

}
