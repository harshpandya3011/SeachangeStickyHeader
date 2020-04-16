package com.seachange.healthandsafty.presenter

import com.seachange.healthandsafty.view.LoginView
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class LoginPresenterTest {

    @Test
    fun validEmailTest() {

        val presenter = LoginPresenter(object : LoginView {

            override fun showLoginSuccess() {

            }

            override fun readyToLogin() {

            }

            override fun showEmailError() {

            }

            override fun emptyEmailError() {

            }

            override fun showPasswordError() {

            }
        })

        Assert.assertEquals(true,
        presenter.isEmailValid("kevins.song@seachange.com"))
    }

    @Test
    fun validPasswordTest() {

        val presenter = LoginPresenter(object : LoginView {

            override fun showLoginSuccess() {

            }

            override fun readyToLogin() {

            }

            override fun showEmailError() {

            }

            override fun emptyEmailError() {

            }

            override fun showPasswordError() {

            }
        })

        Assert.assertEquals(false,
                presenter.isPasswordValid("12345"))
    }

    @Test
    fun validEmailAndPasswordTest() {

        val presenter = LoginPresenter(object : LoginView {

            override fun showLoginSuccess() {

            }

            override fun readyToLogin() {
                Assert.assertEquals(true,
                        true)
            }

            override fun showEmailError() {

            }

            override fun emptyEmailError() {

            }

            override fun showPasswordError() {

            }
        })

        presenter.checkEmailAndPassword("kevin.song@seachange-intl.com", "12343456")
    }
}