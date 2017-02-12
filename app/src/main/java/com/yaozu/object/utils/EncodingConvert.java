package com.yaozu.object.utils;

import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jieyz on 2016/1/13.
 */
public class EncodingConvert {
    public static String toUtf(String str) {
        if (str == null) return null;
        String retStr = str;
        byte b[];
        try {
            b = str.getBytes("iso-8859-1");
            if (b[0] < 0) {
                retStr = new String(b, "utf-8");
            }
        } catch (UnsupportedEncodingException e) {

        }
        return retStr;
    }

    public static boolean isContainsChinese(String str) {
        Pattern pat = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher matcher = pat.matcher(str);
        boolean flg = false;
        if (matcher.find()) {
            flg = true;
        }
        return flg;
    }

    public static String getRandomString(int length) { //length表示生成字符串的长度
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            if (number >= base.length()) {
                number = number - 1;
            }
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
}
