package com.seachange.healthandsafty.activity
import android.os.Bundle

import com.seachange.healthandsafty.fragment.HazardTypeFragment
import com.seachange.healthandsafty.R

class HazardTypeActivity : BaseActivity() {

    private var hazardTypeFragment: HazardTypeFragment? = null
    private var loading:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hazard_type)
        supportActionBar!!.hide()

        hazardTypeFragment = HazardTypeFragment()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.hazard_type_framelayout, hazardTypeFragment!!, "Hazard Type")
        fragmentTransaction.commit()
        showNoConnectionBaseView = true
        setTapble(false)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onBackPressed() {
        if (!loading) {
            super.onBackPressed()
        }
    }

    fun setLoading(boolean: Boolean) {
        loading = boolean
    }
}
