package com.seachange.healthandsafty.activity

import android.os.Bundle
import androidx.core.content.ContextCompat
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.android.volley.VolleyError
import kotlinx.android.synthetic.main.activity_change_password.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.application.SCApplication
import com.seachange.healthandsafty.helper.EmailValidator
import com.seachange.healthandsafty.helper.Logger
import com.seachange.healthandsafty.network.JsonCallBack
import com.seachange.healthandsafty.network.VolleyJsonHelper
import com.seachange.healthandsafty.network.VolleyPostRequestListener
import com.seachange.healthandsafty.utils.UtilStrings

class ChangePasswordActivity : BaseActivity() {

    private var emailValidator: EmailValidator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        supportActionBar!!.hide()
        change_password_form_mail.typeface = SCApplication.FontMaterial()
        reset_check.typeface = SCApplication.FontMaterial()
        emailValidator = EmailValidator()

        reset_button.setOnClickListener{
            attemptReset()
        }

        val mMailListener = email_reset.onFocusChangeListener

        email_reset.setOnFocusChangeListener { view, focus ->

            mMailListener.onFocusChange(view, focus)
            if (focus) {
                change_password_form_mail.setTextColor(ContextCompat.getColor(mCtx, R.color.tittleGrey))
            } else {
                change_password_form_mail.setTextColor(ContextCompat.getColor(mCtx, R.color.formWhite))
            }
        }

        email_reset.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                change_password_form_mail.setTextColor(ContextCompat.getColor(mCtx, R.color.tittleGrey))
            }
        })
    }

    private fun attemptReset() {

        val error = checkEmailError()

        if (!error){
            change_password_form_mail.setTextColor(ContextCompat.getColor(mCtx, R.color.tittleGrey))
            checkIfEmailExist()
        }
    }

    fun checkEmailError():Boolean {
        var error = false
        if (TextUtils.isEmpty(getEmailEntered())) {
            error = true
            email_reset_text_box.setError("Please enter a valid email address.", false)
            change_password_form_mail.setTextColor(ContextCompat.getColor(mCtx, R.color.errorColor))

        }else if(!isEmailValid(getEmailEntered())){
            email_reset_text_box.setError("Please enter a valid email address.", false)
            change_password_form_mail.setTextColor(ContextCompat.getColor(mCtx, R.color.errorColor))
            error = true
        }
        return error
    }

    private fun resetSent() {

        reset_form_view.visibility = View.GONE
        reset_title.text = mCtx.getString(R.string.mail_sent)

        val stringBuilder = StringBuilder()
        stringBuilder.append(mCtx.getString(R.string.name_start))
        stringBuilder.append(" ")
        stringBuilder.append(getEmailEntered())
        stringBuilder.append(mCtx.getString(R.string.name_sub))

        reset_sub_title.text = stringBuilder
        reset_check.visibility = View.VISIBLE
        reset_button.text = mCtx.getString(R.string.button_continue)
        reset_button.setOnClickListener{
            this.finish()
        }
    }

    public fun checkIfEmailExist(){

        showProgress(true)
        val email = getEmailEntered()

        val callBack = object : VolleyPostRequestListener {

            override fun requestStarted() {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun requestCompleted(response: String?) {

                Logger.info("response for check email: " + response)
                if (response.equals("true")) {
                    getUserData()
                    //email exist go get user data
                    Logger.info("User with current email:  " + response)

                } else {

                    //email doesn't exist.
                    //hide spinner
                    //show error message

                    showProgress(false)
                    showMessage("We can't find you", "The email address $email does not exist. Please check the spelling and try again", false)

                    email_reset_text_box.setError("Please enter a valid email address.", false)
                    change_password_form_mail.setTextColor(ContextCompat.getColor(mCtx, R.color.errorColor))


                    Logger.info("User doesn't exist with this email: " + response)
                }
            }

            override fun requestEndedWithError(error: VolleyError?) {
                showProgress(false)
            }
        }

        val hashMap = HashMap<String, String>()
        hashMap.put("email", email)
        VolleyJsonHelper(mCtx).postRequestWithParams(UtilStrings.CHECK_EMAIL_API, hashMap, "Check Email", "bearer", callBack)
    }

    private fun getUserData() {

        val email = getEmailEntered()

        val callBack = object : JsonCallBack {

            override fun callbackJSONObject(result: JSONObject) {
                if (result.has(UtilStrings.STATUS_CODE)) {
                    try {
                        val statusCode = result.getInt(UtilStrings.STATUS_CODE)
                        if (statusCode == 200) {

                            val userId : String = result.getJSONObject("response").getString("id")
                            resetPassword(userId)
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

        VolleyJsonHelper(UtilStrings.GET_USER_API + email , "User TAG", callBack, mCtx).getJsonObjectFromVolleyHelper(null, null)
    }

    private fun resetPassword(userId: String) {

        val email = getEmailEntered()
        val callBack = object : VolleyPostRequestListener {

            override fun requestStarted() {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun requestCompleted(response: String?) {
//                val result = JSONObject(response)
//                val token = result.getString("access_token")
//                PreferenceHelper.getInstance(mCtx).saveToken(token)
                Logger.info("reset password successfully: $response")
                showProgress(false)
                resetSent()
            }

            override fun requestEndedWithError(error: VolleyError?) {
                showProgress(false)
                Logger.info("reset error:" + error.toString())
            }
        }

        val hashMap = HashMap<String, String>()
        hashMap.put("email", email)
        VolleyJsonHelper(mCtx).postRequestWithParams(UtilStrings.GET_USER_API + userId + "/requestpasswordreset", null, "Login", "bearer", callBack)
    }

    fun isEmailValid(email: String): Boolean {
        return emailValidator!!.validate(email)
    }

    private fun showMessage(title: String, message: String, exit: Boolean) {

        var buttonTitle = "TRY AGAIN"
        if (exit){
            buttonTitle = "OK"
        }
        val builder = MaterialDialog.Builder(this)
                .title(title)
                .content(message)
                .titleColor(ContextCompat.getColor(mCtx, R.color.alertTitle))
                .contentColor(ContextCompat.getColor(mCtx, R.color.alertContent))
                .positiveText(buttonTitle)
                .positiveColor(ContextCompat.getColor(mCtx, R.color.alertTitle))
                .onPositive { dialog, which ->
                    if (exit) {
                        this.finish()
                    }
                }
        val dialog = builder.build()
        dialog.show()
    }

    private fun getEmailEntered():String = email_reset.text.toString()

    private fun showProgress(show: Boolean) = if (show) {
        email_reset.isEnabled = false
        change_password_progress_layout.visibility = View.VISIBLE
        change_password_avi.smoothToShow()
    } else {
        email_reset.isEnabled = true
        change_password_progress_layout.visibility = View.GONE
        change_password_avi.smoothToHide()
    }
}
