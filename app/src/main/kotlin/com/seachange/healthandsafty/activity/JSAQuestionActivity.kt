package com.seachange.healthandsafty.activity

import android.os.Bundle
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.fragment.JSAQuestionFragment

class JSAQuestionActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jsaquestion)

        val jsaQuestionFragment = JSAQuestionFragment.newInstance(1)
        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.jas_question_framelayout, jsaQuestionFragment, "jsa_question_fragment")
                    .commit()
        }

        supportActionBar!!.hide()
    }
}
