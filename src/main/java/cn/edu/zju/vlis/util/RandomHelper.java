package cn.edu.zju.vlis.util;

import java.util.Random;

/**
 * Created by wangxiaoyi on 16/4/27.
 */
public class RandomHelper {

    public static int getIntFromRange(int start, int end){
        Random r = new Random(System.currentTimeMillis());
        return start + r.nextInt(end - start + 1);
    }

    public static double getDoubleFromRange(double start, double end){
        Random r = new Random(System.currentTimeMillis());
        return r.doubles(start, end).findFirst().getAsDouble();
    }
}
