package com.seachange.healthandsafty.view

interface LoginView {
    fun showLoginSuccess()
    fun readyToLogin()
    fun showEmailError()
    fun emptyEmailError()
    fun showPasswordError()
}