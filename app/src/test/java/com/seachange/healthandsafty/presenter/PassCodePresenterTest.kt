package com.seachange.healthandsafty.presenter

import com.seachange.healthandsafty.model.UserData
import com.seachange.healthandsafty.view.PassCodeView
import org.junit.Assert
import org.junit.Test

class PassCodePresenterTest {

    @Test
    @Throws(Exception::class)
    fun checkPasscodeTest() {

        val presenter = PassCodePresenter(object : PassCodeView {

            override fun onValidSuccessful() {
                Assert.assertEquals(true,
                        true)
            }

            override fun onValidFailed() {

            }

            override fun onTempPassCode() {

            }
        })

        presenter.validatePassCode(
                "$2a$11\$lTvDAXTuDlVpkLnmvQuHWOHFfPrD/oD5dCqQ8Y8guPjBEx7NdIMRi",
                "1234",
                UserData(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        0,
                        null,
                        null
                ), true
        )

    }
}