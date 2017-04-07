package com.daliammao.ptr.utils;

import android.content.Context;

import com.daliammao.ptr.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: zhoupengwei
 * @time:16/3/4-下午5:52
 * @Email: 496946423@qq.com
 * @desc:
 */
public class TimeUtil {
    private static SimpleDateFormat sDataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getFriendlyUpdateTime(Context context,long lastUpdateTime) {
        if (lastUpdateTime == -1) {
            return null;
        }
        long diffTime = new Date().getTime() - lastUpdateTime;
        int seconds = (int) (diffTime / 1000);
        if (diffTime < 0) {
            return null;
        }
        if (seconds <= 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(context.getString(R.string.cube_ptr_last_update));

        if (seconds < 60) {
            sb.append(seconds + context.getString(R.string.cube_ptr_seconds_ago));
        } else {
            int minutes = (seconds / 60);
            if (minutes > 60) {
                int hours = minutes / 60;
                if (hours > 24) {
                    Date date = new Date(lastUpdateTime);
                    sb.append(sDataFormat.format(date));
                } else {
                    sb.append(hours + context.getString(R.string.cube_ptr_hours_ago));
                }

            } else {
                sb.append(minutes + context.getString(R.string.cube_ptr_minutes_ago));
            }
        }
        return sb.toString();
    }
}
