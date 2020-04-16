package com.seachange.healthandsafty.view.stickyheader;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class StickyRecyclerView extends RecyclerView {
    public StickyRecyclerView(@NonNull Context context) {
        super(context);
    }

    public StickyRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StickyRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        super.setAdapter(adapter);
        if (getAdapter() instanceof StickyListener) {
            setStickyItemDecoration();
        }
    }

    private void setStickyItemDecoration() {
//        StickyHeaderItemDecoration itemDecoration = new StickyHeaderItemDecoration(() getAdapter());
//        this.addItemDecoration(itemDecoration);
    }

}
