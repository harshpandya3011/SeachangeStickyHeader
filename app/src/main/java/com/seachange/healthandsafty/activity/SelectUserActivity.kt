package com.seachange.healthandsafty.activity

import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.ndk.CrashlyticsNdk
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.fragment.SelectUserFragment
import com.seachange.healthandsafty.helper.CheckPreference
import com.seachange.healthandsafty.helper.HazardObserver
import com.seachange.healthandsafty.viewmodel.ManageUsersViewModel
import io.fabric.sdk.android.Fabric
import java.util.*


class SelectUserActivity : BaseActivity(), Observer {

    private val manageUsersViewModel by viewModels<ManageUsersViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_user)
        Fabric.with(this, Crashlytics(), CrashlyticsNdk())

        AppCenter.start(application, "8561f7ba-b6aa-48ae-831a-be053ac36ff0",
                Analytics::class.java, Crashes::class.java)

        val selectUserFragment = SelectUserFragment.newInstance(1)
        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.selecte_user_framelayout, selectUserFragment, "select_user_fragment")
                    .commit()
        }
        supportActionBar!!.title = "Proceed As"
        Analytics.trackEvent("Select User Screen")

//        val mWordViewModel = ViewModelProviders.of(this).get(DBCheckViewModel::class.java!!)
//        mWordViewModel.allChecks.observe(this, android.arch.lifecycle.Observer { checks ->
//            // Update the cached copy of the words in the adapter.
//            val tmp = checks
//            com.seachange.healthandsafty.helper.Logger.info("DB -> result called............. ")
//            if (tmp!!.size>0) {
//                for (i in 0..tmp!!.size-1) {
//                    com.seachange.healthandsafty.helper.Logger.info("DB -> " + tmp.get(i).zoneCheck)
//                }
//            }
//        })

        //
        //removed no connection dialog, may need it again, wait and see
        //
//        showNoConnectionDialog = true
        showNoConnectionBaseView = true
        manageUsersViewModel.fetchUsers()
    }

    fun refreshMenuItems() {
        refreshCaygoSiteData()
    }

    override fun onResume() {
        super.onResume()
        HazardObserver.getInstance().addObserver(this)
        manageUsersViewModel.refreshUsers()
        CheckPreference(mCtx).saveCaygoUser(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        HazardObserver.getInstance().deleteObserver(this)
    }

    override fun update(p0: Observable?, p1: Any?) {
        val isSync = updateSyncFromObserver(p0)
        if (isSync) {
            updateSyncFromObserver(p0)
            HazardObserver.getInstance().resetSyncObserver()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.action_logout).title = "Log out "
        return super.onPrepareOptionsMenu(menu)
    }
}
