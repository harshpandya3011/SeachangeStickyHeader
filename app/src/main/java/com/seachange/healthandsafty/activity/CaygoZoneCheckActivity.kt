package com.seachange.healthandsafty.activity

import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.*
import com.google.android.material.snackbar.Snackbar
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.Chronometer
import com.afollestad.materialdialogs.MaterialDialog
import com.microsoft.appcenter.analytics.Analytics
import kotlinx.android.synthetic.main.activity_caygo_tour_main.*
import org.parceler.Parcels
import java.text.SimpleDateFormat
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.application.SCApplication
import com.seachange.healthandsafty.helper.*
import com.seachange.healthandsafty.utils.UtilStrings
import com.seachange.healthandsafty.view.ForegroundBackgroundView
import com.seachange.healthandsafty.view.ZoneCheckView
import java.util.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.seachange.healthandsafty.db.DBCheck
import com.seachange.healthandsafty.db.DBCheckViewModel
import com.seachange.healthandsafty.helper.tourcheck.DBTourCheckManager
import com.seachange.healthandsafty.helper.tourcheck.DBTourCheckManagerView
import com.seachange.healthandsafty.model.*
import com.seachange.healthandsafty.nfc.ui.NFCMainActivity
import com.seachange.healthandsafty.utils.Util


class CaygoZoneCheckActivity : BaseActivity(), View.OnClickListener, Observer, ZoneCheckView, ForegroundBackgroundView, DBTourCheckManagerView {

    override fun allTourChecksInDB(mList: ArrayList<DBTourCheckModel>?) {

    }

    override fun getTourCheckByIdInDB(mTour: DBTourCheckModel?) {

    }

    private var pausedEnabled = true

    override fun backForeground() {
        Logger.info("app in foreground")
        if (!pausedEnabled) {
            val dbCheckResume = DBCheckResume()
            dbCheckResume.timeResumed = getDateString()
            if (!isTourCheck) {
                mDBChecker!!.onAddResumeEvent(dbCheckResume)
            } else {
                for (i in mCurrentDBTourCheck!!.zoneChecks.indices) {
                    if (mCurrentDBTourCheck!!.zoneChecks[i].zoneId == mZoneId) {
                        mCurrentDBTourCheck!!.zoneChecks[i].addResumeZoneCheckCommands(dbCheckResume)
                    }
                }
                mDBTourCheckHelper!!.saveCurrentTourCheck(mCurrentDBTourCheck)
            }
            Logger.info("app in foreground call resume")

        }
        pausedEnabled = true
    }

    override fun wentBackground() {
        if (pausedEnabled) {
            mCurrentZone!!.status = 3
            mCurrentZone!!.zoneCheckId = mZoneCheckId
            val dbCheckPause = DBCheckPause()

            if (!isTourCheck) {
                dbCheckPause.timePaused = getDateString()
                mDBChecker!!.onAddPauseEvent(dbCheckPause)
            } else {
                for (i in mCurrentDBTourCheck!!.zoneChecks.indices) {
                    if (mCurrentDBTourCheck!!.zoneChecks[i].zoneId == mZoneId) {
                        mCurrentDBTourCheck!!.zoneChecks[i].addPauseZoneCheckCommands(dbCheckPause)
                    }
                }
            }
            pausedEnabled = false
        }
        Logger.info("app in background")
    }

    private val networkChangeReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            val status = NetworkUtil.getConnectivityStatusString(context)
            if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.action)) {
                if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
                    Logger.info("connection lost" + " Network connectivity change")
                } else {
                    Logger.info("connection active" + " Network connectivity change")
                }
            }
        }
    }

    private var mChronometer: Chronometer? = null
    private var mZoneId = 1
    private var mCurrentZone: SiteZone? = null
    private var managerTour: ManagerTour? = null
    private var mTourName: String? = null
    private var mZoneChecker: ZoneCheckHelper? = null
    private var mZonePrefs: CheckPreference? = null
    private var mZoneCheckId: String? = null
    private var caygoSite: CaygoSite? = null
    private var mCheckTimeId: String? = null
    private var mCheckTime: String? = null
    private var isTourCheck: Boolean = false
    private var tourCheckId: String? = ""
    private var qrCodeDialogShowing: Boolean = false
    private var appStateListener: ForegroundBackgroundListener? = null
    private var mDBChecker: DBZoneCheckHelper? = null
    private var checkTime: CheckTime? = null
    private var qrCodeId: String? = null
    private var checkStarted = false
    private var mDBTourCheckHelper: DBTourCheckHelper? = null
    private var mCurrentDBTourCheck: DBTourCheckModel? = null
    private var mDBTourCheckManager: DBTourCheckManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_caygo_tour)
        supportActionBar!!.hide()
        PreferenceHelper.getInstance(mCtx).resetCurrentHazards()

        appStateListener = ForegroundBackgroundListener(this)
        mDBChecker = DBZoneCheckHelper(mCtx)

        ProcessLifecycleOwner.get()
                .lifecycle
                .addObserver(appStateListener!!)

        mChronometer = findViewById<View>(R.id.chronometer) as Chronometer
        caygoSite = PreferenceHelper.getInstance(mCtx).siteData

        initIntentData()

        managerTour = ManagerTour(mCtx, mTourName)
        mChronometer!!.base = SystemClock.elapsedRealtime()
        mChronometer!!.start()

        initButtons()
        this.setDateAndTime()

        if (intent.hasExtra(UtilStrings.SITE_ZONE)) {
            mCurrentZone = Parcels.unwrap<SiteZone>(intent.getParcelableExtra<Parcelable>(UtilStrings.SITE_ZONE))
            initZoneId(mCurrentZone!!.zone_id)
            tour_zone!!.text = mCurrentZone!!.zone_name
            mCurrentZone!!.isChecked = true
//            managerTour!!.updateZones(mCurrentZone)
        }
        mZonePrefs = CheckPreference(mCtx)
        mZoneChecker = ZoneCheckHelper(mCtx)
        getCurrentHazards()

        mDBTourCheckManager = DBTourCheckManager(this, this)

        if (isTourCheck) {
            mDBTourCheckHelper = DBTourCheckHelper(mCtx)
            mCurrentDBTourCheck = mDBTourCheckHelper!!.currentTourCheck
            checkStarted = true
            startTourCheck(1)
        } else {
            startCheck(1)
        }
        HazardObserver.getInstance().isCheckEnded = false

        showNoConnectionBaseView = true
        setTapble(false)

    }

    private fun initButtons() {
        add_hazard!!.setOnClickListener(this)
        end_tour!!.setOnClickListener(this)
        view_hazard_close!!.setOnClickListener(this)
        view_hazard_close!!.text = mCtx.getString(R.string.fa_close_circle)
        view_hazard_close!!.typeface = SCApplication.FontMaterial()
    }

    private fun initIntentData() {
        mTourName = if (intent.hasExtra(UtilStrings.TOUR_NAME)) {
            intent.extras!!.getString(UtilStrings.TOUR_NAME)
        } else {
            "caygo_tour"
        }

        qrCodeId = intent.getStringExtra(UtilStrings.QRCODE)

        if (intent.hasExtra(UtilStrings.CHECK_TIME_ID)) {
            mCheckTimeId = intent.extras!!.getString(UtilStrings.CHECK_TIME_ID)
        }

        if (intent.hasExtra(UtilStrings.CHECK_TIME)) {
            mCheckTime = intent.extras!!.getString(UtilStrings.CHECK_TIME)
        }

        isTourCheck = intent.extras!!.getBoolean(UtilStrings.TOUR_CHECK)

        if (intent.hasExtra(UtilStrings.SITE_TOUR_ID)) {
            tourCheckId = intent.extras!!.getString(UtilStrings.SITE_TOUR_ID)
        }
    }

    override fun onResume() {
        super.onResume()
        HazardObserver.getInstance().addObserver(this)

        //register network receiver
        val intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeReceiver, intentFilter)

        if (isTourCheck) {
            mCurrentDBTourCheck = mDBTourCheckHelper!!.currentTourCheck
            val gson = Gson()
            Logger.info("DB SITE TOUR: check home screen" + gson.toJson(mCurrentDBTourCheck))
        }
    }

    override fun onPause() {
        super.onPause()
        //unregister network receiver
        unregisterReceiver(networkChangeReceiver)
    }

    override fun onStop() {
        super.onStop()
        //
        //double check this...
        //
        if (!isTourCheck) {
            if (mDBChecker != null) {
                if (mDBChecker!!.getmStartList().size == 0) {
                    val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    val current = Calendar.getInstance().time
                    val time = df.format(current)
                    val dbStart = DBCheckStart()
                    dbStart.qrCodeId = qrCodeId
                    dbStart.timeStarted = time
                    mDBChecker!!.onAddStartEvent(dbStart)
                    Logger.info("check start:  not start need to add start")
                }
                Logger.info("check start:  not tour")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        HazardObserver.getInstance().deleteObserver(this)
        ProcessLifecycleOwner.get()
                .lifecycle
                .removeObserver(appStateListener!!)

        if (!isTourCheck) {
            if (!mCurrentZone!!.isChecked) {
                insertCheckToDBAndReset()
            }
            mDBChecker!!.resetDBCheck()
        }
    }

    private fun insertCheckToDBAndReset() {

        if (isTourCheck) {
            mDBChecker!!.resetDBCheck()

        } else {
//        mDBChecker = DBZoneCheckHelper(mCtx)
            Logger.info("DB -> Save this to DB " + mDBChecker!!.checkForDB)

            //
            //only insert if check is not saved already. save it if it is normal zone check...
            //if there is check in db, only update, not to save a new one...
            //
            mApplication.insertCheck(DBCheck(mDBChecker!!.checkForDB,false))
            mDBChecker!!.resetDBCheck()
        }
    }

    private fun startCheck(tmp: Int) {
        when {
            mCurrentZone!!.status == 0 -> {
                mZoneCheckId = UUID.randomUUID().toString()
                Logger.infoZoneCheck("start with status: " + mCurrentZone!!.status)
            }
            mCurrentZone!!.status == 4 -> {
                mZoneCheckId = mCurrentZone!!.zoneCheckId
                Logger.infoZoneCheck("start with status: " + mCurrentZone!!.status)
            }
            else -> {
                mZoneCheckId = mCurrentZone!!.zoneCheckId
                Logger.infoZoneCheck("resume with status: " + mCurrentZone!!.status)
            }
        }
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val current = Calendar.getInstance().time
        val time = df.format(current)

        if (intent.hasExtra(UtilStrings.SCHEDULED_CHECK_TIME)) {
            checkTime = Parcels.unwrap<CheckTime>(intent.getParcelableExtra(UtilStrings.SCHEDULED_CHECK_TIME))
            checkTime!!.processCheckTime()
            var validCheckTime = false
            if (checkTime!!.status == 2) {
                validCheckTime = true
            }
            if (!validCheckTime && mCurrentZone!!.status == 0) {
                Analytics.trackEvent(UtilStrings.LOG_ZONE_CHECK_START + "wrong id: " + mCheckTime + " -> current time: " + time + " / Zone Check time id: " + mZoneCheckId)
                Handler().postDelayed({
                    cantStart()
                }, 500)
                return
            } else {
                Logger.info(UtilStrings.LOG_ZONE_CHECK_START + mCheckTime + " -> current time: " + time + " / Zone Check time id: " + mZoneCheckId)
            }
        }

        mZonePrefs!!.saveZoneUUID(mZoneCheckId)

        if (mCheckTime != null) {
            Analytics.trackEvent(UtilStrings.LOG_ZONE_CHECK_START + mCheckTime + " -> current time: " + time + " / Zone Check time id: " + mZoneCheckId)
        } else {
            Analytics.trackEvent(UtilStrings.LOG_ZONE_CHECK_START + " no schedule check time id " + " -> current time: " + time + " / Zone Check time id: " + mZoneCheckId)
        }

        Logger.info("""$mZoneCheckId Date: $time""")

        addStartToDB()
    }

    private fun addStartToDB() {

        //get current time
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val current = Calendar.getInstance().time
        val time = df.format(current)

        //create start data to save to db
        val hashMap = HashMap<String, String>()
        hashMap["groupId"] = caygoSite!!.groupId.toString()
        hashMap["zoneCheckId"] = mZoneCheckId!!
        hashMap["zoneId"] = mZoneId.toString()

        if (isTourCheck) {
            hashMap["siteTourId"] = tourCheckId!!
        } else {
            hashMap["scheduledZoneCheckTimeId"] = mCheckTimeId!!
        }

        //add check first, need will be override
        mDBChecker?.onStartCheck(hashMap)


        val mWordViewModel = ViewModelProviders.of(this).get(DBCheckViewModel::class.java!!)
        var dbRecieved = false
        mWordViewModel.allChecks.observe(this, androidx.lifecycle.Observer { checks ->

            //
            // Update the cached copy of the words in the adapter.
            //checks is the list of the models in db
            // arraylist if the list of saved checks in db
            // mDBList is list of DBZoneCheck Model parsed
            //after find the zone check exist, use it as current, delete that check from db

            if (dbRecieved) {

                //db has retrieved...
            } else if (isTourCheck) {

                //tour check do nothing at moment...
            } else {

                dbRecieved = true
                val arrayList = ArrayList<String>()
                for (i in checks!!.indices) {
                    arrayList.add(checks.get(i).zoneCheck)
                }
                val gson = Gson()
                val updatedList = checks
                val mDBList = mDBChecker?.getUnsyncedChecks(arrayList.toString()).orEmpty()
                for (tmp in mDBList) {
                    if (tmp.isCurrentZoneCheck(mZoneId.toString(), checkTime!!.check_id)) {

//                    reset current check...
                        hashMap["zoneCheckId"] = tmp.zoneCheckId
                        val checkStr = gson.toJson(tmp)
                        mDBChecker?.resetInitCheckObject(checkStr)
                        mDBChecker = DBZoneCheckHelper(mCtx)
                        mCurrentZone!!.status = 4
                        Logger.info("DBBUILDER: existing : " + checkStr)

                        for (i in updatedList.indices) {

                            val type = object : TypeToken<DBZoneCheckModel>() {}.type
                            val zoneCheck = gson.fromJson<DBZoneCheckModel>(updatedList.get(i).zoneCheck, type)

                            if (zoneCheck != null && zoneCheck.zoneCheckId != null && tmp.zoneCheckId != null) {
                                if (zoneCheck.zoneCheckId.equals(tmp.zoneCheckId)) {
                                    mWordViewModel.delete(checks.get(i))
                                    Logger.info("DBBUILDER: existing : remove from db " + gson.toJson(tmp))
                                }
                            }
                        }
                    }
                }

                Logger.info("BUILDER: cure:  " + mCurrentZone!!.status)
                if (mCurrentZone!!.status == 0) {
                    val dbStart = DBCheckStart()
                    dbStart.qrCodeId = qrCodeId
                    dbStart.timeStarted = time
                    mDBChecker?.onAddStartEvent(dbStart)
                } else if (mDBChecker?.getmStartList()?.size == 0) {
                    val dbStart = DBCheckStart()
                    dbStart.qrCodeId = qrCodeId
                    dbStart.timeStarted = time
                    mDBChecker?.onAddStartEvent(dbStart)
                } else {
                    val dbCheckResume = DBCheckResume()
                    dbCheckResume.timeResumed = getDateString()
                    mDBChecker?.onAddResumeEvent(dbCheckResume)
                    Logger.info("DBBUILDER: add resume " + mDBChecker?.checkForDB)
                }
                Logger.info("DBBUILDER: " + mDBChecker?.checkForDB)
            }
        })
    }

    //this is for old tour zone checks...
    private fun startTourCheck(tmp: Int) {
        when {
            mCurrentZone!!.status == 0 -> {
                mZoneCheckId = UUID.randomUUID().toString()
                Logger.infoZoneCheck("start with status: " + mCurrentZone!!.status)
            }
            mCurrentZone!!.status == 4 -> {
                mZoneCheckId = mCurrentZone!!.zoneCheckId
                Logger.infoZoneCheck("start with status: " + mCurrentZone!!.status)
            }
            else -> {
                mZoneCheckId = mCurrentZone!!.zoneCheckId
                Logger.infoZoneCheck("resume with status: " + mCurrentZone!!.status)
            }
        }
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val current = Calendar.getInstance().time
        val time = df.format(current)

        if (intent.hasExtra(UtilStrings.SCHEDULED_CHECK_TIME)) {
            checkTime = Parcels.unwrap<CheckTime>(intent.getParcelableExtra(UtilStrings.SCHEDULED_CHECK_TIME))
            checkTime!!.processCheckTime()
            var validCheckTime = false
            if (checkTime!!.status == 2) {
                validCheckTime = true
            }
            if (!validCheckTime && mCurrentZone!!.status == 0) {
                Analytics.trackEvent(UtilStrings.LOG_ZONE_CHECK_START + "wrong id: " + mCheckTime + " -> current time: " + time + " / Zone Check time id: " + mZoneCheckId)
                Handler().postDelayed({
                    cantStart()
                }, 500)
                return
            } else {
                Logger.info(UtilStrings.LOG_ZONE_CHECK_START + mCheckTime + " -> current time: " + time + " / Zone Check time id: " + mZoneCheckId)
            }
        }

        mZonePrefs!!.saveZoneUUID(mZoneCheckId)

        if (mCheckTime != null) {
            Analytics.trackEvent(UtilStrings.LOG_ZONE_CHECK_START + mCheckTime + " -> current time: " + time + " / Zone Check time id: " + mZoneCheckId)
        } else {
            Analytics.trackEvent(UtilStrings.LOG_ZONE_CHECK_START + " no schedule check time id " + " -> current time: " + time + " / Zone Check time id: " + mZoneCheckId)
        }

        Logger.info("""$mZoneCheckId Date: $time""")
        addCheckToTour()
    }

    private fun addCheckToTour() {
        //get current time
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val current = Calendar.getInstance().time
        val time = df.format(current)

        //create start data to save to db
        val hashMap = HashMap<String, String>()
        hashMap["groupId"] = caygoSite!!.groupId.toString()
        hashMap["zoneCheckId"] = mZoneCheckId!!
        hashMap["zoneId"] = mZoneId.toString()
        hashMap["siteTourId"] = tourCheckId!!

        val tmpModel = mCurrentDBTourCheck!!.getExistingTourCheck(mZoneId)
        if (tmpModel == null) {

            //
            //new tour zone check add to site tour object in db
            //

            val mDBZoneCheckModel = DBZoneCheckModel()
            mDBZoneCheckModel.groupId = caygoSite!!.groupId
            mDBZoneCheckModel.zoneCheckId = mZoneCheckId!!
            mDBZoneCheckModel.zoneId = mZoneId
            mDBZoneCheckModel.siteTourId = tourCheckId!!

            if (mCurrentZone!!.status == 0) {
                if (mDBZoneCheckModel != null) {

                    val dbStart = DBCheckStart()
                    dbStart.qrCodeId = qrCodeId
                    dbStart.timeStarted = time
                    mDBZoneCheckModel.addStartZoneCheckCommands(dbStart)
                }
            } else {
                val dbCheckResume = DBCheckResume()
                dbCheckResume.timeResumed = getDateString()
                mDBZoneCheckModel.addResumeZoneCheckCommands(dbCheckResume)
                Logger.info("DBBUILDER: add resume " + mDBChecker!!.checkForDB)
            }

            if(mDBZoneCheckModel.startZoneCheckCommands == null) {
                val dbStart = DBCheckStart()
                dbStart.qrCodeId = qrCodeId
                dbStart.timeStarted = time
                mDBZoneCheckModel.addStartZoneCheckCommands(dbStart)
            } else if(mDBZoneCheckModel.startZoneCheckCommands.size == 0) {
                val dbStart = DBCheckStart()
                dbStart.qrCodeId = qrCodeId
                dbStart.timeStarted = time
                mDBZoneCheckModel.addStartZoneCheckCommands(dbStart)
            }

            mCurrentDBTourCheck!!.zoneChecks.add(mDBZoneCheckModel)


        } else {

            //assign status if this is in db
            mCurrentZone!!.status = 4

            if (mCurrentZone!!.status == 0) {
                val dbStart = DBCheckStart()
                dbStart.qrCodeId = qrCodeId
                dbStart.timeStarted = time
                tmpModel.addStartZoneCheckCommands(dbStart)

            } else if (tmpModel.startZoneCheckCommands == null) {

                val dbStart = DBCheckStart()
                dbStart.qrCodeId = qrCodeId
                dbStart.timeStarted = time
                tmpModel.addStartZoneCheckCommands(dbStart)
            } else if (tmpModel!!.startZoneCheckCommands.size == 0) {
                val dbStart = DBCheckStart()
                dbStart.qrCodeId = qrCodeId
                dbStart.timeStarted = time
                tmpModel.addStartZoneCheckCommands(dbStart)
            } else {
                val dbCheckResume = DBCheckResume()
                dbCheckResume.timeResumed = getDateString()
                tmpModel.addResumeZoneCheckCommands(dbCheckResume)

                Logger.info("DB SITE TOUR: add resume " + mDBChecker!!.checkForDB)
            }

            if(tmpModel.startZoneCheckCommands == null) {
                val dbStart = DBCheckStart()
                dbStart.qrCodeId = qrCodeId
                dbStart.timeStarted = time
                tmpModel.addStartZoneCheckCommands(dbStart)
            }else if (tmpModel!!.startZoneCheckCommands.size == 0) {
                val dbStart = DBCheckStart()
                dbStart.qrCodeId = qrCodeId
                dbStart.timeStarted = time
                tmpModel.addStartZoneCheckCommands(dbStart)
            }

            for (i in mCurrentDBTourCheck!!.zoneChecks.indices) {
                if (mCurrentDBTourCheck!!.zoneChecks[i].zoneId == tmpModel.zoneId) {
                    mCurrentDBTourCheck!!.zoneChecks[i] = tmpModel
                }
            }
        }

        mDBTourCheckHelper!!.saveCurrentTourCheck(mCurrentDBTourCheck)
        mDBTourCheckManager!!.updateOrEnterTourChecks(mCurrentDBTourCheck!!, mApplication!!)
    }


    //end for old tour zone checks...


    private fun cantStart() {

        val tmp = "We're sorry. You can not start this Zone Check because the time period has elapsed.\n\n" +
                "Please select the Zone again for the current time."

        val builder = MaterialDialog.Builder(this)
                .content(tmp)
                .title("Time Elapsed")
                .titleColor(ContextCompat.getColor(mCtx, R.color.alertTitle))
                .contentColor(ContextCompat.getColor(mCtx, R.color.alertTitle))
                .positiveText("Start Again")
                .positiveColor(ContextCompat.getColor(mCtx, R.color.colorDefaultYellow))
                .onPositive { _, _ ->
                    this.finish()
                }

        val mDialog = builder.build()
        mDialog.setCancelable(false)
        mDialog.show()
    }

    private fun initZoneId(zId: Int) {
        this.mZoneId = zId
    }

    private fun setDateAndTime() {
        val df = SimpleDateFormat("EEE, d MMM yyyy, HH:mm", Locale.getDefault())
        tour_time!!.text = df.format(Calendar.getInstance().time)
    }

    fun getDateString(): String {
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return df.format(Calendar.getInstance().time)
    }

    private fun getCurrentHazards() {
        val hazards = PreferenceHelper.getInstance(mCtx).currentHazards
        tour_hazard_count!!.text = Integer.toString(hazards)
    }

    override fun onClick(view: View) {

        when (view.id) {
            R.id.add_hazard -> {
                goAddHazard()
            }

            R.id.end_tour -> {
                showScanPopUp()
            }

            R.id.view_hazard_close -> {

                val dbCancel = DBCheckCancel()
                dbCancel.timeCancelled = getDateString()
                mDBChecker!!.onAddCancelEvent(dbCancel)

                if (isTourCheck) {
                    for (i in mCurrentDBTourCheck!!.zoneChecks.indices) {
                        if (mCurrentDBTourCheck!!.zoneChecks[i].zoneId == mZoneId) {
                            mCurrentDBTourCheck!!.zoneChecks[i].addCancelZoneCheckCommands(dbCancel)
                        }
                    }
                    mDBTourCheckHelper!!.saveCurrentTourCheck(mCurrentDBTourCheck)
                }

                if (PreferenceHelper.getInstance(mCtx).currentHazards > 0) {
                    showScanPopUp()
                } else {
                    if (mCurrentZone != null) {
                        mCurrentZone!!.isChecked = false
                        mCurrentZone!!.status = 4
                        managerTour!!.updateZones(mCurrentZone)
                    }
//                    mDBChecker!!.resetDBCheck()

                    this.finish()
                }
            }
        }
    }

    private fun showScanPopUp() {

         if (caygoSite!!.siteTypeNFC(mCtx)) {
            scanNFCToEndCheck()
         } else {
             scanQRCode()
         }
    }

    private fun scanQRCode() {
        val builder = MaterialDialog.Builder(this)
                .title("Scan to End")
                .content("Scan QR Code to End Zone Check")
                .positiveText("OK")
                .negativeText("Cancel")
                .onPositive { _, _ ->
                    scanQRCodeEndCheck()
                }

        val dialog = builder.build()
        dialog.show()
    }

    private fun scanQRCodeEndCheck() {
        HazardObserver.getInstance().isCheckEndedFailed = false
        val intent = Intent(this, QRCodeCaptureActivity::class.java)
        intent.putExtra(UtilStrings.SCAN_LEVEL, 2)
        intent.putExtra(UtilStrings.ZONE_ID, mZoneId)
        intent.putExtra(UtilStrings.SITE_ZONE, Parcels.wrap<SiteZone>(mCurrentZone))
        intent.putExtra(UtilStrings.SITE_TOUR_ID, tourCheckId)
        intent.putExtra(UtilStrings.CHECK_TIME_ID, mCheckTimeId)
        intent.putExtra(UtilStrings.TOUR_NAME, mTourName)
        intent.putExtra(UtilStrings.TOUR_CHECK, isTourCheck)
        var lastTourCheck = false
        if (getIntent().hasExtra(UtilStrings.TOUR_LAST_CHECK)) {
            lastTourCheck = getIntent().extras!!.getBoolean(UtilStrings.TOUR_LAST_CHECK)
        }
        if (isTourCheck) {
            intent.putExtra(UtilStrings.TOUR_LAST_CHECK, lastTourCheck)
        }
        startActivity(intent)
    }

    private fun scanNFCToEndCheck() {

        HazardObserver.getInstance().isCheckEndedFailed = false
        val intent = Intent(mCtx, NFCMainActivity::class.java)
        intent.putExtra(UtilStrings.NFC_TYPE, 4)
        intent.putExtra(UtilStrings.SCAN_LEVEL, 2)
        intent.putExtra(UtilStrings.ZONE_ID, mZoneId)
        intent.putExtra(UtilStrings.SITE_ZONE, Parcels.wrap<SiteZone>(mCurrentZone))
        intent.putExtra(UtilStrings.SITE_TOUR_ID, tourCheckId)
        intent.putExtra(UtilStrings.CHECK_TIME_ID, mCheckTimeId)
        intent.putExtra(UtilStrings.TOUR_NAME, mTourName)
        intent.putExtra(UtilStrings.TOUR_CHECK, isTourCheck)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        var lastTourCheck = false
        if (getIntent().hasExtra(UtilStrings.TOUR_LAST_CHECK)) {
            lastTourCheck = getIntent().extras!!.getBoolean(UtilStrings.TOUR_LAST_CHECK)
        }
        if (isTourCheck) {
            intent.putExtra(UtilStrings.TOUR_LAST_CHECK, lastTourCheck)
        }
        startActivity(intent)
    }



    private fun goAddHazard() {
        val intent = Intent(this, HazardSourceActivity::class.java)
        intent.putExtra(UtilStrings.ZONE_ID, mZoneId)
        intent.putExtra(UtilStrings.SITE_ZONE, Parcels.wrap<SiteZone>(mCurrentZone))
        intent.putExtra(UtilStrings.SITE_TOUR_ID, tourCheckId)
        intent.putExtra(UtilStrings.CHECK_TIME_ID, mCheckTimeId)
        intent.putExtra(UtilStrings.TOUR_NAME, mTourName)
        intent.putExtra(UtilStrings.TOUR_CHECK, isTourCheck)
        startActivity(intent)
    }

    override fun update(p0: Observable?, p1: Any?) {
        val observer = p0 as HazardObserver
        if (observer.isCheckEnded) {
            endCheck()
        } else if (observer.isCheckEndedFailed) {
            //show dialog
            if (qrCodeDialogShowing) return

            var title = "Incorrect QR Code"
            var message = "Please scan a QR Code for the Zone you select."
            if (caygoSite!!.siteTypeNFC(mCtx)) {
                title = "Incorrect NFC Tag"
                message = "Please tap an NFC tag for the Zone you selected."
            }

            val builder = MaterialDialog.Builder(this)
                    .title(title)
                    .content(message)
                    .positiveText("Try Again")
                    .onPositive { dialog, _ ->
                        dialog.dismiss()
                        qrCodeDialogShowing = false
                    }

            val dialog = builder.build()
            dialog.show()
            qrCodeDialogShowing = true
            HazardObserver.getInstance().isQrCodeFailed = false

        } else if (observer.isHazardChanged) {
            Handler().postDelayed({
                updateHazards()
                Snackbar.make(findViewById(R.id.tour_main), "Hazard Added", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
            }, 500)
        }
    }

    private fun updateHazards() {
        val hazards = PreferenceHelper.getInstance(mCtx).currentHazards
        tour_hazard_count!!.text = Integer.toString(hazards)
        if (mCurrentZone != null) {
            mCurrentZone!!.hazards = hazards
            mCurrentZone!!.zoneCheckId = mZoneCheckId
            val tmp = mCurrentZone
            tmp!!.isChecked = false
            managerTour!!.updateZones(tmp)
        }
    }

    override fun onBackPressed() {
        if (PreferenceHelper.getInstance(mCtx).currentHazards > 0) {
            showScanPopUp()
        } else {
            super.onBackPressed()
        }
    }

    private fun endCheck() {
        //update hazards for current zone...
        mCurrentZone!!.hazards = PreferenceHelper.getInstance(mCtx).currentHazards
        PreferenceHelper.getInstance(mCtx).resetCurrentHazards()
        if (!isTourCheck) {
            mCurrentZone!!.isChecked = true
        } else {
            //set tour zone check synced with server.
            //server side may not finished process data
            //synced status need the reflected on the client side

            if(connected) {
                mCurrentDBTourCheck = mDBTourCheckHelper!!.currentTourCheck
                for (i in mCurrentDBTourCheck!!.zoneChecks.indices) {
                    if (mCurrentDBTourCheck!!.zoneChecks[i].zoneId == mZoneId) {
                        mCurrentDBTourCheck!!.zoneChecks[i].isSync = true
                    }
                }

                mDBTourCheckHelper!!.saveCurrentTourCheck(mCurrentDBTourCheck)
                mDBTourCheckManager!!.updateOrEnterTourChecks(mCurrentDBTourCheck!!, mApplication!!)
            }
        }
        mCurrentZone!!.zoneCheckId = mZoneCheckId

        if (caygoSite!!.checkSettings != null) {
            if (caygoSite!!.checkSettings.intervalInMinutes != null) {
                val validCheckTime = Util().zoneCheckDate(caygoSite!!.checkSettings.intervalInMinutes, mCheckTime)
                if (validCheckTime) {
                    Logger.info("fetch data -> " + "same check don't need to refresh data")
                } else {
                    PreferenceHelper.getInstance(mCtx).homeScreenRefresh = true
                    Logger.info("fetch data -> " + "need to refresh data ")
                }
            }
        } else {
            if (checkTime == null) {
                Logger.info("fetch data -> " + "site tour but has not settings....... ")
            } else {
                checkTime!!.processCheckTime()
                if (checkTime!!.status == 3) {
                    PreferenceHelper.getInstance(mCtx).homeScreenRefresh = true
                }
            }
        }

        PreferenceHelper.getInstance(mCtx).setZoneCheckedInScheduleInPrefs(checkTime, mCurrentZone)

        this.finish()
        val seconds = (SystemClock.elapsedRealtime() - mChronometer!!.base) / 1000
        Analytics.trackEvent("Caygo Check took : $seconds seconds")
        mZonePrefs!!.saveTimeSpendOnZoneCheck(seconds)
    }
}
