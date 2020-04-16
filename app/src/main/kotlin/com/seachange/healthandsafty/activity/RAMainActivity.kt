package com.seachange.healthandsafty.activity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import android.view.Menu
import android.view.MenuItem
import com.android.volley.VolleyError
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.fragment.RiskAssessmentFragment
import com.seachange.healthandsafty.model.RiskAssement
import com.seachange.healthandsafty.presenter.RiskAssessPresenter
import com.seachange.healthandsafty.view.RiskAssessView

import kotlinx.android.synthetic.main.activity_ramain.*

class RAMainActivity : BaseActivity(), RiskAssessView {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private var mPresenter:RiskAssessPresenter? = null
    private var mCurrentFrag: RiskAssessmentFragment? =null
    private var mPreviousFrag: RiskAssessmentFragment? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ramain)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Assigned Risk Assessments"
        supportActionBar!!.run {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.adapter = mSectionsPagerAdapter
        tabs.setupWithViewPager(container)

        fab.setOnClickListener { view ->
            val intent = Intent(this, RAHazardsBPActivity::class.java)
            startActivity(intent)
        }

        mCurrentFrag = RiskAssessmentFragment.newInstance(1)
        mPreviousFrag = RiskAssessmentFragment.newInstance(2)
    }

    override fun receivedResponse(arrayList: ArrayList<RiskAssement>) {

    }

    override fun errorReceived(result: VolleyError) {

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

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {

            return when(position) {
                0->{
                    mCurrentFrag!!
                }
                else -> {
                   mPreviousFrag!!
                }
            }
        }

        override fun getCount(): Int {
            return 2
        }

        override fun getPageTitle(position: Int): CharSequence {

            if (position == 0) {
                return "Upcoming"
            } else {
                return "Previous"
            }
        }
    }
}
