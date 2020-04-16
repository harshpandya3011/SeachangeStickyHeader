package com.seachange.healthandsafty.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateUtils;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kevinsong on 24/10/2017.
 */

public class Util {

    public Util() {

    }

    public static String todayDate() {
        DateFormat df = new SimpleDateFormat("dd MMM", Locale.getDefault());
        return df.format(Calendar.getInstance().getTime());
    }

    public String todayDateWithMonth() {
        DateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return df.format(Calendar.getInstance().getTime());
    }

    public String todayDayDateWithMonth() {
        DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault());
        return df.format(Calendar.getInstance().getTime());
    }

    public void playBeep(Context mCtx) {
        MediaPlayer m = new MediaPlayer();
        try {
            if (m.isPlaying()) {
                m.stop();
                m.release();
                m = new MediaPlayer();
            }

            AssetFileDescriptor descriptor = mCtx.getAssets().openFd("beep.mp3");
            m.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            m.prepare();
            m.setVolume(1f, 1f);
            m.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean checkDate(String checkTime, int interval) {

        Date current = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        try {
            Date checkDate = sdf.parse(checkTime);
            Date checkDoneBy = addMinutesToDate(interval, checkDate);
            if (current.before(checkDoneBy)) return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean zoneCheckDate(int interval, String scheduleTime) {
        Date current = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        try {
            Date startCheckDate = sdf.parse(scheduleTime);
            Date checkDoneBy = addMinutesToDate(interval, startCheckDate);
            if (current.after(startCheckDate) && current.before(checkDoneBy)) return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Date addMinutesToDate(int minutes, Date beforeTime) {
        final long ONE_MINUTE_IN_MILLIS = 60000;//millisecs
        long curTimeInMs = beforeTime.getTime();
        return new Date(curTimeInMs + (minutes * ONE_MINUTE_IN_MILLIS));
    }

    public Gson gsonHelper() {
        return new Gson();
    }

    public boolean isToday(String checkTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        try {
            Date checkDate = sdf.parse(checkTime);
            if (DateUtils.isToday(checkDate.getTime())) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getTodayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        return sdf.format(Calendar.getInstance().getTime());
    }

    public boolean isNumeric(String strNum) {
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }

    public boolean isTimeBetweenTwoTime(String argStartTime,
                                        String argEndTime, String argCurrentTime) throws ParseException {
        String reg = "^([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";
        //
        if (argStartTime.matches(reg) && argEndTime.matches(reg)
                && argCurrentTime.matches(reg)) {
            boolean valid = false;
            // Start Time
            java.util.Date startTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                    .parse(argStartTime);
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(startTime);

            // Current Time
            java.util.Date currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                    .parse(argCurrentTime);
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTime(currentTime);

            // End Time
            java.util.Date endTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                    .parse(argEndTime);
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(endTime);

            //
            if (currentTime.compareTo(endTime) < 0) {

                currentCalendar.add(Calendar.DATE, 1);
                currentTime = currentCalendar.getTime();

            }

            if (startTime.compareTo(endTime) < 0) {

                startCalendar.add(Calendar.DATE, 1);
                startTime = startCalendar.getTime();

            }
            //
            if (currentTime.before(startTime) || currentTime.equals(startTime)) {

                System.out.println(" Time is Lesser ");

                valid = false;
            } else {

                if (currentTime.after(endTime) || currentTime.equals(endTime)) {
                    endCalendar.add(Calendar.DATE, 1);
                    endTime = endCalendar.getTime();

                }

                System.out.println("Comparing , Start Time /n " + startTime);
                System.out.println("Comparing , End Time /n " + endTime);
                System.out
                        .println("Comparing , Current Time /n " + currentTime);

                if (currentTime.before(endTime)) {
                    System.out.println("RESULT, Time lies b/w");
                    valid = true;
                } else {
                    valid = false;
                    System.out.println("RESULT, Time does not lies b/w");
                }

            }
            return valid;

        } else {
            throw new IllegalArgumentException(
                    "Not a valid time, expecting HH:MM:SS format");
        }

    }

    public String currentTimeInHourMinuteSecond() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("HH:MM:SS", Locale.getDefault());
        return df.format(c);
    }
}
