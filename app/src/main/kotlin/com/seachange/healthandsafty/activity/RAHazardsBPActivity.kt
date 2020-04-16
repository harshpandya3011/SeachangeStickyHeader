package com.seachange.healthandsafty.activity

import android.content.Intent

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.activity.camera.CameraActivity
import com.seachange.healthandsafty.fragment.RAHazardsBPFragment
import com.seachange.healthandsafty.helper.PreferenceHelper
import com.seachange.healthandsafty.model.HazardBP
import com.seachange.healthandsafty.presenter.HazardBPPresenter
import com.seachange.healthandsafty.view.HazardBPView

import kotlinx.android.synthetic.main.activity_rahazards.*

class RAHazardsBPActivity : BaseActivity(), HazardBPView {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    var mHazardBPs: ArrayList<HazardBP> = ArrayList()
    var mHazards: ArrayList<HazardBP> = ArrayList()
    var mBPs: ArrayList<HazardBP> = ArrayList()
    private var mAllFragment: RAHazardsBPFragment ? = null
    private var mHazardFragment: RAHazardsBPFragment ? = null
    private var mBPFragment: RAHazardsBPFragment ?= null
    private var mPresenter: HazardBPPresenter ?= null
    private var firstLoad = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rahazards)

        setSupportActionBar(hazards_toolbar)
        hazards_toolbar.title = "Risk Assessment"
        supportActionBar!!.run {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        mPresenter = HazardBPPresenter(this)
        mPresenter!!.getData()

        mAllFragment = RAHazardsBPFragment.newInstance(1)
        mHazardFragment = RAHazardsBPFragment.newInstance(1)
        mBPFragment = RAHazardsBPFragment.newInstance(1)

        mAllFragment!!.setPageType(1)
        mHazardFragment!!.setPageType(2)
        mBPFragment!!.setPageType(3)

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.adapter = mSectionsPagerAdapter
        tabs.setupWithViewPager(container)

        hazard_fab.setOnClickListener { view ->

            if (PreferenceHelper.getInstance(mCtx).raFlow == 1) {
                val intent = Intent(this, RASelectOptionActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, CameraActivity::class.java)
                startActivity(intent)
            }
        }

        container.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                pageChanged(position)

            }
        })
        section_label.visibility = View.GONE
//        if (mHazardBPs.size>0) {
//            section_label.visibility = View.GONE
//        } else {
//            section_label.visibility = View.VISIBLE
//        }
    }

    override fun onResume() {
        super.onResume()
//        if (firstLoad) {
//            section_label.visibility = View.VISIBLE
//            firstLoad = false
//            container.visibility = View.GONE
//        } else {
//            section_label.visibility = View.GONE
//            container.visibility = View.VISIBLE
//        }
    }

    override fun hazardBPReceived(all: ArrayList<HazardBP>, hazards: ArrayList<HazardBP>, bps: ArrayList<HazardBP>) {

        mHazardBPs = all
        mHazards = hazards
        mBPs = bps
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_rahazards, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            this.finish()
        } else if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    fun pageChanged(position: Int) {
//        if (position == 0){
//            if(mHazardBPs.size>0) {
//                section_label.visibility = View.GONE
//            } else {
//                section_label.visibility = View.VISIBLE
//            }
//        } else if (position == 1){
//            if( mHazardBPs.size>0) {
//                section_label.visibility = View.GONE
//            } else {
//                section_label.visibility = View.VISIBLE
//            }
//        }else if (position == 2){
//            if( mBPs.size>0) {
//                section_label.visibility = View.GONE
//            } else {
//                section_label.visibility = View.VISIBLE
//            }
//        }
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {

            when(position) {
                0->{
                    return mAllFragment!!
                }
                1->{
                    return mHazardFragment!!
                }
                else -> {
                    return mBPFragment!!
                }
            }
        }

        override fun getCount(): Int {
            return 3
        }

        override fun getPageTitle(position: Int): CharSequence {

            if (position == 0) {
                return "ALL"
            } else if(position ==1) {
                return "HAZARDS"
            } else {
                return "BEST \n PRACTICES"
            }
        }
    }
}
