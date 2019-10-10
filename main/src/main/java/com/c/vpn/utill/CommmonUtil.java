package com.c.vpn.utill;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommmonUtil {
    public static final String REG_NUMBER = ".*\\d+.*";
    public static final String REG_UPPERCASE = ".*[A-Z]+.*";
    public static final String REG_LOWERCASE = ".*[a-z]+.*";


    public static void saveId(String id,Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("id",id);
        editor.commit();
    }

    public static String getID(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return sharedPreferences.getString("id","");
    }


    public static void savePassword(String password,Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("password",password);
        editor.commit();
    }

    public static String getPassword(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return sharedPreferences.getString("password","");
    }



    public static boolean checkPasswordRule(String password){
        //密码为空或者长度小于8位则返回false
        if (password == null || password.length() <6 ) return false;
        int i = 0;
        if (password.matches(REG_NUMBER)) i++;
        if (password.matches(REG_LOWERCASE))i++;
        if (password.matches(REG_UPPERCASE)) i++;
        if (i  != 3 )  return false;
        return true;
    }


    public static boolean isEmail(String email) {
        if (email == null)
            return false;
        String rule = "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile(rule);
        matcher = pattern.matcher(email);
        if (matcher.matches())
            return true;
        else
            return false;
    }


    private final static long minute = 60 * 1000;// 1分钟
    private final static long hour = 60 * minute;// 1小时
    private final static long day = 24 * hour;// 1天
    private final static long month = 31 * day;// 月
    private final static long year = 12 * month;// 年

    /**
     * 返回文字描述的日期
     *
     * @param time
     * @return
     */
    public static String getTimeFormatText(String time) {
        if (time == null) {
            return "";
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = formatter.parse(dealDateFormat(time));
            long diff = date.getTime() -  new Date().getTime();
            long r = 0;
            if (diff > year) {
                r = (diff / year);
                return r + "+year";
            }
            if (diff > month) {
                r = (diff / month);
                return r + "+month";
            }
            if (diff > day) {
                r = (diff / day);
                return r + "+day";
            }
            if (diff > hour) {
                r = (diff / hour);
                return r + "+hour";
            }
            if (diff > minute) {
                r = (diff / minute);
                return r + "+minute";
            }
            return "";
        }catch (Exception e){
            return "";
        }
    }



    public static String dealDateFormat(String oldDate) {
        Date date1 = null;
        DateFormat df2 = null;
        try {
            oldDate= oldDate.replace("Z", " UTC");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
            Date date = df.parse(oldDate);
            SimpleDateFormat df1 = new SimpleDateFormat ("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
            date1 = df1.parse(date.toString());
            df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return df2.format(date1);
    }

}
