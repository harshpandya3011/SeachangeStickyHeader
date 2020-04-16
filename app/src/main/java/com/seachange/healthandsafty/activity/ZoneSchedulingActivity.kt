package com.seachange.healthandsafty.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import com.android.volley.AuthFailureError
import com.android.volley.NoConnectionError
import com.android.volley.ServerError
import com.android.volley.TimeoutError
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.fragment.*
import com.seachange.healthandsafty.model.SiteZone
import com.seachange.healthandsafty.model.ZoneCheckTimeRange
import com.seachange.healthandsafty.viewmodel.ManageScheduleViewModel
import java.net.HttpURLConnection

class ZoneSchedulingActivity : BaseActivity() {

    private val manageScheduleViewModel by viewModels<ManageScheduleViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zone_scheduling)
        if (supportFragmentManager.fragments.isEmpty()) {
            supportFragmentManager.commit {
                replace(R.id.fl_zone_schedule, ManageScheduleMainFragment())
            }
        }
        setupActionBar()
        initViewModel()
    }

    private fun setupActionBar() {
        supportActionBar?.title = "Schedule Zone"
        supportActionBar?.run {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun initViewModel() {
        manageScheduleViewModel.errorLiveData.observe(this, Observer {
            if (it == null) return@Observer

            when (it) {
                is NoConnectionError -> {
                    AlertDialog.Builder(this)
                            .setTitle(R.string.no_connection_error_title)
                            .setMessage(R.string.no_connection_error)
                            .setPositiveButton(R.string.close) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .setIcon(R.drawable.ic_cloud_off_black_24dp)
                            .show()
                }
                is ServerError -> {
                    AlertDialog.Builder(this)
                            .setTitle(R.string.server_error_title)
                            .setMessage(R.string.server_error)
                            .setPositiveButton(R.string.close) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .setIcon(R.drawable.ic_mood_bad_black_24dp)
                            .show()
                }
                is TimeoutError -> {
                    AlertDialog.Builder(this)
                            .setTitle(R.string.timeout_error_title)
                            .setMessage(R.string.timeout_error)
                            .setPositiveButton(R.string.close) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .setIcon(R.drawable.ic_access_time_black_24dp)
                            .show()
                }
                is AuthFailureError -> {
                    if (it.networkResponse.statusCode == HttpURLConnection.HTTP_FORBIDDEN) {
                        AlertDialog.Builder(this)
                                .setTitle(R.string.permission_denied_error_title)
                                .setMessage(R.string.permission_denied_error)
                                .setPositiveButton(R.string.close) { dialog, _ ->
                                    dialog.dismiss()
                                }
                                .setIcon(R.drawable.ic_block_black_24dp)
                                .show()
                    }
                }
            }

        })
    }

    fun selectZoneToSchedule() {
        supportFragmentManager.commit {
            setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right)
            replace(R.id.fl_zone_schedule, ScheduleZonesFragment())
            addToBackStack(null)
        }
    }

    fun selectScheduleType(zone:SiteZone) {
        val typeFragment = ScheduleZoneTypeFragment()
        typeFragment.initZone(zone)
        supportFragmentManager.commit {
            setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right)
            replace(R.id.fl_zone_schedule, typeFragment)
            addToBackStack(null)
        }
    }

    fun selectIrregularType(zone: SiteZone) {
        val mFrag = IrregularScheduleFragment()
        mFrag.initZone(zone)
        supportFragmentManager.commit {
            setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right)
            replace(R.id.fl_zone_schedule, mFrag)
            addToBackStack(null)
        }
    }

    fun irregularCalendarView(zone: SiteZone) {
        val mFrag = ScheduleCalendarFragment()
        mFrag.initZone(zone)
        supportFragmentManager.commit {
            setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right)
            replace(R.id.fl_zone_schedule, mFrag)
            addToBackStack(null)
        }
    }

    fun selectIrregularTimeSetUp(zone: SiteZone, date: String) {
        val mFrag = ScheduleDateTimeFragment()
        mFrag.initData(zone, 0, false, date, false, null)
        supportFragmentManager.commit {
            setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right)
            replace(R.id.fl_zone_schedule, mFrag)
            addToBackStack(null)
        }
    }

    fun selectTimeRangeSetUp(zone: SiteZone, date: String) {
        val mFrag = IrregularScheduleTimeFragment()
        mFrag.initZone(zone, date)
        supportFragmentManager.commit {
            setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right)
            replace(R.id.fl_zone_schedule, mFrag)
            addToBackStack(null)
        }
    }

    fun selectRegularTimeRangeSetUp(zone: SiteZone, tab: Int, isReg: Boolean, edit: Boolean, timeRange: ZoneCheckTimeRange?) {
        val mFrag = ScheduleDateTimeFragment()
        mFrag.initData(zone, tab, isReg, "", edit, timeRange)
        supportFragmentManager.commit {
            setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right)
            replace(R.id.fl_zone_schedule, mFrag)
            addToBackStack(null)
        }
    }

    fun selectIrregularTimeRangeSetUp(zone: SiteZone, tab: Int, isReg: Boolean, edit: Boolean, timeRange: ZoneCheckTimeRange?, date: String) {
        val mFrag = ScheduleDateTimeFragment()
        mFrag.initData(zone, tab, isReg, date, edit, timeRange)
        supportFragmentManager.commit {
            setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right)
            replace(R.id.fl_zone_schedule, mFrag)
            addToBackStack(null)
        }
    }

    fun selectZoneTime(zone: SiteZone, edit: Boolean) {
        val mFrag = ScheduleZoneTabFragment()
        mFrag.initZone(zone, edit)
        supportFragmentManager.commit {
            setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right)
            replace(R.id.fl_zone_schedule, mFrag)
            addToBackStack(null)
        }
    }

    fun tabCopyFragment(zone: SiteZone, tab: Int, fromTab: Boolean) {
        val mFrag = ViewScheduledZonesFragment()
        mFrag.initData(zone, tab, fromTab)
        supportFragmentManager.commit {
            setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right)
            replace(R.id.fl_zone_schedule, mFrag)
            addToBackStack(null)
        }
    }
}
