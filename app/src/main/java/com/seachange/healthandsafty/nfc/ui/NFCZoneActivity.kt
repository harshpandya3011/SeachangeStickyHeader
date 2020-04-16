package com.seachange.healthandsafty.nfc.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.activity.BaseActivity
import com.seachange.healthandsafty.application.SCApplication
import com.seachange.healthandsafty.model.SiteZone
import com.seachange.healthandsafty.nfc.helper.NFCSetUpHelper
import com.seachange.healthandsafty.nfc.helper.NFCZoneSetUp
import com.seachange.healthandsafty.utils.UtilStrings
import kotlinx.android.synthetic.main.activity_nfczone.*
import org.parceler.Parcels

class NFCZoneActivity : BaseActivity() {

    private var mZone: SiteZone? = null
    private var nfcZonecheckHelper: NFCSetUpHelper?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfczone)
        mZone = Parcels.unwrap<SiteZone>(intent.getParcelableExtra<Parcelable>(UtilStrings.SITE_ZONE))
        setupActionBar()
        initView()
        initData()

        NFCZoneSetUp.getInstance().initSetUp(mZone)
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

    private fun setupActionBar() {
        setSupportActionBar(nfc_zone_toolbar)
        supportActionBar?.title = mZone!!.zone_name + " NFC Tags"
        supportActionBar?.run {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

    }

    private fun initData() {
        nfcZonecheckHelper = NFCSetUpHelper(mCtx)

    }

    private fun initView() {
        nfc_zone_setup_check_icon_b.typeface = SCApplication.FontMaterial()
        nfc_zone_setup_check_icon_a.typeface = SCApplication.FontMaterial()

        nfc_zone_setup_pointa.setOnClickListener {
            val intent = Intent(mCtx, NFCMainActivity::class.java)
            intent.putExtra(UtilStrings.SITE_ZONE, Parcels.wrap<SiteZone>(mZone))
            intent.putExtra(UtilStrings.NFC_TYPE, 2)
            intent.putExtra(UtilStrings.NFC_POINT, "A")
            startActivity(intent)
        }

        nfc_zone_setup_pointb.setOnClickListener {
            val intent = Intent(mCtx, NFCMainActivity::class.java)
            intent.putExtra(UtilStrings.SITE_ZONE, Parcels.wrap<SiteZone>(mZone))
            intent.putExtra(UtilStrings.NFC_TYPE, 2)
            intent.putExtra(UtilStrings.NFC_POINT, "B")
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        if(nfcZonecheckHelper!!.nfcStatus) {
            showSnackBar()
        }
        nfcZonecheckHelper!!.setNFCSetUpChanged(false)
        updateSetUpView()
    }

    private fun updateSetUpView() {


        if (NFCZoneSetUp.getInstance().isPointASetup || mZone!!.isTagSetup) {
            a_tag_icon.setImageDrawable(ContextCompat.getDrawable(mCtx, R.mipmap.point_setup))
            a_tag.setTextColor(ContextCompat.getColor(applicationContext, R.color.alertTitle))
        }

        if (NFCZoneSetUp.getInstance().isPointBSetup || mZone!!.isTagSetup) {
            b_tag_icon.setImageDrawable(ContextCompat.getDrawable(mCtx, R.mipmap.point_setup))
            b_tag.setTextColor(ContextCompat.getColor(applicationContext, R.color.alertTitle))
        }

        if (NFCZoneSetUp.getInstance().isTagSet && NFCZoneSetUp.getInstance().isPointASetup && NFCZoneSetUp.getInstance().isPointBSetup) {
           Handler().postDelayed({
               this.finish()
            }, 1200)
        }
        NFCZoneSetUp.getInstance().isTagSet = false
    }

    private fun showSnackBar() {
        Handler().postDelayed({
            val text = "Point "+ NFCZoneSetUp.getInstance().point +" NFC tag setup successful";
            Snackbar.make(findViewById(R.id.nfc_zone_main), text, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }, 500)
    }
}
