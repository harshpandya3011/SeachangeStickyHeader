package com.seachange.healthandsafty.activity

import android.os.Bundle
import android.os.Parcelable
import kotlinx.android.synthetic.main.activity_end_check.*
import org.parceler.Parcels
import com.seachange.healthandsafty.model.SiteZone
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.helper.CheckPreference
import com.seachange.healthandsafty.utils.UtilStrings

class EndCheckActivity : BaseActivity() {

    private var mZonePref: CheckPreference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end_check)
        supportActionBar!!.hide()

        end_check_close.setOnClickListener {
            finish()
        }

        mZonePref = CheckPreference(mCtx)

        val mUser = mZonePref!!.currentCaygoUser
        if(mUser !=null) {
            end_check_user_name.text = mUser.fullName
            end_check_user_header.text = "Nice Work " + mUser.firstName + " !"
        }

        zone_check_time.text = mZonePref!!.timeSpendOnZoneCheck

        if (intent.hasExtra(UtilStrings.SITE_ZONE)) {
            val  mCurrentZone = Parcels.unwrap<SiteZone>(intent.getParcelableExtra<Parcelable>(UtilStrings.SITE_ZONE))
            if (mCurrentZone !=null) {
                end_check_zone_title.text = mCurrentZone.zone_name
                end_check_zone_sub_title.text = mCurrentZone.zone_name
            }
        }

    }
}
