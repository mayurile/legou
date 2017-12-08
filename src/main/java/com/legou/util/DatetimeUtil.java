package com.legou.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by Administrator on 2017/11/6.
 */
public class DatetimeUtil {
    //joda-time
    public static final String STANDARD_FORMAT="yyyy-MM-dd HH:mm:ss";
    public static final String STANDARD_FORMAT_DATE="yyyy-mm-dd";

    //str->date
    public static Date strtodate(String dateTimestr,String formatStr){
        DateTimeFormatter dateTimeFormatter= DateTimeFormat.forPattern(formatStr);
        DateTime datetime=dateTimeFormatter.parseDateTime(dateTimestr);
        return datetime.toDate();
    }
    //date->str
    public static String datetostr(Date date,String formatStr){
        if(date==null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime=new DateTime(date);
        return dateTime.toString(formatStr);
    }
    public static Date strtodate(String dateTimestr){
        DateTimeFormatter dateTimeFormatter= DateTimeFormat.forPattern(STANDARD_FORMAT);
        DateTime datetime=dateTimeFormatter.parseDateTime(dateTimestr);
        return datetime.toDate();
    }
    //date->str
    public static String datetostr(Date date){
        if(date==null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime=new DateTime(date);
        return dateTime.toString(STANDARD_FORMAT);
    }
    public static long datetolong(Date date){

        if(date==null){
            return 0;
        }
        long translatetime=date.getTime()/1000;
        return translatetime;
    }
}
