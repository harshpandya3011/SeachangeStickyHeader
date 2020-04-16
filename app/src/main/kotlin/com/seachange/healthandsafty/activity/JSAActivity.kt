package com.seachange.healthandsafty.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.fragment.JSAMainFragment

class JSAActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jsa)

        val jsaContentFragment = JSAMainFragment.newInstance(1)
        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.jas_content_framelayout, jsaContentFragment, "jsa_content_fragment")
                    .commit()
        }
        supportActionBar!!.hide()
    }
}
