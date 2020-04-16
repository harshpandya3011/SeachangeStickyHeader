package com.seachange.healthandsafty.helper;

import android.app.TimePickerDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.seachange.healthandsafty.view.TimerUpdateView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TimeSetter implements View.OnFocusChangeListener, TimePickerDialog.OnTimeSetListener, View.OnClickListener {

    private EditText mEditText;
    private Calendar mCalendar;
    private SimpleDateFormat mFormat;
    private TextView mTv;
    private boolean isEdit;
    private TimerUpdateView mView;

    public TimeSetter(EditText editText, TimerUpdateView timerUpdateView){
        isEdit = true;
        this.mEditText = editText;
        mEditText.setOnFocusChangeListener(this);
        mEditText.setOnClickListener(this);
        mView = timerUpdateView;
    }

    public TimeSetter(TextView tv, TimerUpdateView timerUpdateView){
        isEdit = false;
        mTv = tv;
        mTv.setOnFocusChangeListener(this);
        mTv.setOnClickListener(this);
        mView = timerUpdateView;
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (hasFocus){
            showPicker(view);
        }
    }

    @Override
    public void onClick(View view) {
        showPicker(view);
    }

    private void showPicker(View view) {
        if (mCalendar == null)
            mCalendar = Calendar.getInstance();

        int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = mCalendar.get(Calendar.MINUTE);

        new TimePickerDialog(view.getContext(), this, hour, minute, true).show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mCalendar.set(Calendar.MINUTE, minute);

        if (mFormat == null)
            mFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        if (isEdit) {
            this.mEditText.setText(mFormat.format(mCalendar.getTime()));
        } else {
            this.mTv.setText(mFormat.format(mCalendar.getTime()));
        }

        mView.timerUpdated(hourOfDay, minute, mCalendar);
    }

    public TextView getmTv() {
        return mTv;
    }

    public void setmTv(TextView mTv) {
        this.mTv = mTv;
    }

    public void initCalendar(int hourOfDay, int minute){
        if (mCalendar == null) mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mCalendar.set(Calendar.MINUTE, minute);
    }

    public void setHour(int hourOfDay) {
        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);

    }

    public int getCurrentHour() {
        return mCalendar.get(Calendar.HOUR);
    }

    public int getCurrentMinute() {
        return mCalendar.get(Calendar.MINUTE);
    }
    public void setMinTime(String temp) {
        if (mFormat == null)
            mFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        if (isEdit) {
            this.mEditText.setText(mFormat.format(mCalendar.getTime()));
        } else {
            this.mTv.setText(mFormat.format(mCalendar.getTime()));
        }
    }

    public void setMinTimeString(String temp) {
        if (isEdit) {
            this.mEditText.setText(temp);
        } else {
            this.mTv.setText(temp);
        }
    }

    public void setCurrentCalendar(Calendar date) {
        mCalendar = date;
    }

    public Calendar getCurrentCalendar() {
        return mCalendar;
    }
}
