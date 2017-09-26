package com.neighbours.neighbours.Util;

import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by U on 1/5/2016.
 */
public class TimeUtil {

    public static CharSequence getHumanReadableTime(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            long timeLong = sdf.parse(time).getTime();
            long now = System.currentTimeMillis();
            return DateUtils.getRelativeTimeSpanString(timeLong, now, DateUtils.SECOND_IN_MILLIS);
        } catch (ParseException e) {
            e.printStackTrace();
            return time;
        }
    }
}
