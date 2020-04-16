package com.seachange.healthandsafty.presenter

import android.content.Context
import android.widget.LinearLayout
import com.seachange.healthandsafty.view.RASelectOptionView

public class RASelectOptionPresenter(private val mView: RASelectOptionView) {

    fun getLayoutParams(screenWidth:Int, context: Context) {

        val padding_in_dp = 24
        val scale = context.resources.displayMetrics.density
        val padding_in_px = (padding_in_dp * scale + 0.5f).toInt()
        val height = screenWidth / 2 - padding_in_px

        val layoutParams = LinearLayout.LayoutParams(height, height)

        val margin_in_dp = 10
        val scale_margin = context.getResources().getDisplayMetrics().density
        val margin_in_px = (margin_in_dp * scale_margin + 0.5f).toInt()
        layoutParams.setMargins(margin_in_px,margin_in_px  ,margin_in_px  ,margin_in_px)

        mView.optionViewParam(layoutParams)
    }
}