package com.seachange.healthandsafty.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.fragment.RAHazardsFragment
import com.seachange.healthandsafty.helper.HazardObserver
import java.util.*

class RAHazardsActivity : AppCompatActivity(), Observer {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rahazards2)

        val mFragment = RAHazardsFragment.newInstance(1)
        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.ra_hazards_content_framelayout, mFragment, "jsa_content_fragment")
                    .commit()
        }
        supportActionBar!!.title = "Select Risk Categories"
        supportActionBar!!.run {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onResume() {
        super.onResume()
        HazardObserver.getInstance().addObserver(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->
                this.finish()
        }
        return true
    }

    override fun update(p0: Observable?, p1: Any?) {
        val observer = p0 as HazardObserver
        if (observer.isRaAdded) {
            this.finish()
        }
    }
}
