package com.codepath.apps.simpletweets.helper;

import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yingbwan on 9/23/2017.
 */

public class MyUtils {

    public static String convertMDYToYMD(String mdy){
        SimpleDateFormat src = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat dst = new SimpleDateFormat("yyyyMMdd");
        Date tmDate=null;
        try {
            tmDate = src.parse(mdy);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return tmDate==null?null:dst.format(tmDate);
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}
