package com.seachange.healthandsafty.helper.interfacelistener;

public interface DateDialogView {
    void onDialogPositiveClicked(String date, boolean today, int dayNumber);
    void onDialogNegativeClicked();
}
