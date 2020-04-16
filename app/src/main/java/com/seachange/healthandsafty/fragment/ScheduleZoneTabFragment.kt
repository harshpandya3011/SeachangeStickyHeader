package com.seachange.healthandsafty.fragment

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.android.volley.VolleyError
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.activity.ZoneSchedulingActivity
import com.seachange.healthandsafty.helper.*
import com.seachange.healthandsafty.helper.View.cardbutton.CardItem
import com.seachange.healthandsafty.helper.View.cardbutton.RapidFloatingActionContentCardListView
import com.seachange.healthandsafty.model.*
import com.seachange.healthandsafty.presenter.ScheduleRequestPresenter
import com.seachange.healthandsafty.utils.DateUtil
import com.seachange.healthandsafty.view.ScheduleRequestView
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper
import kotlinx.android.synthetic.main.fragment_blank.*
import java.util.*
import android.text.style.ForegroundColorSpan
import android.text.SpannableString
import android.text.SpannableStringBuilder
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.seachange.healthandsafty.viewmodel.ManageScheduleViewModel


class ScheduleZoneTabFragment : BaseFragment(), Observer, RapidFloatingActionContentCardListView.OnRapidFloatingActionContentCardListViewListener, ScheduleRequestView {

    private val manageScheduleViewModel by activityViewModels<ManageScheduleViewModel>()

    private lateinit var mViewPager: CustomViewPager
    private lateinit var mTab:TabLayout
    private var zone: SiteZone? = null
    private var rfabHelper: RapidFloatingActionHelper? = null
    private var mCurrentTab: Int = 0
    private var mScheduleHelper: ScheduleSetUpHelper? = null
    private var mPresenter: ScheduleRequestPresenter? = null
    private var mCaygoSite: CaygoSite? = null
    private var mZoneScheduleSetting: ScheduleSetting? = null
    private var mIsEdit = false
    private val mTabDot = "•"

    private val TAB_TITLES = arrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2,
            R.string.tab_text_3,
            R.string.tab_text_4,
            R.string.tab_text_5,
            R.string.tab_text_6,
            R.string.tab_text_7
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCaygoSite = PreferenceHelper.getInstance(mCtx).siteData
        mScheduleHelper = ScheduleSetUpHelper(mCtx)
        mPresenter = ScheduleRequestPresenter(mCtx, this)
        HazardObserver.getInstance().isScheduleUpdated = false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar!!.title = zone!!.zone_name
        initView()
        initButton()
        initFloatButtons(DateUtil().getDayString(0))
        initTabSelection()
    }

    override fun onStop() {
        super.onStop()
        HazardObserver.getInstance().isScheduleUpdated = false
    }

    override fun onDestroy() {
        super.onDestroy()
        HazardObserver.getInstance().deleteObserver(this)
    }

    override fun refreshSuccessfully() {
        Logger.info("schedule post successfully")
        updateButtonAndSpinner(false)
        mZoneScheduleSetting!!.isUnSync = false
        mScheduleHelper!!.updateCurrentScheduleSettingByZoneId(zone!!.zone_id, mZoneScheduleSetting, false)

        //remove current saved data, used latest data from server
        mScheduleHelper!!.resetSavedSchedules()
        mScheduleHelper!!.initSchedules()

        showSnackBar()
        initZoneScheduleSetting()
    }

    override fun refreshWithError(error: VolleyError?) {
        Logger.info("schedule post refresh failed")
    }

    override fun removeSuccessfully() {

    }

    override fun removeWithError(error: VolleyError?) {

    }

    override fun addTimeRangeWithError(error: VolleyError?) {
        Logger.info("schedule post failed")
        updateButtonAndSpinner(false)
        manageScheduleViewModel.postError(error)
    }

    override fun reloadPage() {

    }

    private fun initTabSelection() {
        mTab.addOnTabSelectedListener(object :
                TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                initFloatButtons(DateUtil().getDayString(tab.position))
                mCurrentTab = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }

    private fun updateTabTitle(tab:String):SpannableStringBuilder {
        val builder = SpannableStringBuilder()
        val redSpannable = SpannableString(mTabDot)
        redSpannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(mCtx, R.color.colorPrimary)), 0, mTabDot.length, 0)
        builder.append(redSpannable)

        val whiteSpannable = SpannableString(tab)
        whiteSpannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(mCtx, R.color.alertTitle)), 0, tab.length, 0)
        builder.append(whiteSpannable)
        return builder
    }

    private fun updateTabTitleNoUnsync(tab:String):SpannableStringBuilder {
        val builder = SpannableStringBuilder()
        val whiteSpannable = SpannableString(tab)
        whiteSpannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(mCtx, R.color.alertTitle)), 0, tab.length, 0)
        builder.append(whiteSpannable)
        return builder
    }

    private fun updateTabTitleNoSchedule(tab:String):SpannableStringBuilder {
        val builder = SpannableStringBuilder()
        val whiteSpannable = SpannableString(tab)
        whiteSpannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(mCtx, R.color.jsaNumberBoxBackground)), 0, tab.length, 0)
        builder.append(whiteSpannable)
        return builder
    }

    override fun onResume() {
        super.onResume()
        initZoneScheduleSetting()
        HazardObserver.getInstance().addObserver(this)
    }

    private fun initZoneScheduleSetting() {
        this.initZoneScheduleSettingByTitle(true)
    }

    private fun initZoneScheduleSettingByTitle(updateTitle: Boolean) {
        mZoneScheduleSetting = mScheduleHelper!!.getCurrentScheduleSettingByZoneId(zone!!.zone_id, true)
        if(mZoneScheduleSetting!=null) {
            schedule_save_button.isEnabled = mZoneScheduleSetting!!.isUnSync
            Logger.info("this is the button: " + mZoneScheduleSetting!!.isUnSync)
        } else {
            schedule_save_button.isEnabled = false
            Logger.info("this is the button: disable" )
        }
        if (updateTitle) processTabTitle()
    }

    fun updateButtonFromChildFragment() {
        this.initZoneScheduleSetting()
    }

    private fun processTabTitle() {
       val count = mTab.tabCount
        for (i in 0..count) {
            if (mZoneScheduleSetting !=null){
                var currentSchedule = mZoneScheduleSetting!!.getScheduleByDay(i+1)
                if(currentSchedule !=null) {

//                if not synced show dot else only show title
                    if(currentSchedule.isNeedToSync) {
                        mTab.getTabAt(i)?.text = updateTabTitle(context!!.resources.getString(TAB_TITLES[i]))
                    } else {
                        mTab.getTabAt(i)?.text = updateTabTitleNoUnsync(context!!.resources.getString(TAB_TITLES[i]))
                    }
                }else {
                    if (mTab!=null && mCtx!=null)
                        mTab.getTabAt(i)?.text = updateTabTitleNoSchedule(mCtx!!.resources.getString(TAB_TITLES[i]))
                }
            } else {
                if (mTab!=null && mCtx!=null)
                    mTab.getTabAt(i)?.text = updateTabTitleNoSchedule(mCtx!!.resources.getString(TAB_TITLES[i]))
            }
        }
    }

    private fun initFloatButtons(day: String) {
        val rfaContent = RapidFloatingActionContentCardListView(activity!!.applicationContext)
        rfaContent.setOnRapidFloatingActionContentCardListViewListener(this)

        val cardItems = ArrayList<CardItem>()
        cardItems.add(CardItem().setName("Copy $day To"))
        cardItems.add(CardItem().setName("Copy $day From"))
        cardItems.add(CardItem().setName("Add Time Range"))
        rfaContent.setList(cardItems)

        card_list_sample_rfal.setIsContentAboveLayout(false)
        card_list_sample_rfal.setDisableContentDefaultAnimation(true)

        rfabHelper = RapidFloatingActionHelper(
                activity!!.applicationContext,
                card_list_sample_rfal,
                card_list_sample_rfab,
                rfaContent
        ).build()
    }

    fun initZone(selectZone: SiteZone, edit: Boolean) {
        zone = selectZone
        mIsEdit = edit
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_blank, container, false)
        mViewPager = view.findViewById(R.id.zone_tab_view_pager)
        mTab= view.findViewById(R.id.zone_day_tabs)
        return view
    }

    private fun initView() {
        val sectionsPagerAdapter = SectionsPagerAdapter(childFragmentManager)
        mViewPager.adapter = sectionsPagerAdapter
        mViewPager.setPagingEnabled(false)
        mTab.setupWithViewPager(mViewPager)
    }

    private fun initButton() {
        schedule_save_button.setOnClickListener {
            //send request, when successful, back to previous page
            sendDataToServer()
            updateButtonAndSpinner(true)
        }
    }

    private fun updateButtonAndSpinner(showSpinner: Boolean) {
        if (showSpinner) {
            progress_add_schedule.visibility = View.VISIBLE
            schedule_save_button.text = ""
        } else {
            progress_add_schedule.visibility = View.GONE
            schedule_save_button.text = getString(R.string.save_settings)
        }
    }

    private fun backToPreviousPage() {
        if (activity!=null)
         activity!!.supportFragmentManager.popBackStack()
    }

    private fun sendDataToServer() {
        val gson = Gson()
        val settingRequest = ScheduleSettingRequest()
        settingRequest.setDataFromSetting(mZoneScheduleSetting)
        settingRequest.groupId = mCaygoSite!!.groupId
        Logger.info("saved schedules to send to server: " + gson.toJson(settingRequest))
        mPresenter!!.postScheduleRange(gson.toJson(settingRequest), mCaygoSite!!.site_id.toString(), mIsEdit)
    }

    override fun onRFACCardListItemClick(position: Int) {
        rfabHelper!!.toggleContent()
        when (position) {
            0 -> {
                (activity as ZoneSchedulingActivity).tabCopyFragment(zone!!, mCurrentTab, false)
            }
            1 -> {
                (activity as ZoneSchedulingActivity).tabCopyFragment(zone!!, mCurrentTab, true)
            }
            else -> (activity as ZoneSchedulingActivity).selectRegularTimeRangeSetUp(zone!!, mCurrentTab , true, false, null)
        }
    }

    override fun update(o: Observable?, arg: Any?) {
        val observer = o as HazardObserver
        if (observer.isScheduleUpdated) {
//            showSnackBar()
        }
    }

    private fun showSnackBar() {
        Handler().postDelayed({
            Snackbar.make(view!!.findViewById(R.id.snack_ref), getString(R.string.schedule_setting_saved), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }, 10)
    }

    fun editCurrentTimeRange(timeRange: ZoneCheckTimeRange) {
        (activity as ZoneSchedulingActivity).selectRegularTimeRangeSetUp(zone!!, mCurrentTab , true, true, timeRange)
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            return ScheduleZoneDayFragment.newInstance(position+1, zone!!)
        }

        override fun getCount(): Int {
            return 7
        }

        override fun getPageTitle(position: Int): CharSequence {
            return context!!.resources.getString(TAB_TITLES[position])
        }
    }
}
