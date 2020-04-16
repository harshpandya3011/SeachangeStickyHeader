package com.seachange.healthandsafty.activity

import android.os.Bundle
import android.os.Handler
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.android.volley.VolleyError
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.adapter.ZoneCheckAdapter
import com.seachange.healthandsafty.application.SCApplication
import com.seachange.healthandsafty.helper.RequestErrorHandler
import com.seachange.healthandsafty.helper.View.RequestErrorHandlerView
import com.seachange.healthandsafty.model.*
import com.seachange.healthandsafty.presenter.CheckPresenter
import com.seachange.healthandsafty.utils.UtilStrings
import com.seachange.healthandsafty.view.CheckItemView
import kotlinx.android.synthetic.main.activity_zone_check.*
import org.parceler.Parcels
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ZoneCheckActivity : BaseActivity(), CheckItemView, RequestErrorHandlerView {

    private var mAdapter:ZoneCheckAdapter? =null
    private var mPresenter:CheckPresenter? =null
    private var mZoneCheckId:String ? = null
    private var mZoneCheck: ZoneFoundHazard ? =null
    private var errorHandler: RequestErrorHandler? = null
    private var mDBCheck: DBZoneCheckModel? = null
    private var mSiteZone : SiteZone? = null
    private var mScheduledTime: String ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zone_check)
        supportActionBar!!.hide()

        zone_check_back_btn.text = mCtx.resources.getString(R.string.fa_left_arrow)
        zone_check_back_btn.typeface = SCApplication.FontMaterial()
        zone_check_list.isNestedScrollingEnabled = false
        zone_check_list. layoutManager = LinearLayoutManager(this)

        if (intent.hasExtra(UtilStrings.ZONE_CHECK_ID)) {
            mZoneCheckId = intent.extras!!.getString(UtilStrings.ZONE_CHECK_ID)
        }

        if (intent.hasExtra(UtilStrings.DB_SITE_ZONE)) {
            mDBCheck = Parcels.unwrap<DBZoneCheckModel>(intent.getParcelableExtra(UtilStrings.DB_SITE_ZONE))
        }

        if (intent.hasExtra(UtilStrings.CHECK_TIME)) {
            mScheduledTime = intent.extras!!.getString(UtilStrings.CHECK_TIME)
        }

        if (intent.hasExtra(UtilStrings.SITE_ZONE)) {
            mSiteZone = Parcels.unwrap<SiteZone>(intent.getParcelableExtra(UtilStrings.SITE_ZONE))
        }

        zone_check_back_btn.setOnClickListener{
            closeActivity()
        }

        errorHandler = RequestErrorHandler(this, this, false, false)
        mPresenter = CheckPresenter(this, mCtx)

        //1.if there is connection and have valid zone check id, get data from server
        //2. if there is no connection and have data locally for the check show data from device
        //3. no data from device
        if (connected && mZoneCheckId != "null") {
            mPresenter!!.getData(mZoneCheckId!!)
            check_content.visibility= View.GONE
            zone_check_progressBar.visibility = View.VISIBLE
            initTitleHeader()
        } else if (mDBCheck !=null) {
            showDetailLocally()
        } else {
            check_content.visibility= View.GONE
            showNoDataDialog()
        }

        showNoConnectionDialog = true
    }

    private fun showNoDataDialog() {
        zone_check_back_btn.visibility = View.GONE
        if (!isLive) return
        //show error dialog...
        val builder = MaterialDialog.Builder(this)
                .content("Sorry, viewing this Zone Check information is not available while offline. \nPlease connect to the internet and try again.")
                .title("Not Available Offline")
                .icon(ContextCompat.getDrawable(this, R.mipmap.baseline_cloud_off_black_48dp)!!)
                .typeface(SCApplication.FontMaterial(), null)
                .titleColor(ContextCompat.getColor(applicationContext, R.color.alertTitle))
                .contentColor(ContextCompat.getColor(applicationContext, R.color.alertContent))
                .negativeText("Close")
                .onNegative { _, _ ->
                    closeActivity()
                }

        val connectionDialog = builder.build()
        connectionDialog.setCancelable(false)
        connectionDialog.show()
    }

    private fun closeActivity() {
        this.finish()
    }

    private fun showDetailLocally() {
        if(mDBCheck !=null) {
            check_content.visibility= View.VISIBLE
            check_zone_name.text = mSiteZone!!.zone_name
            if (mScheduledTime !=null) {
                check_scheduled_time.text = getScheduledDateString(mScheduledTime!!)
            }

            if (mDBCheck!!.startZoneCheckCommands !=null) {
                if (mDBCheck!!.startZoneCheckCommands.size > 0) {
                    check_start_time.text = getScheduledDateString(mDBCheck!!.startZoneCheckCommands[0].timeStarted!!)
                }
            }

            if (mDBCheck!!.completeZoneCheckCommand !=null) {
                check_finish_time.text = getScheduledDateString(mDBCheck!!.completeZoneCheckCommand.timeCompleted!!)
            }

            if (mDBCheck!!.startZoneCheckCommands !=null){
                if (mDBCheck!!.startZoneCheckCommands.size > 0) {
                    if (mDBCheck!!.completeZoneCheckCommand !=null) {
                        try {
                            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                            val dateStart =   sdf.parse(mDBCheck!!.startZoneCheckCommands[0].timeStarted!!)
                            val dateEnd = sdf.parse(mDBCheck!!.completeZoneCheckCommand.timeCompleted!!)

                            val millse = dateEnd.time - dateStart.time
                            val mills = Math.abs(millse)

                            val Hours = (mills / (1000 * 60 * 60)).toInt()
                            val Mins = (mills / (1000 * 60)).toInt() % 60
                            val Secs = ((mills / 1000).toInt() % 60).toLong()
                            var durationString = ""

                            if (Hours>0) {
                                durationString = "$Hours"+ "h " + "$Mins"+ "m "+ "$Secs"+ "s"
                            } else if(Mins>0) {
                                durationString =  "$Mins" + "m "+ "$Secs" + "s"
                            } else {
                                durationString =  "$Secs" + "s"
                            }
                            check_duration_time.text = durationString
                        }catch (e: ParseException) {

                        }
                    }
                }
            }

            if (mDBCheck!!.addressHazardCommandModelsV2!=null) {
                schedule_zone_child_hazards.text = Integer.toString(mDBCheck!!.addressHazardCommandModelsV2.size)
                val array: ArrayList<ZoneHazard> = ArrayList()
                for (tmp in mDBCheck!!.addressHazardCommandModelsV2!!) {
                    val zoneHazard = ZoneHazard()

                    val hazardType: HazardType = HazardType()
                    hazardType.type_name = tmp.typeName
                    hazardType.category = tmp.categoryName
                    zoneHazard.hazardType = hazardType
                    zoneHazard.dateTimeIdentified = tmp.timeFound
                    array.add(zoneHazard)
                }
                mAdapter = ZoneCheckAdapter(array)
                zone_check_list.adapter = mAdapter
            }
        }
    }

    private fun getScheduledDateString(timeScheduled: String): String {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val format = SimpleDateFormat("EEE, dd MMM yyyy, HH:mm", Locale.getDefault())
            if (timeScheduled !=null) {
                val date = sdf.parse(timeScheduled)
                return format.format(date)
            } else {
                return ""
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }

    override fun getResponse(zoneFoundHazard: ZoneFoundHazard) {
        Handler().postDelayed({
            zone_check_progressBar.visibility = View.GONE
            mZoneCheck = zoneFoundHazard
            init(zoneFoundHazard.identifiedHazards!!)
            initHeader()
            initTime()
        check_content.visibility= View.VISIBLE

        }, 500)
    }

    override fun errorReceived(error: VolleyError?) {
        zone_check_progressBar.visibility = View.GONE
        if (!isLive) return

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val strDate = sdf.parse("25/08/2018")

        if (Date().before(strDate)) {
            check_no_data_error.visibility = View.VISIBLE
        }

        if(error !=null) {
            errorHandler!!.onErrorResponse(error)
        }
    }

    override fun errorDialogPositiveClicked() {
         //To change body of created functions use File | Settings | File Templates.
    }

    override fun errorDialogNegativeClicked() {
         //To change body of created functions use File | Settings | File Templates.
    }

    private fun initHeader() {
        check_zone_name.text = mZoneCheck!!.zoneName
        check_scheduled_time.text = mZoneCheck!!.getScheduledDateString()
    }

    private fun initTitleHeader() {
        check_zone_name.text = mSiteZone!!.zone_name
    }

    private fun initTime() {
        check_start_time.text = mZoneCheck!!.getStartDateString()
        check_finish_time.text = mZoneCheck!!.getCompleteDateString()
        check_duration_time.text = mZoneCheck!!.getDurationString()
    }

    private fun init(array: ArrayList<ZoneHazard>) {
        schedule_zone_child_hazards.text = Integer.toString(mZoneCheck!!.numOfHazardsIdentified!!)
        mAdapter = ZoneCheckAdapter(array)
        zone_check_list.adapter = mAdapter
    }
}
