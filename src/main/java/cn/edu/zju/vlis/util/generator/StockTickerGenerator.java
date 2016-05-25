package cn.edu.zju.vlis.util.generator;

import cn.edu.zju.vlis.util.MathHelper;
import cn.edu.zju.vlis.util.generator.eventbean.StockTick;
import cn.edu.zju.vlis.util.RandomHelper;

/**
 * Created by wangxiaoyi on 16/4/27.
 */
public class StockTickerGenerator extends StreamEventGenerator<StockTick> {

    private String [] names = {"S1", "S2", "S3","S4","S5"};

    public StockTickerGenerator(){
    }
    /**
     * generate next event object
     *
     * @return next event
     */
    @Override
    public StockTick next() {
        StockTick tick = new StockTick(names[RandomHelper.getIntFromRange(0, 4)]);
        tick.setTime(System.currentTimeMillis());
        tick.setPrice(MathHelper.getDouble(RandomHelper.getDoubleFromRange(34, 39), "#.##"));
        return tick;
    }

    public static void main(String []args){
        StreamEventGenerator generator = new StockTickerGenerator();
        int i = 0;
        while (i ++ < 100){
            System.out.println(generator.next());
        }
    }
}
