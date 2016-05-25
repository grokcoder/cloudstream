package cn.edu.zju.vlis.util;

import java.text.DecimalFormat;

/**
 * Created by wangxiaoyi on 16/4/27.
 */
public class MathHelper {

    public static double getDouble(double origin, String formate){
        DecimalFormat df = new DecimalFormat(formate);
        return Double.parseDouble(df.format(origin));
    }
}
