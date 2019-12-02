package com.hunofox.baseFramework.utils;

import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;

import java.io.IOException;

/**
 * 项目名称：ProprietorVote2.0
 * 项目作者：胡玉君
 * 创建日期：2018/4/20 10:27.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */

public class LocationUtils {


    /**
     * 获取图片/文件的经纬度信息
     */
    public static double[] getlocation(String path) {
        float output1 = 0;
        float output2 = 0;

        Location location;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            String latValue = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String latRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            String lngValue = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            String lngRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
            if (latValue != null && latRef != null && lngValue != null && lngRef != null) {
                try {
                    output1 = convertRationalLatLonToFloat(latValue, latRef);
                    output2 = convertRationalLatLonToFloat(lngValue, lngRef);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(output1);
        location.setLongitude(output2);
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        double[] f = {lat, lng};
        return f;
    }

    //向文件中保存经纬度信息
    public static boolean saveLocation(String path, double lat, double lng) {
        try {

            String latStr = dblToLocation(lat);
            String lngStr = dblToLocation(lng);

            if(latStr == null || lngStr == null) return false;
            latStr = latStr.replace("°", "/1,").replace("′", "/1,").replace("″", "/1");
            lngStr = lngStr.replace("°", "/1,").replace("′", "/1,").replace("″", "/1");

            ExifInterface exif = new ExifInterface(path);

            //设置经纬度，TAG是可以自定义的
            if (lat != 0 || lng != 0) {
                exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, lngStr);
                exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, lng > 0 ? "E" : "W");
                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, latStr);
                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, lat > 0 ? "N" : "S");
                exif.saveAttributes();
                return true;
            } else {
                return false;
            }

        } catch (IOException e) {
            return false;
        } catch (UnsupportedOperationException e){
            //图片类型不支持，仅支持jpg格式图片
            return false;
        }
    }

    //------------------------------------------ 以下为辅助方法 -------------------------------------------------------//
    private static float convertRationalLatLonToFloat(
            String rationalString, String ref) {
        try {
            String[] parts = rationalString.split(",");

            String[] pair;
            pair = parts[0].split("/");
            double degrees = Double.parseDouble(pair[0].trim())
                    / Double.parseDouble(pair[1].trim());

            pair = parts[1].split("/");
            double minutes = Double.parseDouble(pair[0].trim())
                    / Double.parseDouble(pair[1].trim());

            pair = parts[2].split("/");
            double seconds = Double.parseDouble(pair[0].trim())
                    / Double.parseDouble(pair[1].trim());

            double result = degrees + (minutes / 60.0) + (seconds / 3600.0);
            if ((ref.equals("S") || ref.equals("W"))) {
                return (float) -result;
            }
            return (float) result;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException();
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException();
        }
    }

    //经纬度转换为时分秒
    public static String dblToLocation(double data) {
        String ret_s = "";
        int tmp_i_du = (int) data;
        ret_s = String.valueOf(tmp_i_du) + "°";
        //度小数部分
        double tmp_d_du = data - tmp_i_du;
        int tmp_i_fen = (int) (tmp_d_du * 60);
        ret_s = ret_s.concat(String.valueOf(tmp_i_fen) + "′");
        double tmp_d_fen = tmp_d_du * 60 - tmp_i_fen;
        int tmp_i_miao = (int) (tmp_d_fen * 60);
        ret_s = ret_s.concat(String.valueOf(tmp_i_miao) + "″");
        return ret_s;
    }

}
