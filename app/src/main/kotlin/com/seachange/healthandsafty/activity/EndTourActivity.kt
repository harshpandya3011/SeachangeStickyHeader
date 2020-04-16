package com.seachange.healthandsafty.activity

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_end_tour.*
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.helper.CheckPreference

class EndTourActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end_tour)
        supportActionBar!!.hide()

        val mZonePref = CheckPreference(mCtx)

        val mUser = mZonePref.currentCaygoUser
        if(mUser !=null) {
            end_tour_name.text = "Nice Work " + mUser.fullName + " !"
        }

        end_tour_close.setOnClickListener {
            finish()
        }
    }
}
