package com.seachange.healthandsafty.nfc.helper

import android.app.Activity
import android.content.Intent
import android.nfc.NfcAdapter

class NFCHelper {

    fun writeNFCMessage(payload: String, intent: Intent?, activity: Activity): Pair<Boolean, String> {
        return NFCUtil.createNFCMessage(payload, intent,activity)
    }

    fun enableNFCInForeground(nfcAdapter: NfcAdapter, activity: Activity) {
        NFCUtil.enableNFCInForeground(nfcAdapter, activity, javaClass)
    }

    fun disableNFCInForeground(nfcAdapter: NfcAdapter, activity: Activity) {
        NFCUtil.disableNFCInForeground(nfcAdapter, activity)
    }
}