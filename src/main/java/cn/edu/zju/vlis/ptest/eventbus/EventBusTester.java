package cn.edu.zju.vlis.ptest.eventbus;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wangxiaoyi on 2016/12/19.
 */
public class EventBusTester {

    public static void main(String []args){
        ExecutorService exec = Executors.newCachedThreadPool();

        for (int i = 5; i <= 5; i ++){
           // exec.submit(new EventReceiver(100000, 1, "cn8:9092", "test-event" + i));
            exec.submit(new EventSender(1000000, 8, "cn8:9092", i, "test-event" + i));
        }
        //exec.shutdown();
    }

}
