package com.seachange.healthandsafty.nfc.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.nfc.view.NFCListener

//tag page base id type
//read tag - type = 1
//write tag - type = 2


class NFCTagActivity : AppCompatActivity(), NFCListener {

    override fun onDialogDisplayed() {
        isDialogDisplayed = true
    }

    override fun onDialogDismissed() {
        isDialogDisplayed = false
    }


    private var mNfcReadFragment: NFCReadFragment? = null
    private var isDialogDisplayed = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfctag)
        supportActionBar!!.hide()
        showReadFragment()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }


    private fun showReadFragment() {
        mNfcReadFragment = supportFragmentManager.findFragmentByTag(NFCReadFragment.TAG) as? NFCReadFragment
        if (mNfcReadFragment == null) {
            mNfcReadFragment = NFCReadFragment.newInstance()
        }
        mNfcReadFragment!!.show(fragmentManager, NFCReadFragment.TAG)
    }

}
