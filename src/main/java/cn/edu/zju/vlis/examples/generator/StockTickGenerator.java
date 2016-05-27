package cn.edu.zju.vlis.examples.generator;

import cn.edu.zju.vlis.examples.generator.eventbean.StockTick;
import cn.edu.zju.vlis.util.MathHelper;
import cn.edu.zju.vlis.util.RandomHelper;

/**
 * Created by wangxiaoyi on 16/4/27.
 */
public class StockTickGenerator extends StreamEventGenerator<StockTick> {

    private String [] names = {"S1", "S2", "S3","S4","S5"};

    public StockTickGenerator(){
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
        StreamEventGenerator generator = new StockTickGenerator();
        int i = 0;
        while (i ++ < 100){
            System.out.println(generator.next());
        }
    }
}
