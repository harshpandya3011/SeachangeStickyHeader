package com.seachange.healthandsafty.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.jetbrains.annotations.Nullable;
import org.parceler.Parcels;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

import com.seachange.healthandsafty.activity.HazardSourceActivity;
import com.seachange.healthandsafty.helper.DBTourCheckHelper;
import com.seachange.healthandsafty.helper.DBZoneCheckHelper;
import com.seachange.healthandsafty.helper.tourcheck.DBTourCheckManager;
import com.seachange.healthandsafty.helper.tourcheck.DBTourCheckManagerView;
import com.seachange.healthandsafty.model.DBTourCheckModel;
import com.seachange.healthandsafty.model.DBZoneCheckModel;
import com.seachange.healthandsafty.model.ZoneCheckHazard;
import com.seachange.healthandsafty.activity.HazardTypeActivity;
import com.seachange.healthandsafty.adapter.HazardSourceRecyclerViewAdapter;
import com.seachange.healthandsafty.adapter.HazardSourceVerticalViewAdapter;
import com.seachange.healthandsafty.application.SCApplication;
import com.seachange.healthandsafty.helper.HazardObserver;
import com.seachange.healthandsafty.helper.Logger;
import com.seachange.healthandsafty.helper.PreferenceHelper;
import com.seachange.healthandsafty.model.HazardSource;
import com.seachange.healthandsafty.model.HazardType;
import com.seachange.healthandsafty.model.SiteZone;
import com.seachange.healthandsafty.R;
import com.seachange.healthandsafty.utils.UtilStrings;

public class HazardSourceFragment extends BaseFragment implements View.OnClickListener, Observer, DBTourCheckManagerView {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 2, screenWidth;
    private OnFragmentInteractionListener mListener;

    private HazardSourceRecyclerViewAdapter mHazardTypeAdapter;
    private HazardSourceVerticalViewAdapter mHazardSourceAdapter;
    private RecyclerView recentRecyclerView, sourceRecyclerView;
    private int mSourceTypeSelected = 0, mZoneId;
    private Button mAddHazard;
    private ArrayList<HazardSource> mHazardSources;
    public ArrayList<HazardType> mHazardTypes;
    private SiteZone mSiteZone;
    private TextView mSelectedHazards, mDownArrow;
    private ArrayList<ZoneCheckHazard> mSelectHazards;
    private String siteTourId, mCheckTimeId;
    private NestedScrollView mScrollView;
    private DBZoneCheckHelper mDBChecker;
    private boolean isTourCheck = false;
    private RelativeLayout progressRel;
    private View mBlockView;
    private DBTourCheckManager mDBTourCheckManager;
    private DBTourCheckModel mCurrentDBTourCheck;
    private DBTourCheckHelper mDBTourCheckHelper;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        mSelectHazards = new ArrayList<>();
        HazardObserver.getInstance().setHazardChanged(false);
        screenWidth = this.getResources().getDisplayMetrics().widthPixels;
        getExtras();
        setUpListener();

        if (isTourCheck) {
            mDBTourCheckHelper = new  DBTourCheckHelper(mCtx);
            mCurrentDBTourCheck = mDBTourCheckHelper.getCurrentTourCheck();
            mDBTourCheckManager = new DBTourCheckManager(this, ((HazardSourceActivity)getActivity()));
        }
    }

    private void getExtras() {
        isTourCheck = getActivity().getIntent().getExtras().getBoolean(UtilStrings.TOUR_CHECK);

        siteTourId = getActivity().getIntent().getExtras().getString(UtilStrings.SITE_TOUR_ID);
        mZoneId = getActivity().getIntent().getExtras().getInt(UtilStrings.ZONE_ID);
        if (getActivity().getIntent().hasExtra(UtilStrings.SITE_ZONE)) {
            mSiteZone = Parcels.unwrap(getActivity().getIntent().getParcelableExtra(UtilStrings.SITE_ZONE));
        }

        if (getActivity().getIntent().hasExtra(UtilStrings.CHECK_TIME_ID)) {
            mCheckTimeId = getActivity().getIntent().getExtras().getString(UtilStrings.CHECK_TIME_ID);
        }
    }

    private void setUpListener() {
        mListener = new OnFragmentInteractionListener() {

            @Override
            public void onSourceFragmentType(HazardType hazardType, int position) {

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
            }

            @Override
            public void onSourceFragmentSource(HazardSource source, View frontView, View backGround, View titleView) {
                goToSelectHazardScreen(source, frontView, backGround, titleView);
            }
        };
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.updateAddButton();
        mDBChecker = new DBZoneCheckHelper(mCtx);
        mAddHazard.setOnClickListener(this);
        initProgressView();
        mHazardSources = PreferenceHelper.getInstance(mCtx).getSourceAndTypeData();
        SiteZone zone = PreferenceHelper.getInstance(mCtx).getFrequentlyFoundByZone(mZoneId);
        if (zone !=null) {
            mHazardTypes = zone.getHazardTypes();
        }
        if (mHazardSources != null) {
            mHazardSourceAdapter = new HazardSourceVerticalViewAdapter(mCtx, mHazardSources, screenWidth, mListener);
            sourceRecyclerView.setAdapter(mHazardSourceAdapter);
        } else {
            showLoadingSpinner();
        }
        if (mHazardTypes != null) {
            mHazardTypeAdapter = new HazardSourceRecyclerViewAdapter(mCtx, mHazardTypes, mListener);
            recentRecyclerView.setAdapter(mHazardTypeAdapter);
        }

        YoYo.with(Techniques.Bounce)
                .duration(1000)
                .repeat(0)
                .playOn(mDownArrow);

        mScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (mDownArrow.getVisibility() == View.VISIBLE) mDownArrow.setVisibility(View.GONE);
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hazardsource_list, container, false);
        Context context = view.getContext();

        recentRecyclerView = view.findViewById(R.id.hazard_source_recent);
        sourceRecyclerView = view.findViewById(R.id.hazard_source_type);
        mAddHazard = view.findViewById(R.id.source_add_hazard);
        mIndicator =  view.findViewById(R.id.avi_source);
        mProgressView = view.findViewById(R.id.progress_rel_source);
        mSelectedHazards = view.findViewById(R.id.source_selected_hazards);
        mDownArrow = view.findViewById(R.id.source_down_arrow);
        mScrollView = view.findViewById(R.id.source_scrollview);

        progressRel = view.findViewById(R.id.source_send_progress);
        mBlockView = view.findViewById(R.id.source_block_view);

        sourceRecyclerView.setNestedScrollingEnabled(false);
        mDownArrow.setTypeface(SCApplication.FontMaterial());
        if (mColumnCount <= 1) {
            sourceRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            sourceRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        recentRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        return view;
    }

    private void updateSelectedHazards(Boolean add, HazardType hazardType) {

        if (isTourCheck) {
            if (add) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                String time = df.format(Calendar.getInstance().getTime());
                String hazardId = UUID.randomUUID().toString();
                ZoneCheckHazard hazard = new ZoneCheckHazard();

//                hazard.setGroupId(mCayoSite.getGroupId());
//                hazard.setZoneId(mZoneId);
//                hazard.setZoneCheckId(mZonePref.getZoneUUID());
//                if (!mCheckTimeId.equals("")){
//                    hazard.setScheduledZoneCheckTimeId(mCheckTimeId);
//                }else {
//                    hazard.setScheduledZoneCheckTimeId(null);
//                }
//                hazard.setSiteTourId(siteTourId);
                hazard.setHazardId(hazardId);
                hazard.setTimeFound(time);
                hazard.setTypeId(hazardType.getType_id());
                hazard.setTypeName(hazardType.getType_name());
                hazard.setCategoryName(hazardType.getCategory());
                hazard.setRiskName("Slip/Trip/Fall");
                hazard.setResolved(true);
//                hazard.setPasscodeUser(new UserDateHelper(mCtx).getUserJsonString());
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
                hazard.setCategoryName(hazardType.getCategory());
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
        }
    }

    private void goToSelectHazardScreen(HazardSource source, View frontView, View backGround, View titleView){
        if (getActivity() == null)return;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Pair<View, String> pair1 = Pair.create(frontView, frontView.getTransitionName());
            Pair<View, String> pair2 = Pair.create(backGround, backGround.getTransitionName());
            Pair<View, String> pair3 = Pair.create(titleView, titleView.getTransitionName());

            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(getActivity(), pair1, pair2, pair3);
            Intent intent = new Intent(getActivity(), HazardTypeActivity.class);
            intent.putExtra(UtilStrings.OBJECT_HAZARD_SOURCE, Parcels.wrap(source));
            intent.putExtra(UtilStrings.SITE_ZONE, Parcels.wrap(mSiteZone));
            intent.putExtra(UtilStrings.CHECK_TIME_ID, mCheckTimeId);
            intent.putExtra(UtilStrings.SITE_TOUR_ID, siteTourId);
            intent.putExtra(UtilStrings.ZONE_ID, mZoneId);
            intent.putExtra(UtilStrings.TOUR_CHECK, isTourCheck);

            startActivity(intent, options.toBundle());
        } else {
            Intent intent = new Intent(getActivity(), HazardTypeActivity.class);
            intent.putExtra(UtilStrings.OBJECT_HAZARD_SOURCE, Parcels.wrap(source));
            intent.putExtra(UtilStrings.SITE_ZONE, Parcels.wrap(mSiteZone));
            intent.putExtra(UtilStrings.CHECK_TIME_ID, mCheckTimeId);
            intent.putExtra(UtilStrings.SITE_TOUR_ID, siteTourId);
            intent.putExtra(UtilStrings.TOUR_CHECK, isTourCheck);
            intent.putExtra(UtilStrings.ZONE_ID, mZoneId);
            startActivity(intent);
        }
    }

    private void updateAddButton () {
        if (mSourceTypeSelected == 0){
            mAddHazard.setEnabled(false);
            mSelectedHazards.setVisibility(View.VISIBLE);
        } else {
            mAddHazard.setEnabled(true);
            mSelectedHazards.setVisibility(View.VISIBLE);
            mSelectedHazards.setText(mSourceTypeSelected + " Selected");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        HazardObserver.getInstance().addObserver(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.source_add_hazard:
               addHazardToServer();
               break;
        }
    }

    private void addHazardToServer(){
        if (mSelectHazards.size()>0){
            if (isTourCheck) {
                tourAddHazardToDb();
            } else {
                mDBChecker.onAddHazards(mSelectHazards);
            }
            PreferenceHelper.getInstance(mCtx).addHazardsWithNumber(getCurrentHazards());
            HazardObserver.getInstance().setHazardChanged(true);
            if (getActivity() != null) {
                ((HazardSourceActivity) getActivity()).setLoading(false);
                getActivity().finish();
            }
            Logger.info("DBBUILDER: add hazards on categroy: " + mDBChecker.getCheckForDB());
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
        mDBTourCheckManager.updateOrEnterTourChecks(mCurrentDBTourCheck, ((HazardSourceActivity)getActivity()).mApplication);
    }

    public int getCurrentHazards() {
        int hazards = 0;
        for (int i=0;i<mHazardTypes.size();i++) {
            if (mHazardTypes.get(i).isSelected()) {
                hazards++;
            }
        }
        return hazards;
    }

    @Override
    public void update(Observable observable, Object data) {
        if (!isLive) return;
        HazardObserver observer = (HazardObserver) observable;
        if (observer.isHazardChanged()) {
            if (getActivity()!=null) {
                getActivity().finish();
            }
        } else if (observer.isSourceDataReceived()) {
            mHazardSources = PreferenceHelper.getInstance(mCtx).getSourceAndTypeData();
            if (mHazardSources != null) {
                mHazardSourceAdapter = new HazardSourceVerticalViewAdapter(mCtx, mHazardSources, screenWidth, mListener);
                sourceRecyclerView.setAdapter(mHazardSourceAdapter);
                hideLoadingSpinner();
                Logger.info("data received notification received, update UI now");
            }
        } else if (observer.isFrequentlyDataReceived()) {
            mHazardTypes = PreferenceHelper.getInstance(mCtx).getFrequentlyFoundByZone(mZoneId).getHazardTypes();
            if (mHazardTypes != null) {
                mHazardTypeAdapter = new HazardSourceRecyclerViewAdapter(mCtx, mHazardTypes, mListener);
                recentRecyclerView.setAdapter(mHazardTypeAdapter);
            }
        }
    }

    private void showProgress() {
        progressRel.setVisibility(View.VISIBLE);
        mBlockView.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressRel.setVisibility(View.GONE);
        mBlockView.setVisibility(View.GONE);
    }

    @Override
    public void allTourChecksInDB(@Nullable ArrayList<DBTourCheckModel> mList) {

    }

    @Override
    public void getTourCheckByIdInDB(@Nullable DBTourCheckModel mTour) {

    }

    public interface OnFragmentInteractionListener {
        void onSourceFragmentType(HazardType hazardType, int position);
        void onSourceFragmentSource(HazardSource source, View frontView, View background, View titleView);
    }
}
