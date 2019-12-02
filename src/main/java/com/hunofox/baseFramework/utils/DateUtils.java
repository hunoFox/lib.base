package com.hunofox.baseFramework.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 项目名称：ysApprove
 * 项目作者：胡玉君
 * 创建日期：2017/5/4 16:30.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */

public class DateUtils {

    /**
     * date2比date1多的天数
     */
    public static int differentDays(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2) {//同一年
            int timeDistance = 0;
            if(year1 < year2){
                for (int i = year1; i < year2; i++) {
                    if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) { // 闰年
                        timeDistance += 366;
                    } else {// 非闰年
                        timeDistance += 365;
                    }
                }
            }else{
                for (int i = year2; i < year1; i++) {
                    if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) { // 闰年
                        timeDistance -= 366;
                    } else {// 非闰年
                        timeDistance -= 365;
                    }
                }
            }

            return timeDistance + (day2 - day1);
        } else {//不同年
            return day2 - day1;
        }
    }

    public static Date formatString2Date(String date){
        if(date != null && !date.contains(" ")){
            date = date+" 00:00:00";
        }

        Date d = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            d = format.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return d;
    }

    public static String formatDate2String(Date date){
        return formatDate2String(date, true);
    }

    /**

            Locale aLocale = Locale.getDefault();
            SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            String date = s.format(System.currentTimeMillis());
            System.out.println("s  "+date);
            String s1 = SimpleDateFormat.getDateInstance().format(System.currentTimeMillis());
            String s2 = SimpleDateFormat.getDateTimeInstance().format(System.currentTimeMillis());
            System.out.println("s1 "+s1);
            System.out.println("s2 "+s2);

             这是打印结果
             s  2016-06-17 05:29
             s1 2016-6-17
             s2 2016-6-17 17:29:00

     * @param date
     * @param hasTime
     * @return
     */
    public static String formatDate2String(Date date, boolean hasTime){
        if(date == null){
            date = new Date();
        }

        String formatDate;
        if(hasTime){
            formatDate = "yyyy-MM-dd HH:mm:ss";
        }else{
            formatDate = "yyyy-MM-dd";
        }
        SimpleDateFormat format = new SimpleDateFormat(formatDate);
        return format.format(date);
    }

    /**
     * 计算传入日期 i 天前(-)/后(+) 的日期
     *
     * @param d 日期基准点
     * @param i 负数为基准点前i天的日期，正数为基准点后i天的日期
     * @return
     */
    public static String getDate(Date d, long i){
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
        return df.format(new Date(d.getTime() + i * 24 * 60 * 60 * 1000));
    }


}
