package com.seachange.healthandsafty.activity.checklist_templates

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.fragment.checklist_templates.CheckListTemplateFragment

class CheckListTemplateActivity : AppCompatActivity() {

//    private val healthAndSafetyViewModel by viewModels<HealthAndSafetyViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frame_layout)

//        healthAndSafetyViewModel.fetchCheckTypeList()


        if (supportFragmentManager.fragments.isEmpty()) {
            supportFragmentManager.commit {
                replace(R.id.fl_container_manage_user, CheckListTemplateFragment())
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

}
