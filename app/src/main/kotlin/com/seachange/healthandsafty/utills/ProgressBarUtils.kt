package com.seachange.healthandsafty.utills

import android.animation.ObjectAnimator
import android.os.Build
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar

const val PROGRESS_ANIM_DURATION = 80
private val PROGRESS_ANIM_INTERPOLATOR = DecelerateInterpolator()
const val PROPERTY_PROGRESS = "progress"
fun ProgressBar.setProgressAndAnimate(progress: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        setProgress(progress, true)
    } else {
        ObjectAnimator.ofInt(this, PROPERTY_PROGRESS, progress).apply {
            setAutoCancel(true)
            duration = PROGRESS_ANIM_DURATION.toLong()
            interpolator = PROGRESS_ANIM_INTERPOLATOR
        }.start()
    }
}