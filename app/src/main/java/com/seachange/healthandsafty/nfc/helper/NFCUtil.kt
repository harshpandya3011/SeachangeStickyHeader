package com.seachange.healthandsafty.nfc.helper

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.util.Log
import com.seachange.healthandsafty.R
import java.io.IOException

object NFCUtil {

    fun createNFCMessage(payload: String, intent: Intent?, activity: Activity): Pair<Boolean,String> {

        var isNfcMsgCreate:Pair<Boolean,String> = Pair(false, activity.applicationContext.resources.getString(R.string.message_tap_tag))
        val pathPrefix = "seachange.com:caygo"
        val nfcRecord = NdefRecord(NdefRecord.TNF_EXTERNAL_TYPE, pathPrefix.toByteArray(), ByteArray(0), payload.toByteArray())
        val nfcMessage = NdefMessage(arrayOf(nfcRecord))
        intent?.let {
            val tag = it.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            return writeMessageToTag(nfcMessage, tag,activity)
        }
        return isNfcMsgCreate
    }

    fun retrieveNFCMessage(intent: Intent?): String {
        intent?.let {
            if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
                val nDefMessages = getNDefMessages(intent)
                nDefMessages[0].records?.let {
                    it.forEach {
                        it?.payload.let {
                            it?.let {
                                return String(it)

                            }
                        }
                    }
                }

            } else {
                return "Touch NFC tag to read data"
            }
        }
        return "Touch NFC tag to read data"
    }


    private fun getNDefMessages(intent: Intent): Array<NdefMessage> {

        val rawMessage = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
        rawMessage?.let {
            return rawMessage.map {
                it as NdefMessage
            }.toTypedArray()
        }
        // Unknown tag type
        val empty = byteArrayOf()
        val record = NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty)
        val msg = NdefMessage(arrayOf(record))
        return arrayOf(msg)
    }

    fun disableNFCInForeground(nfcAdapter: NfcAdapter, activity: Activity) {
        nfcAdapter.disableForegroundDispatch(activity)
    }

    fun <T> enableNFCInForeground(nfcAdapter: NfcAdapter, activity: Activity, classType: Class<T>) {
        val pendingIntent = PendingIntent.getActivity(activity, 0,
                Intent(activity, classType).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)
        val nfcIntentFilter = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
        val filters = arrayOf(nfcIntentFilter)

        val TechLists = arrayOf(arrayOf(Ndef::class.java.name), arrayOf(NdefFormatable::class.java.name))

        nfcAdapter.enableForegroundDispatch(activity, pendingIntent, filters, TechLists)
    }


    private fun writeMessageToTag(nfcMessage: NdefMessage, tag: Tag?, activity: Activity): Pair<Boolean, String> {

        try {
            val nDefTag = Ndef.get(tag)

            nDefTag?.let {
                it.connect()
                if (it.maxSize < nfcMessage.toByteArray().size) {
                    //Message to large to write to NFC tag
                    Log.d("NFC: " , "too large to write: " +  nfcMessage.toByteArray().size + "tag size" + it.maxSize )
                    return Pair(false,activity.applicationContext.resources.getString(R.string.message_tap_tag))
                }
                if (it.isWritable) {
                    it.writeNdefMessage(nfcMessage)
                    it.close()
                    //Message is written to tag
                    return Pair(true,"")
                } else {
                    //NFC tag is read-only
                    Log.d("NFC: " , "read only")
                    return Pair(false,activity.applicationContext.resources.getString(R.string.message_tap_tag))
                }
            }

            val nDefFormatableTag = NdefFormatable.get(tag)

            nDefFormatableTag?.let {
                return try {
                    it.connect()
                    it.format(nfcMessage)
                    it.close()
                    //The data is written to the tag
                    Pair(true,"")
                } catch (e: IOException) {
                    //Failed to format tag
                    Log.d("NFC: " , "failed to format: "+e.printStackTrace())
                    Log.d("NFC: " , "failed to format msg: "+e.message)
                    Pair(false,activity.applicationContext.resources.getString(R.string.message_tap_tag))
                }
            }
            //NDEF is not supported
            Log.d("NFC: " , "not supported")

            return Pair(false,activity.applicationContext.resources.getString(R.string.message_tap_tag))

        } catch (e: Exception) {
            //Write operation has failed connection lost
            Log.d("NFC: " , "operation failed"+e.printStackTrace())
            Log.d("NFC: " , "operation msg"+e.message)
            if(e is IOException && e.message.isNullOrEmpty()){
                //moved card
                return Pair(false,activity.applicationContext.resources.getString(R.string.tag_connection_lost_message))
            }else if(e is IOException && !e.message.isNullOrEmpty()){
                // nfc connection lost
                return Pair(false, e.message.toString())
            }else{
                Log.d("NFC: " , "failed... unknown reason")
                return Pair(false,activity.applicationContext.resources.getString(R.string.message_tap_tag))
            }

        }
//        return false
    }
}