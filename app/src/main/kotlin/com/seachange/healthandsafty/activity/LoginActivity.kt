package com.seachange.healthandsafty.activity

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Base64
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.android.volley.VolleyError
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.application.SCApplication
import com.seachange.healthandsafty.helper.*
import com.seachange.healthandsafty.helper.View.RequestErrorHandlerView
import com.seachange.healthandsafty.model.CaygoSite
import com.seachange.healthandsafty.network.JsonCallBack
import com.seachange.healthandsafty.network.VolleyJsonHelper
import com.seachange.healthandsafty.network.VolleyPostRequestListener
import com.seachange.healthandsafty.presenter.LoginPresenter
import com.seachange.healthandsafty.utils.UtilStrings
import com.seachange.healthandsafty.view.LoginView
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : BaseActivity(), LoginView, RequestErrorHandlerView {


    private var emailValidator: EmailValidator? = null
    var passwordShowing = false
    private var loginPresenter: LoginPresenter? = null
    private var errorHandler: RequestErrorHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setChildActivity(this)
        supportActionBar!!.hide()
        loginPresenter = LoginPresenter(this)
        emailValidator = EmailValidator()

        initView()
        setTextFieldValidation()
        hideAndShowPassword()

        showNoConnectionDialog = true
        errorHandler = RequestErrorHandler(this, this, false, true)
        initButtons()
        getEmailIfExist()
        if (UserDateHelper(mCtx).userLoggedIn) {
            UserDateHelper(mCtx).resetUserLoggedInDetails()
        }
    }

    private fun initView() {
        form_mail.typeface = SCApplication.FontMaterial()
        form_lock.typeface = SCApplication.FontMaterial()
        login_show_button.typeface = SCApplication.FontMaterial()
        clear_mail_button.typeface = SCApplication.FontMaterial()
    }

    private fun getEmailIfExist() {
        val savedEmail = PreferenceHelper.getInstance(mCtx).userEmail
        if (savedEmail != null) {
            email.setText(savedEmail, TextView.BufferType.EDITABLE)
            password.requestFocus()
        }
    }

    private fun initButtons() {
        password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        password_reset_button.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
            this.finish()
        }


        email_sign_in_button.setOnClickListener { attemptLogin() }

        clear_mail_button.setOnClickListener {
            email.text?.clear()
        }
    }

    private fun hideAndShowPassword() {

        login_show_button.setOnClickListener {

            val cursor: Int = password.selectionStart
            if (passwordShowing) {
                passwordShowing = false
                password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                login_show_button.text = mCtx.resources.getString(R.string.fa_eye)
            } else {
                passwordShowing = true
                password.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                login_show_button.text = mCtx.resources.getString(R.string.fa_eye_off)
            }
            password.setSelection(cursor)

            //strange bug, need to set type face otherwise, font changes after hide or show password
            password.typeface = Typeface.DEFAULT
        }
    }

    private fun setTextFieldValidation() {

        email.setOnFocusChangeListener { view, focus ->

            val emailStr = email.text.toString()
            var error = false

            if (focus) {
                form_mail.setTextColor(ContextCompat.getColor(mCtx, R.color.tittleGrey))
            } else {
                if (!TextUtils.isEmpty(emailStr)) {
                    if (!isEmailValid(email.text.toString())) {
                        email_text_box.setError("Please enter a valid email address.", false)
                        error = true
                    }
                    form_mail.setTextColor(ContextCompat.getColor(mCtx, R.color.formWhite))

                    if (error) {
                        form_mail.setTextColor(ContextCompat.getColor(mCtx, R.color.errorColor))
                    } else {
                        form_mail.setTextColor(ContextCompat.getColor(mCtx, R.color.formWhite))
                    }
                } else {
                    email_text_box.setError("Please enter a valid email address.", false)
                    form_mail.setTextColor(ContextCompat.getColor(mCtx, R.color.errorColor))
                }
            }
            updateSignInButton()
        }

        password.setOnFocusChangeListener { view, focus ->
            val passwordStr = password.text.toString()
            var error = false

            if (focus) {
                form_lock.setTextColor(ContextCompat.getColor(mCtx, R.color.tittleGrey))
            } else {
                if (!TextUtils.isEmpty(passwordStr)) {
                    if (!isPasswordValid(passwordStr)) {
                        password_text_box.setError("Please enter a password of at least 8 characters.", false)
                        error = true
                    }
                    if (error) {
                        form_lock.setTextColor(ContextCompat.getColor(mCtx, R.color.errorColor))
                    } else {
                        form_lock.setTextColor(ContextCompat.getColor(mCtx, R.color.formWhite))
                    }
                } else {
                    password_text_box.setError("Please enter a password of at least 8 characters.", false)
                    form_lock.setTextColor(ContextCompat.getColor(mCtx, R.color.errorColor))
                }
            }
            updateSignInButton()
        }

        //text change watcher
        password.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(p0: Editable?) {
                updateSignInButton()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                form_lock.setTextColor(ContextCompat.getColor(mCtx, R.color.tittleGrey))
            }

        })

        email.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(p0: Editable?) {
                updateSignInButton()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                form_mail.setTextColor(ContextCompat.getColor(mCtx, R.color.tittleGrey))
            }
        })
    }

    private fun updateSignInButton() {
        val emailStr = email.text.toString()
        val passwordStr = password.text.toString()
        email_sign_in_button.isEnabled = !TextUtils.isEmpty(passwordStr) && isPasswordValid(passwordStr) && !TextUtils.isEmpty(emailStr) && isEmailValid(emailStr)
    }

    private fun attemptLogin() {
        // Reset errors.
        email.error = null
        password.error = null
        loginPresenter!!.checkEmailAndPassword(email.text.toString(), password.text.toString())
    }

    override fun showLoginSuccess() {

    }

    override fun readyToLogin() {
        showProgress(true)
        login()
    }

    override fun showEmailError() {
        form_mail.setTextColor(ContextCompat.getColor(mCtx, R.color.errorColor))
        email_text_box.setError("Please enter a valid email address.", false)
        email_text_box.setError("Please enter a valid email address.", false)
    }

    override fun emptyEmailError() {
        email_text_box.setError("Please enter a valid email address.", false)
        form_mail.setTextColor(ContextCompat.getColor(mCtx, R.color.errorColor))
    }

    override fun showPasswordError() {
        password_text_box.setError("Please enter a password of at least 8 characters.", false)
        form_lock.setTextColor(ContextCompat.getColor(mCtx, R.color.errorColor))
    }

    private fun login() {

        val email = email.text.toString()
        val password = password.text.toString()
        email_sign_in_button.isEnabled = false
        showProgress(true)
        val callBack = object : VolleyPostRequestListener {

            override fun requestStarted() {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun requestCompleted(response: String?) {
                val result = JSONObject(response)
                val token = result.getString("access_token")
                val refreshToken = result.getString("refresh_token")
                val preferenceHelper = PreferenceHelper.getInstance(mCtx)

                //need to test this,
                //clear preference when logged in with different users.
                val savedEmail = PreferenceHelper.getInstance(mCtx).userEmail
                if (savedEmail != null) {
                    if (savedEmail != email) {
                        PreferenceHelper.getInstance(mCtx).clearPreference()
                        mApplication.deleteAllFromDB()
                    }
                }

                preferenceHelper.saveUserEmail(email)
                preferenceHelper.saveRefreshToken(refreshToken)
                preferenceHelper.saveToken(token)

                val siteId = result.optString("as:tenantId")?.toIntOrNull()
                if (siteId != null && preferenceHelper.siteData == null) {
                    preferenceHelper.saveSiteData(CaygoSite().apply {
                        site_id = siteId
                    })
                }
                getUserData()
                email_sign_in_button.isEnabled = true
                showProgress(false)
                Logger.info("""logged in successfully: $response""")

                //
                //logged in successfully: load the users page now.
                //
                val intent = Intent(this@LoginActivity, SelectUserActivity::class.java)
                intent.putExtra(UtilStrings.MANAGER_HOME, true)
                intent.putExtra(UtilStrings.FROM_LOGIN, true)
                startActivity(intent)

                (mCtx as SCApplication).checkRefreshToken()
            }

            override fun requestEndedWithError(error: VolleyError?) {
                email_sign_in_button.isEnabled = true
                showProgress(false)
                if (isLive) {
                    errorHandler!!.onErrorResponse(error)
                }
            }
        }

        val hashMap = HashMap<String, String>()
        hashMap["username"] = email
        hashMap["password"] = password
        hashMap["grant_type"] = "password"

        val authBasic = String.format("%s:%s", UtilStrings.AUTH_CLIENT, UtilStrings.AUTH_BASIC)
        val auth = "Basic " + Base64.encodeToString(authBasic.toByteArray(), Base64.DEFAULT)
        VolleyJsonHelper(mCtx).postRequestWithParams(UtilStrings.LOGIN_API, hashMap, "Login", auth, callBack)
    }

    private fun isEmailValid(email: String): Boolean {
        return emailValidator!!.validate(email)
    }

    fun isPasswordValid(password: String): Boolean {
        return password.length > 7
    }

    private fun showProgress(show: Boolean) {
        if (show) {
            email.isEnabled = false
            password.isEnabled = false
            login_progress_layout.visibility = View.VISIBLE
            login_avi.smoothToShow()
        } else {
            email.isEnabled = true
            password.isEnabled = true
            login_progress_layout.visibility = View.GONE
            login_avi.smoothToHide()
        }
    }

    private fun getUserData() {
        val callBack = object : JsonCallBack {
            override fun callbackJSONObject(result: JSONObject) {
                if (result.has(UtilStrings.STATUS_CODE)) {
                    try {
                        val statusCode = result.getInt(UtilStrings.STATUS_CODE)
                        if (statusCode == 200) {

                            val response = result.getJSONObject("response")
                            val userId: String = response.getString("id")
                            val userName: String = response.getString("name")
                            val siteId = response.optString("primarySiteId")?.toIntOrNull()
                            val preferenceHelper = PreferenceHelper.getInstance(mCtx)
                            preferenceHelper.saveUserName(userName)
                            if (siteId != null && preferenceHelper.siteData == null) {
                                preferenceHelper.saveSiteData(CaygoSite().apply {
                                    site_id = siteId
                                })
                            }
                            Logger.info("User TAG" + ": " + result.toString())
                        }
                    } catch (e: JSONException) {
                        Logger.info("User TAG" + ": " + e.toString())
                    }
                }
            }

            override fun callbackJsonArray(result: JSONArray) {

            }

            override fun callbackErrorCalled(result: VolleyError) {
                Logger.info(result.toString())
                showProgress(false)
            }
        }

        VolleyJsonHelper(UtilStrings.USER_API, "User TAG", callBack, mCtx).getJsonObjectFromVolleyHelper(PreferenceHelper.getInstance(mCtx).requestToken(), null)
    }

    override fun errorDialogPositiveClicked() {

    }

    override fun errorDialogNegativeClicked() {
        val intent = Intent(this@LoginActivity, ResetPasswordActivity::class.java)
        startActivity(intent)
    }

    //close all all screens when on back pressed, need to think about this one...
//    override fun onBackPressed() {
//        super.onBackPressed()
//        finishAffinity()
//    }
}
