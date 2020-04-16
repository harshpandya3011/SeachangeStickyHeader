package com.seachange.healthandsafty.helper

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import android.util.Log
import com.seachange.healthandsafty.view.ForegroundBackgroundView

class ForegroundBackgroundListener(private val mView: ForegroundBackgroundView) : LifecycleObserver {


    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun startSomething() {
        Log.v("ProcessLog", "APP IS ON FOREGROUND")
        mView.backForeground()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stopSomething() {
        Log.v("ProcessLog", "APP IS IN BACKGROUND")
        mView.wentBackground()
    }
}