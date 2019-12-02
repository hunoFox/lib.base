package com.hunofox.baseFramework.utils;

import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 项目名称：kcwxApp
 * 项目作者：胡玉君
 * 创建日期：2017/10/18 15:55.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */

public class EncryptUtils {


    /**
     * 计算MD5
     *
     * @param sourceString 原报文
     * @return MD5结果
     * @throws NoSuchAlgorithmException
     */
    public static String MD5Encode(String sourceString) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");

        //二进制字符转hex
        byte[] bytes = md.digest(sourceString.getBytes());
        StringBuffer buf = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            if (((int) bytes[i] & 0xff) < 0x10) {
                buf.append("0");
            }
            buf.append(Long.toString((int) bytes[i] & 0xff, 16));
        }
        return buf.toString();
    }


    public static String md5sum(FileInputStream fileInputStream) {
        byte[] buffer = new byte[1024];
        int numRead = 0;
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            while ((numRead = fileInputStream.read(buffer)) > 0) {
                md5.update(buffer, 0, numRead);
            }
            fileInputStream.close();
            return MD5(md5.digest());
        } catch (Exception e) {
            System.out.println("error");
            return null;
        }
    }

    public static String MD5(byte[] md) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

}
