package com.seachange.healthandsafty.nfconboard

import android.content.Intent
import android.os.Bundle
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.activity.BaseActivity
import com.seachange.healthandsafty.helper.PreferenceHelper
import com.seachange.healthandsafty.nfc.ui.NFCSetUpActivity
import kotlinx.android.synthetic.main.activity_nfcon_board_tag.*

class NFCOnBoardTagActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfcon_board_tag)
        initData()
        initButton()
    }

    private fun initButton() {
        welcome_start_nfc_button.setOnClickListener {
            val intent = Intent(this, NFCSetUpActivity::class.java)
            startActivity(intent)
            this.finish()
        }
    }

    private fun initData() {
        val mCaygoSite = PreferenceHelper.getInstance(mCtx).siteData
        if (mCaygoSite !=null) {
            val zones = mCaygoSite.siteZones
            if (zones != null) {
                if (zones.size>0) {
                    nfc_zone_number.text = Integer.toString(zones.size * 2)
                } else {
                    nfc_zone_number.text = "0"
                }
            }
        }
    }
}
