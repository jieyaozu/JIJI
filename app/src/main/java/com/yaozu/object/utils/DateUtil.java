package com.yaozu.object.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by jieyz on 2016/4/22.
 */
public class DateUtil {
    /**
     * 计算两个日期之间相差的天数
     *
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数
     * @throws ParseException
     */
    public static int daysBetween(Date smdate, Date bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        smdate = sdf.parse(sdf.format(smdate));
        bdate = sdf.parse(sdf.format(bdate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 比较字符串的日期格式的计算
     */
    public static int daysBetween(String smdate, String bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(smdate));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(bdate));
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 根据时间戳生成指定格式的日期字符串
     * yyyy-MM-dd
     *
     * @param position
     * @return
     */
    public static String generateTime(long position) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date(position));
    }

    /**
     * 根据时间戳生成指定格式的日期字符串
     *
     * @param position
     * @return
     */
    public static String generateTime(long position, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(new Date(position));
    }

    public static String generateDateOfTime(long position) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(position));
    }

    /**
     * 得到指定日期的前几天
     *
     * @param time 指定日期
     * @param days 前几天
     * @return
     * @throws ParseException
     */
    public static String getBeforeDate(String time, int days) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(time);
        Date dBefore = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, -days);  //设置为前一天
        dBefore = cal.getTime();   //得到前一天的时间
        return sdf.format(dBefore);
    }

    public static long getTime(String time) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse(time);
        return date.getTime();
    }

    public static long getTime(String time, String pattern) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date date = sdf.parse(time);
        return date.getTime();
    }

    /**
     * 计算一天之内的时间差
     *
     * @param time1
     * @param time2
     * @return
     */
    public static long betweenTime(long time1, long time2) {
        long time = time2 - time1;
        long mm = (time / (1000 * 60));
        return mm;
    }

    /**
     * 得到和现在的j时间相对时间
     * 比如 3分钟前、2小时前
     *
     * @param publictime 这是格式化后的时间 例如：2016-06-01
     * @return
     */
    public static String getRelativeTime(String publictime) {
        String value = "";
        try {
            int cap = DateUtil.daysBetween(publictime, DateUtil.generateDateOfTime(System.currentTimeMillis()));
            if (cap == 0) {
                long minute = DateUtil.betweenTime(DateUtil.getTime(publictime), System.currentTimeMillis());
                if (minute < 60) {
                    if (minute == 0) minute = 1;
                    value = minute + "分钟前";
                } else {
                    value = minute / 60 + "小时前";
                }
            } else if (cap == 1) {
                value = "昨天" + DateUtil.generateTime(DateUtil.getTime(publictime), "HH:mm");
            } else if (cap == 2) {
                value = "前天" + DateUtil.generateTime(DateUtil.getTime(publictime), "HH:mm");
            } else {
                value = publictime;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return value;
    }
}
