package com.seachange.healthandsafty.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.application.SCApplication
import com.seachange.healthandsafty.helper.HazardObserver
import com.seachange.healthandsafty.presenter.RASelectOptionPresenter
import com.seachange.healthandsafty.view.RASelectOptionView
import kotlinx.android.synthetic.main.activity_raselect_option.*
import java.util.*

class RASelectOptionActivity : BaseActivity(), Observer, RASelectOptionView {

    private var mPresenter: RASelectOptionPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_raselect_option)

        supportActionBar!!.title = "Select Type"
        supportActionBar!!.run {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        ra_hazard_option.setOnClickListener{
            val intent = Intent(this, RAHazardsActivity::class.java)
            startActivity(intent)
        }

        ra_bp_option.setOnClickListener {

        }

        mPresenter = RASelectOptionPresenter(this)
        val screenWidth = this.resources.displayMetrics.widthPixels
        mPresenter!!.getLayoutParams(screenWidth, mCtx)

        ra_alert.typeface = SCApplication.FontMaterial()
        ra_bp.typeface = SCApplication.FontMaterial()
        HazardObserver.getInstance().isRaAdded = false
    }

    override fun optionViewParam(param: LinearLayout.LayoutParams) {
        ra_hazard_option.layoutParams = param
        ra_bp_option.layoutParams = param
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_rahazards, menu)
        return true
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
        if (observer.isImagesChanged || observer.isRaAdded) {
            this.finish()
        }
    }
}
