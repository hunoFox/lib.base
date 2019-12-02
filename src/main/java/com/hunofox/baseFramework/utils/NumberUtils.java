package com.hunofox.baseFramework.utils;

import android.text.TextUtils;

import java.math.BigDecimal;

/**
 * 项目名称：kcwxApp
 * 项目作者：胡玉君
 * 创建日期：2017/10/20 13:56.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */

public class NumberUtils {

    /**
     * 去掉字符串后面的.0
     * @param str
     * @return
     */
    public static String deletePointZero(String str){
        if(str == null || TextUtils.isEmpty(str)) return "0";

        if(str.endsWith(".0")){
            str = str.substring(0, str.length()-2);
        }

        try{
            Integer.parseInt(str);
        }catch (NumberFormatException e){
            return "0";
        }
        return str;
    }

    /**
     * 格式化数字型字符串保留两位小数，若字符串不为数字则返回0.00
     *
     * @param number
     * @return
     */
    public static String formatDoubleDecimal(String number){
        if(number == null || number.length() <= 0){
            return "0.00";
        }

        try{
            Double.parseDouble(number);
        }catch (NumberFormatException e){
            return "0.00";
        }

        try{
            BigDecimal b = new BigDecimal(number);
            BigDecimal one = new BigDecimal("1");
            number = String.format("%.2f", b.divide(one, 2, BigDecimal.ROUND_HALF_UP).doubleValue());
        }catch (Exception e){
            return "0.00";
        }

        return number;
    }

    public static String formatDoubleDecimal(String number, int decimal){
        if(number == null || number.length() <= 0){
            return "0.00";
        }

        try{
            Double.parseDouble(number);
        }catch (NumberFormatException e){
            return "0.00";
        }

        try{
            BigDecimal b = new BigDecimal(number);
            BigDecimal one = new BigDecimal("1");
            number = String.format("%."+ decimal +"f", b.divide(one, decimal, BigDecimal.ROUND_HALF_UP).doubleValue());
        }catch (Exception e){
            return "0.00";
        }

        return number;
    }

    /**
     * 判断字符串型的数字是否大于某个值
     *
     * @param strNum
     * @param number
     * @return
     */
    public static boolean ifMoreThan(String strNum, float number){
        try{
            return Float.parseFloat(strNum) > number;
        }catch (NumberFormatException e){
            return false;
        }
    }

    /**
     * String型数字求和,若某个值无法转为float类型，则将该值设置为0
     *
     * @param args
     * @return
     */
    public static float stringSum(String...args){
        float sum = 0f;
        for(String num:args){
            try{
                sum += Float.parseFloat(num);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return sum;
    }

    /**
     * 为数字型字符串添加小数点
     *
     * @param str
     * @return
     */
    public static String addComma(String str){
        try{
            Double.parseDouble(str);
        }catch (Exception e){
            return "0.00";
        }

        if(str.contains(".")){
            int indexOf = str.indexOf(".");
            String front = str.substring(0, indexOf);
            String back = str.substring(indexOf, str.length());

            String reverseStr = new StringBuilder(front).reverse().toString();
            String strTemp = "";
            for (int i = 0; i < reverseStr.length(); i++) {
                if (i * 3 + 3 > reverseStr.length()) {
                    strTemp += reverseStr.substring(i * 3, reverseStr.length());
                    break;
                }
                strTemp += reverseStr.substring(i * 3, i * 3 + 3) + ",";
            }
            if (strTemp.endsWith(",")) {
                strTemp = strTemp.substring(0, strTemp.length() - 1);
            }
            String resultStr = new StringBuilder(strTemp).reverse().toString();
            if (3 > back.length()) {
                back = back + "0";
            }
            resultStr = resultStr + back;
            return resultStr;
        }else{
            String reverseStr = new StringBuilder(str).reverse().toString();
            String strTemp = "";
            for (int i = 0; i < reverseStr.length(); i++) {
                if (i * 3 + 3 > reverseStr.length()) {
                    strTemp += reverseStr.substring(i * 3, reverseStr.length());
                    break;
                }
                strTemp += reverseStr.substring(i * 3, i * 3 + 3) + ",";
            }
            if (strTemp.endsWith(",")) {
                strTemp = strTemp.substring(0, strTemp.length() - 1);
            }
            String resultStr = new StringBuilder(strTemp).reverse().toString();
            return resultStr;
        }
    }


}
