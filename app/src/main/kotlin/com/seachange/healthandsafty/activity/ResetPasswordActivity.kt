package com.seachange.healthandsafty.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.google.android.material.snackbar.Snackbar
import androidx.core.content.ContextCompat
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.android.volley.VolleyError
import kotlinx.android.synthetic.main.activity_reset_password.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.application.SCApplication
import com.seachange.healthandsafty.helper.Logger
import com.seachange.healthandsafty.helper.PreferenceHelper
import com.seachange.healthandsafty.helper.RequestErrorHandler
import com.seachange.healthandsafty.helper.View.RequestErrorHandlerView
import com.seachange.healthandsafty.network.JsonCallBack
import com.seachange.healthandsafty.network.VolleyJsonHelper
import com.seachange.healthandsafty.network.VolleyPostRequestListener
import com.seachange.healthandsafty.utils.UtilStrings

class ResetPasswordActivity : BaseActivity(), RequestErrorHandlerView {


    private var token: String? = null
    private var errorHandler: RequestErrorHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        supportActionBar!!.hide()
        reset_password_lock.typeface = SCApplication.FontMaterial()
        reset_password_confirm_lock.typeface = SCApplication.FontMaterial()

        setTextFieldValidation()
        errorHandler = RequestErrorHandler(this, this, false, true)

        password_reset_confirm.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptReset()
                return@OnEditorActionListener true
            }
            false
        })

        password_reset_button.setOnClickListener{
            attemptReset()
        }

        getToken()
        getDataFromIntent()

    }

    private fun getDataFromIntent() {
        val intent = intent
        val data = intent.data

        if (data !=null && data.toString().contains("'")) {
            val index = data.toString().lastIndexOf('/')
            val token = data.toString().substring(index + 1)
            Logger.info(token)
        }
    }

    //need to get toke from url, default value
    private fun getToken(){
        token = "76adb312-bf4a-464b-8c2f-6d4d5522c4af"
    }

    private fun setTextFieldValidation() {

        val mPasswordListener = password_reset.onFocusChangeListener
        val mConfirmPasswordListener = password_reset_confirm.onFocusChangeListener

        password_reset.setOnFocusChangeListener { view, focus ->

            val passwordStr = password_reset.text.toString()
            var error = false

            mPasswordListener.onFocusChange(view, focus)

            if (focus) {
                reset_password_lock.setTextColor(ContextCompat.getColor(mCtx, R.color.tittleGrey))
            } else{
             if (!TextUtils.isEmpty(passwordStr)) {
                    if (!isPasswordValid(passwordStr)) {
                        password_reset_text_box.setError("Please enter a password of at least 8 characters.", false)
                        error = true
                    }
                    if (error) {
                        reset_password_lock.setTextColor(ContextCompat.getColor(mCtx, R.color.errorColor))
                    } else {
                        reset_password_lock.setTextColor(ContextCompat.getColor(mCtx, R.color.formWhite))
                    }
                } else {
                 password_reset_text_box.setError("Please enter a password of at least 8 characters.", false)
                 reset_password_lock.setTextColor(ContextCompat.getColor(mCtx, R.color.errorColor))
                }
            }
        }

        password_reset_confirm.setOnFocusChangeListener { view, focus ->

            mConfirmPasswordListener.onFocusChange(view, focus)
            val passwordStr = password_reset_confirm.text.toString()
            var error = false

            if (focus) {
                reset_password_confirm_lock.setTextColor(ContextCompat.getColor(mCtx, R.color.tittleGrey))
            } else{
                if (!TextUtils.isEmpty(passwordStr)) {
                    if (!isPasswordValid(passwordStr)) {
                        password_reset_text_box_confirm.setError("Please enter a password of at least 8 characters.", false)
                        error = true
                    }
                    if (error) {
                        reset_password_confirm_lock.setTextColor(ContextCompat.getColor(mCtx, R.color.errorColor))
                    } else {
                        reset_password_confirm_lock.setTextColor(ContextCompat.getColor(mCtx, R.color.formWhite))
                    }
                } else {
                    password_reset_text_box_confirm.setError("Please enter a password of at least 8 characters.", false)
                    reset_password_confirm_lock.setTextColor(ContextCompat.getColor(mCtx, R.color.errorColor))
                }
            }
        }

       //text change watcher
        password_reset.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                reset_password_lock.setTextColor(ContextCompat.getColor(mCtx, R.color.tittleGrey))
            }

        })

        password_reset_confirm.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                reset_password_confirm_lock.setTextColor(ContextCompat.getColor(mCtx, R.color.tittleGrey))
            }
        })
    }

    private fun attemptReset() {

        val error = checkError()
        if (!error){
            resetPassword()
        }
    }

    public fun checkError():Boolean{
        var error = false

        val passwordStr = password_reset.text.toString()
        val passwordConfirm = password_reset_confirm.text.toString()

        if (!isPasswordValid(passwordStr)) {
            error = true
            password_reset_text_box.setError("Please enter a password of at least 8 characters.", false)
            reset_password_lock.setTextColor(ContextCompat.getColor(mCtx, R.color.errorColor))

        }else if(!isPasswordValid(passwordConfirm)){
            password_reset_text_box_confirm.setError("Please enter a password of at least 8 characters.", false)
            reset_password_confirm_lock.setTextColor(ContextCompat.getColor(mCtx, R.color.errorColor))
            error = true

        }else if(passwordStr != passwordConfirm){
            password_reset_text_box_confirm.setError("Please enter matching passwords.", false)
            reset_password_confirm_lock.setTextColor(ContextCompat.getColor(mCtx, R.color.errorColor))
            error = true
        }
        return error
    }

    private fun resetPassword() {

        showProgress(true)
        val callBack = object : JsonCallBack {

            override fun callbackJSONObject(result: JSONObject) {
                if (result.has(UtilStrings.STATUS_CODE)) {
                    try {
                        val statusCode = result.getInt(UtilStrings.STATUS_CODE)
                        if (statusCode == 200) {

                            val userEmail: String = result.getJSONObject("response").getString("email")
                            resetPasswordWithEmail(userEmail)
                            Logger.info("User TAG" + " user email: " + userEmail)
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
//                showMessage("Link expired", "Your password reset link has expired. Enter your email address again to receive a new link.", false)
//                Logger.info(result.toString())
//                showProgress(false)
                errorHandler!!.onErrorResponse(result)

            }
        }

        VolleyJsonHelper(UtilStrings.RESET_PASSWORD_USER + token, "User TAG", callBack, mCtx).getJsonObjectFromVolleyHelper(null, null)
    }

    private fun resetPasswordWithEmail(userEmail: String) {

        val passwordStr = password_reset.text.toString()
        val passwordConfirm = password_reset_confirm.text.toString()

        val callBack = object : VolleyPostRequestListener {

            override fun requestStarted() {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun requestCompleted(response: String?) {
                login(userEmail, passwordStr)
                Logger.info("reset password successfully: " + response)
            }

            override fun requestEndedWithError(error: VolleyError?) {
                showProgress(false)
                errorHandler!!.onErrorResponse(error)
            }
        }

        val hashMap = HashMap<String, String>()
        hashMap["email"] = userEmail
        hashMap["token"] = token!!
        hashMap["password"] = passwordStr
        hashMap["passwordConfirm"] = passwordConfirm

        VolleyJsonHelper(mCtx).putRequestWithParams(UtilStrings.RESET_PASSWORD, hashMap, "Rest password", "bearer", callBack)
    }

    private fun login(email: String, password: String) {

        val callBack = object : VolleyPostRequestListener {

            override fun requestStarted() {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun requestCompleted(response: String?) {
                val result = JSONObject(response)
                val token = result.getString("access_token")
                PreferenceHelper.getInstance(mCtx).saveToken(token)
                Logger.info("logged in successfully: " + response)

                showSelectUser()
                showProgress(false)
            }

            override fun requestEndedWithError(error: VolleyError?) {
                showProgress(false)
                errorHandler!!.onErrorResponse(error)
            }
        }

        val hashMap = HashMap<String, String>()
        hashMap["username"] = email
        hashMap["password"] = password
        hashMap["grant_type"] = "password"
        VolleyJsonHelper(mCtx).postRequestWithParams(UtilStrings.LOGIN_API, hashMap, "Login", "bearer", callBack)
    }

    private fun showSelectUser() {

        Snackbar.make(findViewById(R.id.reset_password_main_view), "Password reset successfully", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()

        Handler().postDelayed({
            val intent = Intent(this, SelectUserActivity::class.java)
            startActivity(intent)
            this.finish()
        }, 1000)
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 7
    }

    private fun showProgress(show: Boolean) = if (show) {
        password_reset_button.isEnabled = false
        reset_password_progress_layout.visibility = View.VISIBLE
        reset_password_avi.smoothToShow()
    } else {
        password_reset_button.isEnabled = true
        reset_password_progress_layout.visibility = View.GONE
        reset_password_avi.smoothToHide()
    }

    override fun errorDialogPositiveClicked() {

    }

    override fun errorDialogNegativeClicked() {

    }
}
