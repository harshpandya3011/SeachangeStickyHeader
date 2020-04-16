package com.seachange.healthandsafty.activity

import android.os.Handler
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.seachange.healthandsafty.R
import kotlinx.android.synthetic.main.activity_base.*
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.seachange.healthandsafty.application.SCApplication
import com.seachange.healthandsafty.helper.Logger
import com.seachange.healthandsafty.helper.PreferenceHelper


open class BaseViewActivity : AppCompatActivity() {

    protected var mNoConnectionView: RelativeLayout? = null
    protected var noDataView: RelativeLayout? = null
    protected var mSyncView: RelativeLayout? = null
    protected var mNoConnectTv: TextView? = null
    protected var mTapable: Boolean = true
    protected var unsyncedChecks: Int = 0
    protected var unsyncedTourChecks: Int = 0
    protected var activeConnection: Boolean = false
    protected var activityContainer: FrameLayout? = null
    private var isLive = false
    private var syncingInProgress: Boolean = false
    protected var mConnected = true
    private var mShowNoConnectionBaseView = false

    override fun setContentView(layoutResID: Int) {
        val coordinatorLayout: CoordinatorLayout = layoutInflater.inflate(R.layout.activity_base, null) as CoordinatorLayout
        activityContainer = coordinatorLayout.findViewById(R.id.layout_container)
        mNoConnectionView = coordinatorLayout.findViewById(R.id.base_no_connection_rel)
        mSyncView = coordinatorLayout.findViewById(R.id.base_view_offline_sync_view)
        mNoConnectTv = coordinatorLayout.findViewById(R.id.base_view_no_connection_tv)
        noDataView = coordinatorLayout.findViewById(R.id.no_connection_no_data_rel)

        layoutInflater.inflate(layoutResID, activityContainer, true)
        super.setContentView(coordinatorLayout)

        mNoConnectionView!!.visibility = View.GONE
        mSyncView!!.visibility = View.INVISIBLE

        mNoConnectionView!!.setOnClickListener {
            if (mTapable) {
                updateCheckText()
                Handler().post {
                    YoYo.with(Techniques.SlideInDown)
                            .duration(500)
                            .repeat(0)
                            .onStart {
                                mSyncView!!.visibility = View.VISIBLE

                            }
                            .onEnd {
                                disableView(activityContainer!!, true)
                                updateSyncButton()
                            }
                            .playOn(base_view_offline_sync_top_view)
                }
            }
        }

        base_view_no_connection_close.setOnClickListener {
            if (mTapable) {
                removeSyncView()
            }
        }
        initSyncButton()
    }

    override fun onResume() {
        super.onResume()
        isLive = true
        updateTextByDataChanges()
    }

    override fun onPause() {
        super.onPause()
        isLive = false
    }

    private fun removeSyncView() {
        YoYo.with(Techniques.SlideOutUp)
                .duration(500)
                .repeat(0)
                .onEnd {
                    mSyncView!!.visibility = View.GONE
                    disableView(activityContainer!!, false)
                }
                .playOn(base_view_offline_sync_top_view)
    }

    private fun initSyncButton() {
        base_view_sync_button.setOnClickListener {
            (applicationContext as SCApplication).getCachedChecks(false, true)
            //
            //remove info view if showing...
            //
            removeSyncView()
        }
    }

    fun setTapble(withInfo: Boolean) {
        mTapable = withInfo
        if (!withInfo && base_view_offline_info.visibility == View.VISIBLE) {
            base_view_offline_info.visibility = View.GONE
        }
    }

    private fun disableView(v: View, enable: Boolean) {

        v.setOnTouchListener { _, _ -> enable }

        if (v is ViewGroup) {
            for (i in 0 until v.childCount) {
                val child = v.getChildAt(i)
                disableView(child, enable)
            }
        }
    }

    fun updateNoConnectView() {
        if (mNoConnectionView == null) return
        if (!mShowNoConnectionBaseView) return
        if (mConnected) {
            if (unsyncedChecks > 0 || unsyncedTourChecks > 0) {
                mNoConnectionView!!.visibility = View.VISIBLE
            } else {
                mNoConnectionView!!.visibility = View.GONE
            }
        }else {
            mNoConnectionView!!.visibility = View.VISIBLE
        }
    }

    fun setNotSyncedChecks(checks: Int) {
        this.unsyncedChecks = checks
        updateTextByDataChanges()
    }

    fun setNotTourSyncedChecks(checks: Int) {
        this.unsyncedTourChecks = checks
        updateTextByDataChanges()
    }

    private fun updateTextByDataChanges() {
        dbUpdated()
        updateBarText()
        updateNoConnectView()
    }

    fun setShowNoConnectionView(show: Boolean) {
        mShowNoConnectionBaseView = show
    }

    private fun dbUpdated() {
        if (base_view_sync_button == null) return
        if(unsyncedChecks>0 || unsyncedTourChecks>0) {
            base_view_sync_button.isEnabled = true
            base_view_sync_button.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorDefaultYellow))
        } else {
            base_view_sync_button.isEnabled = false
            base_view_sync_button.setTextColor(ContextCompat.getColor(applicationContext, R.color.boxGray))
        }
    }

    fun updateBarText() {
        if (mNoConnectTv ==null) return
        var checkText:String = unsyncedChecks.toString() + " Checks | "
        if (unsyncedChecks ==1) checkText = unsyncedChecks.toString() + " Check | "

        var tourText:String = unsyncedTourChecks.toString() + " Tours"
        if (unsyncedTourChecks ==1) tourText = unsyncedTourChecks.toString() + " Tour"

        var prefixText = "Working Offline. "
        if (mConnected) {
            prefixText = "Unsynced Data: "
        }
        mNoConnectTv!!.text = prefixText + checkText + tourText
    }


    fun removeSyncViewIfShowing() {
        if (mSyncView == null) return
        if (mSyncView!!.visibility == View.VISIBLE) {
            YoYo.with(Techniques.SlideOutUp)
                    .duration(500)
                    .repeat(0)
                    .onEnd {
                        mSyncView!!.visibility = View.GONE
                        if (activityContainer != null) {
                            disableView(activityContainer!!, false)
                        }
                    }
                    .playOn(base_view_offline_sync_top_view)
        }
    }

    //
    //this is sync in progress view
    //white background
    //green icon
    //green progress bar
    //
    fun syncingInProgress() {
        Logger.info("sync in progress called on base view acitvity")

        if (syncingInProgress) return
        syncingInProgress = true
        if (mNoConnectionView == null) return
        mNoConnectionView!!.visibility = View.VISIBLE
        mNoConnectionView!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.sysWhite))
        mNoConnectTv!!.text = getString(R.string.syncing)
        mNoConnectTv!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorDefaultGreen))
        base_view_offline_pin.setImageResource(R.drawable.baseline_cloud_upload_24px)
        base_view_offline_info.visibility = View.GONE
        sync_main_loading_spinner.visibility = View.VISIBLE
    }

    //
    //sync successfully view, only show this when sync succeed
    //
    fun syncingEndSuccessfully() {
        if(base_view_success_tv.visibility == View.VISIBLE) return
        syncingInProgress = true
        if (mNoConnectionView == null) return
        mNoConnectionView!!.visibility = View.VISIBLE
        mNoConnectionView!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.colorDefaultGreen))
        mNoConnectTv!!.visibility = View.GONE
        base_view_success_tv.visibility = View.VISIBLE

        base_view_offline_pin.setImageResource(R.mipmap.baseline_cloud_done_white_48dp)
        mNoConnectTv!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.sysWhite))
        sync_main_loading_spinner.visibility = View.GONE
        PreferenceHelper.getInstance(applicationContext).setLastSyncedTime()

        Handler().postDelayed({
            mNoConnectionView!!.visibility = View.GONE
            base_view_success_tv.visibility = View.GONE
            restoreNoConnectionView()
            syncingInProgress = false
        }, 3000)
    }

    fun syncingEndWithError() {
        syncingInProgress = false
        if (!isLive) return
        //show error dialog...
        val builder = MaterialDialog.Builder(this)
                .content("Please check your data or wifi connection and try again. ")
                .title("Sync Failed")
                .icon(ContextCompat.getDrawable(this, R.mipmap.baseline_cloud_off_black_48dp)!!)
                .typeface(SCApplication.FontMaterial(), null)
                .titleColor(ContextCompat.getColor(applicationContext, R.color.alertTitle))
                .contentColor(ContextCompat.getColor(applicationContext, R.color.alertContent))
                .negativeText("Close")
                .onNegative { _, _ ->
                    restoreNoConnectionView()
                }

        val connectionDialog = builder.build()
        connectionDialog.setCancelable(false)
        connectionDialog.show()
    }

    //
    //sync bar default view for offline model
    //
    private fun restoreNoConnectionView() {
        mNoConnectionView!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.noConnectionbg))
        base_view_offline_pin.setImageResource(R.mipmap.baseline_offline_pin_white_48dp)
        mNoConnectTv!!.visibility = View.VISIBLE

        updateBarText()

        mNoConnectTv!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.sysWhite))

        if (!mTapable && base_view_offline_info.visibility == View.VISIBLE) {
            base_view_offline_info.visibility = View.GONE
        } else {
            base_view_offline_info.visibility = View.VISIBLE
        }
        sync_main_loading_spinner.visibility = View.GONE
    }

    fun showNoDataView() {
        if (noDataView == null) return
        noDataView!!.visibility = View.VISIBLE
        base_view_no_data_sync_button.setOnClickListener {
            //fetch schedule.
            PreferenceHelper.getInstance(applicationContext).fetchScheduledDataNotify()
        }
    }

    fun hideNoDataViewIfShowing() {
        if (noDataView == null) return
        if (noDataView!!.visibility == View.VISIBLE) {
            noDataView!!.visibility = View.GONE
        }
    }

    fun removeSyncViewIfPossible() {
        if (!syncingInProgress && unsyncedChecks == 0 && unsyncedTourChecks ==0) {
            mNoConnectionView!!.visibility = View.GONE
        }
    }

    private fun updateCheckText() {
        if (unsyncedChecks ==1) {
            unsycedChecksTv.text = unsyncedChecks.toString() + " Zone Check"
        }else {
            unsycedChecksTv.text = unsyncedChecks.toString() + " Zone Checks"
        }
        if (unsyncedTourChecks==1){
            unsycedTourChecksTv.text = unsyncedTourChecks.toString() + " Site Tour"
        }else {
            unsycedTourChecksTv.text = unsyncedTourChecks.toString() + " Site Tours"
        }

        if (!mConnected) {
            sync_info_title.text = resources.getString(R.string.offline)
            sync_info_content.text = resources.getString(R.string.sync_info_content)
        } else {
            sync_info_title.text = resources.getString(R.string.sync_data)
            sync_info_content.text = resources.getString(R.string.sync_info_content_online)
        }
    }

    private fun updateSyncButton() {
        //need to check what to do with this button for offline and online
//        base_view_sync_button.isEnabled = activeConnection
        val time = PreferenceHelper.getInstance(applicationContext).lastSyncedTime
        if (time != null) {
            val str = time.replace("am", "AM").replace("pm", "PM")
            base_view_last_synced_time.text = str
        } else {
            base_view_last_synced_time.text = "N/A"
        }
    }
}