package com.seachange.healthandsafty.activity

import android.os.Bundle

import com.seachange.healthandsafty.fragment.HazardSourceFragment
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.helper.HazardObserver
import java.util.*

class HazardSourceActivity : BaseActivity(), Observer {

    private var loading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hazard_source)
        supportActionBar!!.hide()

        val hazardSourceFragment = HazardSourceFragment()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.hazard_source_framelayout, hazardSourceFragment, "Hazard Source")
        fragmentTransaction.commit()
        showNoConnectionBaseView = true
        setTapble(false)
    }

    override fun update(p0: Observable?, p1: Any?) {
        if (p0 is HazardObserver) {
            val observer = p0 as HazardObserver
            if (observer.isHazardChanged) {
                this.finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        HazardObserver.getInstance().addObserver(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        HazardObserver.getInstance().deleteObserver(this)
    }

    override fun onStop() {
        super.onStop()
        showNoConnectionDialog = false
    }

    fun setLoading(boolean: Boolean) {
        loading = boolean
    }
}
