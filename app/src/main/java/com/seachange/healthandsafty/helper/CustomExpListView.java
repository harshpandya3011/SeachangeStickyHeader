package com.seachange.healthandsafty.helper;

import android.content.Context;
import android.util.AttributeSet;

import com.seachange.healthandsafty.helper.View.AnimatedExpandableListView;

public class CustomExpListView extends AnimatedExpandableListView {

    public CustomExpListView(Context context){
        super(context);
    }

    public CustomExpListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomExpListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
//        widthMeasureSpec = MeasureSpec.makeMeasureSpec(960, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(20000, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
