package cn.edu.zju.vlis.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wangxiaoyi on 16/4/27.
 */
public class DateHelper {

    public static String getDateFromLong(long time, String pattern){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String date = sdf.format(new Date(time));
        return date;
    }

}
