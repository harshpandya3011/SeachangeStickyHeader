package com.seachange.healthandsafty.helper

import android.util.Log

/**
 * Created by kevinsong on 31/10/2017.
 */

class KLogger {


    companion object {
        @JvmStatic fun info(message: String){
            Log.d("SeaChangeKotlin", message)
        }
        fun print(message: String) {
            Log.d("SeaChangeKotlin", message)
        }
    }

    public fun printInfo(message:String){
        Log.d("SeaChange", message)
    }


    private fun test() {
        val result = double(2)
        Log.d("test","fasdf")
    }

    public fun double(x: Int): Int {
        return 2*x
    }
}