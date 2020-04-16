package com.seachange.healthandsafty.nfc.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.VolleyError
import com.google.gson.Gson

import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.activity.BaseActivity
import com.seachange.healthandsafty.adapter.NFCSetupListAdapter
import com.seachange.healthandsafty.application.SCApplication
import com.seachange.healthandsafty.helper.Logger
import com.seachange.healthandsafty.helper.PreferenceHelper
import com.seachange.healthandsafty.model.CaygoSite
import com.seachange.healthandsafty.model.SiteZone
import com.seachange.healthandsafty.nfc.helper.NFCSetUpHelper
import com.seachange.healthandsafty.nfc.helper.NFCSyncPresenter
import com.seachange.healthandsafty.nfc.model.NFCZoneData
import com.seachange.healthandsafty.nfc.view.NFCListener
import com.seachange.healthandsafty.nfc.view.NFCSyncView
import com.seachange.healthandsafty.nfconboard.NFCOnBoardingFinishedSetUpActivity
import com.seachange.healthandsafty.utils.UtilStrings
import kotlinx.android.synthetic.main.activity_nfcset_up.*
import org.parceler.Parcels
import java.util.ArrayList


class NFCSetUpActivity : BaseActivity() , NFCListener , NFCSyncView{

    override fun onDialogDisplayed() {
        
    }

    override fun onDialogDismissed() {
    }

    private var mCaygoSite: CaygoSite? = null
    private var mZones: ArrayList<SiteZone>? = null
    private var mAdapter: NFCSetupListAdapter ? = null
    private var mListener: OnSelectedZoneListener? = null
    private var mNFCSetUpHelper: NFCSetUpHelper? = null
    private var mUnsyncedZones: ArrayList<NFCZoneData>? = null
    private var mPresenter: NFCSyncPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfcset_up)
        mNFCSetUpHelper = NFCSetUpHelper(mCtx)
        mPresenter = NFCSyncPresenter(mCtx, this)
        setupActionBar()
        initView()
        initData()
        initProgress()
    }

    private fun setupActionBar() {
        setSupportActionBar(nfc_toolbar)
        supportActionBar?.title = "Setup NFC Tags"
        supportActionBar?.run {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun initView() {
        nfc_setup_back_btn.text = mCtx.resources.getString(R.string.fa_left_arrow)
        nfc_setup_back_btn.typeface = SCApplication.FontMaterial()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            nfc_setup_zones_list.isNestedScrollingEnabled = false
        }
        nfc_setup_zones_list. layoutManager = LinearLayoutManager(this)
        nfc_setup_back_btn.setOnClickListener {
            this.finish()
        }

        nfc_setup_read_button.setOnClickListener {
            val intent = Intent(mCtx, NFCMainActivity::class.java)
            intent.putExtra(UtilStrings.NFC_TYPE, 1)
            startActivity(intent)
        }

        nfc_setup_sync_button.setOnClickListener {
            //send zone tags data to server.
            for (zone in mZones!!) {
                if(zone.isNfcPointASet && zone.isNfcPointBSet) {
                    //this zone has tag size, post data
                    Logger.info(zone.zone_name +"point a id: " + zone.qrCodeIdA + "point b id"+zone.qrCodeIdB)
                }
            }

            val syncList = mNFCSetUpHelper!!.tagListToSync
            val gson = Gson()
            val json = gson.toJson(syncList)

            mPresenter!!.syncNFCZoneTags(mCaygoSite!!.site_id.toString(), json)
            Logger.info("""SHITE: list to sync $json""")
            nfc_setup_sync_view.visibility = View.VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.nav_only, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            this.finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        updateList()
    }

    private fun initProgress() {
        val min = mNFCSetUpHelper!!.getCurrentSetupZones(mZones)
        var max = 0
        if(mZones!=null && mZones!!.size>0)  max = mZones!!.size
        nfc_setup_zones_count.text = min.toString() + "/" + max + " Zones Setup"
        nfc_setup_progressBar.max = max
        nfc_setup_progressBar.progress = min
        nfc_setup_sync_button.isEnabled = min == max
        if(!mCaygoSite!!.goToNFCSetupFlow(mCtx)){
            nfc_setup_sync_button.isEnabled = false
        }
    }

    private fun initData() {
        mListener = object : OnSelectedZoneListener {

            override fun onSelectUserInteraction(zone: SiteZone) {
                val intent = Intent(mCtx, NFCZoneActivity::class.java)
                intent.putExtra(UtilStrings.SITE_ZONE, Parcels.wrap<SiteZone>(zone))
                startActivity(intent)
            }
        }
        mCaygoSite = PreferenceHelper.getInstance(mCtx).siteData
        if(mNFCSetUpHelper!!.siteZones.size == 0 || mNFCSetUpHelper!!.siteZones.size != mCaygoSite!!.siteZones.size) {
            mNFCSetUpHelper!!.siteZones = mCaygoSite!!.siteZones
        }
        mUnsyncedZones = mNFCSetUpHelper!!.siteNFCZones
    }

    private fun updateList() {
        mZones = mNFCSetUpHelper!!.siteZones
        mAdapter = NFCSetupListAdapter(mZones!!, mCtx, mListener!!)
        nfc_setup_zones_list.adapter = mAdapter
        initProgress()
    }

    override fun requestSucceed() {
       hideSyncView()
       mNFCSetUpHelper!!.siteZones = ArrayList()
       setUpFinishStartApp()
       PreferenceHelper.getInstance(mCtx).saveNFCSetupSyncFailed(false)
    }

    override fun requestError(result: VolleyError?) {
        hideSyncView()
        PreferenceHelper.getInstance(mCtx).saveNFCSetupSyncFailed(true)
    }

    private fun setUpFinishStartApp() {
        val intent = Intent(this@NFCSetUpActivity, NFCOnBoardingFinishedSetUpActivity::class.java)
        intent.flags = intent.flags or Intent.FLAG_ACTIVITY_NO_HISTORY
        startActivity(intent)
        this.finish()
    }

    private fun hideSyncView() {
        Handler().postDelayed({
            nfc_setup_sync_view.visibility = View.GONE
        }, 700)
    }

    interface OnSelectedZoneListener {
        fun onSelectUserInteraction(zone: SiteZone)
    }
}
