package com.seachange.healthandsafty.presenter

import android.text.TextUtils
import com.seachange.healthandsafty.helper.EmailValidator
import com.seachange.healthandsafty.view.LoginView

class LoginPresenter(private val loginView: LoginView) {


    fun checkEmailAndPassword(emailStr: String, passwordStr: String) {

        var valid = true
        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(passwordStr) && !isPasswordValid(passwordStr)) {
            loginView.showPasswordError()
            valid = false
        }
        if (TextUtils.isEmpty(emailStr)) {
           loginView.emptyEmailError()
            valid = false
        } else if (!isEmailValid(emailStr)) {
            valid = false
            loginView.showEmailError()
        }
        if (valid) {
            loginView.readyToLogin()
        }
    }

    fun isEmailValid(email: String): Boolean {
        val emailValidator = EmailValidator()
        return emailValidator.validate(email)
    }

    fun isPasswordValid(password: String): Boolean {
        return password.length > 7
    }
}