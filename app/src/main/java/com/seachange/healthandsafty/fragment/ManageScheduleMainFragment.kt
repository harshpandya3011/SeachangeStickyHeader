package com.seachange.healthandsafty.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.android.volley.VolleyError
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.activity.ZoneSchedulingActivity
import com.seachange.healthandsafty.adapter.ScheduleZoneRecyclerViewAdapter
import com.seachange.healthandsafty.helper.Logger
import com.seachange.healthandsafty.helper.PreferenceHelper
import com.seachange.healthandsafty.helper.ScheduleSetUpHelper
import com.seachange.healthandsafty.model.CaygoSite
import com.seachange.healthandsafty.model.SiteZone
import com.seachange.healthandsafty.presenter.ScheduleRequestPresenter
import com.seachange.healthandsafty.view.ScheduleRequestView
import com.seachange.healthandsafty.viewmodel.ManageScheduleViewModel
import kotlinx.android.synthetic.main.fragment_manage_schedule_main.*

class ManageScheduleMainFragment : BaseFragment() , ScheduleRequestView {

    private val manageScheduleViewModel by activityViewModels<ManageScheduleViewModel>()

    private var listener: OnFragmentInteractionListener? = null
    private var mScheduledList : ArrayList<SiteZone>? = null
    private var mScheduleList : ArrayList<SiteZone>? = null
    private var mZoneList: ArrayList<SiteZone>? = ArrayList()
    private var mCaygoSite: CaygoSite ? = null
    private var mScheduleHelper: ScheduleSetUpHelper? = null
    private var mPresenter: ScheduleRequestPresenter? = null
    private var removeDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mScheduleHelper = ScheduleSetUpHelper(mCtx)
        initData()
        mPresenter = ScheduleRequestPresenter(mCtx!!, this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initActionBar()
        initClickListener()
        processData()
        pullToRefresh()
    }

    private fun initActionBar() {
        if (activity != null) {
            (activity as AppCompatActivity).supportActionBar!!.title = "Scheduled Zones"
        }
    }

    private fun pullToRefresh() {
        schedule_swipe_container.setOnRefreshListener {
            mPresenter!!.fetchSiteDataFromServer()
        }
    }

    override fun onResume() {
        super.onResume()
        initData()
        processData()
    }

    private fun initData() {
        mCaygoSite = PreferenceHelper.getInstance(mCtx).siteData
        mZoneList = mCaygoSite!!.siteZones
        mScheduleHelper!!.initSchedules()
    }

    private fun processData() {
        mScheduledList = ArrayList()
        mScheduleList = ArrayList()
        for (tmp in mZoneList!!) {
            if (tmp.isScheduled) {
                mScheduledList!!.add(tmp)
            } else {
                mScheduleList!!.add(tmp)
            }
        }
        scheduled_list.adapter = ScheduleZoneRecyclerViewAdapter(mScheduledList!!, activity!!.applicationContext, true, listener)
        unscheduled_list.adapter = ScheduleZoneRecyclerViewAdapter(mScheduleList!!, activity!!.applicationContext, false, listener)
        if (mZoneList!!.size == 0) showEmptyView()
    }

    private fun showEmptyView() {
        schedule_no_zones_view.visibility = View.VISIBLE
    }

    private fun initClickListener() {
        listener = object : OnFragmentInteractionListener {
            override fun onFragmentInteraction(zone: SiteZone) {

            }

            override fun onZoneRemoveClicked(zone: SiteZone) {
                showRemoveAlert(zone)
            }

            override fun onZoneEditClicked(zone: SiteZone) {
                if (activity != null) {
                    val scheduleSetting = mCaygoSite!!.getScheduleSettingByZoneId(zone.zone_id)
                    if (scheduleSetting != null && scheduleSetting.isHasRegularSchedule)  {
                        (activity as ZoneSchedulingActivity).selectZoneTime(zone, true)
                    } else {
                        (activity as ZoneSchedulingActivity).irregularCalendarView(zone)
                    }
                }
            }

            override fun onZoneAddClicked(zone: SiteZone) {
                if (activity != null) {
                    //
                    //regular schedule only at the moment, when irregular ready, show schedule type first...
                    //
//                    (activity as ZoneSchedulingActivity).selectScheduleType(zone)
                    (activity as ZoneSchedulingActivity).selectZoneTime(zone, false)
                }
            }
        }
    }

    private fun showRemoveAlert(zone: SiteZone) {
        removeDialog?.dismiss()
        removeDialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.remove_zone_schedule)
                .setMessage(getString(R.string.msg_remove_zone_schedule, zone.zone_name))
                .setNegativeButton(R.string.cancel_button) { dialog, which ->
                    dialog.cancel()
                }
                .setPositiveButton(R.string.remove, null)
                .setCancelable(false)
                .show()

        removeDialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
            (it as TextView).text = null
            it.setCompoundDrawables(
                    CircularProgressDrawable(requireContext()).apply {
                        centerRadius = resources.getDimension(com.google.android.material.R.dimen.abc_dialog_padding_material) / 2
                        setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                        strokeWidth = resources.getDimension(com.google.android.material.R.dimen.abc_progress_bar_height_material)
                        start()
                        setBounds(0, 0, it.width, it.height)
                    },
                    null,
                    null,
                    null
            )
            it.isEnabled = false
            removeDialog?.getButton(AlertDialog.BUTTON_NEGATIVE)?.isEnabled = false
            mPresenter!!.removeZoneScheduleFromServer(mCaygoSite!!.site_id, zone.zone_id)
        }
    }

    override fun refreshSuccessfully() {
        Logger.info("schedule refresh good")
        schedule_swipe_container.isRefreshing = false
        initData()
        processData()
        removeDialog?.dismiss()
    }

    override fun refreshWithError(error: VolleyError?) {

    }

    override fun removeSuccessfully() {
        Logger.info( "schedule removed good")
    }

    override fun removeWithError(error: VolleyError?) {
        Logger.info("schedule removed shite")
        schedule_swipe_container.isRefreshing = false
        removeDialog?.dismiss()
        manageScheduleViewModel.postError(error)
    }

    override fun addTimeRangeWithError(error: VolleyError?) {

    }

    override fun reloadPage() {

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_manage_schedule_main, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeDialog = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(zone: SiteZone)
        fun onZoneRemoveClicked(zone: SiteZone)
        fun onZoneEditClicked(zone: SiteZone)
        fun onZoneAddClicked(zone: SiteZone)
    }
}
