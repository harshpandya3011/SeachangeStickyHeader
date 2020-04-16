package com.seachange.healthandsafty.fragment;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.text.format.DateUtils;
import android.widget.DatePicker;

import com.seachange.healthandsafty.R;
import com.seachange.healthandsafty.helper.interfacelistener.DateDialogView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MyDatePickerFragment extends DialogFragment {

    private DateDialogView mListener;
    private Dialog datePickerDialog;
    private static final String ARG_YEAR = "year";
    private static final String ARG_MONTH= "month";
    private static final String ARG_DAY = "day";
    private int year, month, day;

    public static MyDatePickerFragment newInstance(int y, int m, int d) {

        MyDatePickerFragment fragment = new MyDatePickerFragment();
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle);

        Bundle args = new Bundle();
        args.putInt(ARG_YEAR, y);
        args.putInt(ARG_MONTH, m);
        args.putInt(ARG_DAY, d);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (getArguments() != null) {
            year = getArguments().getInt(ARG_YEAR);
            month = getArguments().getInt(ARG_MONTH);
            day = getArguments().getInt(ARG_DAY);
        }else {
            final Calendar c = Calendar.getInstance();
             year = c.get(Calendar.YEAR);
             month = c.get(Calendar.MONTH);
             day = c.get(Calendar.DAY_OF_MONTH);
        }

        datePickerDialog = new DatePickerDialog(getActivity(), dateSetListener, year, month, day);
        return datePickerDialog;
    }

    private DatePickerDialog.OnDateSetListener dateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int month, int day) {
                    Calendar calendar = Calendar.getInstance();
                    Calendar tmp = Calendar.getInstance();

                    calendar.set(year, month, day);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
                    String formatedDate = sdf.format(calendar.getTime());

                    boolean today = DateUtils.isToday(calendar.getTimeInMillis());

                    int dayNumber;
                    if (today) {
                        dayNumber = 0;
                    }else if (calendar.before(tmp)) {
                        dayNumber = 1;
                    }else{
                        dayNumber = 2;
                    }

                    mListener.onDialogPositiveClicked(formatedDate, today, dayNumber);
                }
            };

    public DateDialogView getmListener() {
        return mListener;
    }

    public void setmListener(DateDialogView mListener) {
        this.mListener = mListener;
    }

}
