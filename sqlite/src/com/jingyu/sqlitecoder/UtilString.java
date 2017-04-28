package com.jingyu.sqlitecoder;

/**
 * Created by jingyu on 2017/2/21.
 */
public class UtilString {

    /**
     * 获取origin中第一个simbol之后的字符串
     */
    public static String getAfterFirstSimbolString(String origin, String simbol) {
        int index = origin.indexOf(simbol);
        if (index >= 0) {
            if (index + simbol.length() == origin.length()) {
                return "";
            } else {
                return origin.substring(index + simbol.length(), origin.length()).trim();
            }
        }
        return origin;
    }

    /**
     * 获取origin中最后一个simbol之后的字符串
     */
    public static String getAfterLastSimbolString(String origin, String symbol) {
        int index = origin.lastIndexOf(symbol);
        if (index >= 0) {
            if (index + symbol.length() == origin.length()) {
                return "";
            } else {
                return origin.substring(index + symbol.length(), origin.length()).trim();
            }
        }
        return origin;
    }

    /**
     * 去除最后一个符号后面的
     */
    public static String getBeforeLastSimbolString(String origin, String symbol) {
        int position = origin.lastIndexOf(symbol);
        if (position > 0) {
            return origin.substring(0, position).trim();
        } else if (position == 0) {
            return "";
        }
        return origin;
    }

    /**
     * 设置第一个字母为大写
     */
    public static String setFirstLetterBig(String origin) {

        char[] chars = origin.toCharArray();

        if (chars[0] >= 97 && chars[0] <= 122) {
            chars[0] = (char) (chars[0] - 32);
        }

        return new String(chars);

    }

    /**
     * 设置第一个字母为小写写
     */
    public static String setFirstLetterSmall(String origin) {

        char[] chars = origin.toCharArray();

        if (chars[0] >= 65 && chars[0] < 90) {
            chars[0] = (char) (chars[0] + 32);
        }
        return new String(chars);
    }

}
