package com.seachange.healthandsafty.presenter

import com.seachange.healthandsafty.helper.PassCodeHelper
import com.seachange.healthandsafty.model.UserData
import com.seachange.healthandsafty.view.PassCodeView

class PassCodePresenter(private val passCodeView: PassCodeView) {

    companion object {
        const val TAG = "PassCodePresenter"
    }

    fun validatePassCode(hashed: String?, pinCode: String?, user: UserData, isConnected: Boolean) {

        if(isConnected) {
            if (isTempPassCodeEntered(pinCode, user)) {
                passCodeView.onTempPassCode()
            } else {
                val result = PassCodeHelper().isValidPassCode(hashed, pinCode)
                if (result) passCodeView.onValidSuccessful()
                else passCodeView.onValidFailed()
            }
        } else {
            val result = PassCodeHelper().isValidPassCode(hashed, pinCode)
            if (result) passCodeView.onValidSuccessful()
            else passCodeView.onValidFailed()
        }
    }

    private fun isTempPassCodeEntered(pinCode: String?, user: UserData): Boolean {
        return user.isPasscodeResetRequired == true && pinCode == user.temporaryPasscode
    }
}
