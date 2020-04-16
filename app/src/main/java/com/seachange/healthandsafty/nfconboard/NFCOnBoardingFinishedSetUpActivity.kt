package com.seachange.healthandsafty.nfconboard

import android.os.Bundle
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.activity.BaseActivity
import com.seachange.healthandsafty.helper.PreferenceHelper
import kotlinx.android.synthetic.main.activity_nfcon_boarding_finished_set_up.*

class NFCOnBoardingFinishedSetUpActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfcon_boarding_finished_set_up)
        supportActionBar?.hide()
        initButton()
        initData()
    }

    private fun initData() {
        val name = PreferenceHelper.getInstance(mCtx).userName
        if(name != null) {
            showUserName(name)
        }
    }

    private fun showUserName(name: String) {
        val splitStr = name.split("\\s+".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        end_nfc_setup.text = "Nice Work " + splitStr.get(0) + "!"
    }

    private fun initButton() {
        nfc_setup_finish.setOnClickListener {
            this.finish()
        }
    }
}
