package cn.edu.zju.vlis.ptest.eventbus;

import cn.edu.zju.vlis.eventhub.EventBusSubscriber;
import cn.edu.zju.vlis.eventhub.EventData;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by wangxiaoyi on 2016/12/19.
 */
public class EventReceiver implements Runnable {

    private static Logger LOG = Logger.getLogger(EventSender.class.getSimpleName());
    private int maxEventNum = 0;
    private String eventName = "";

    private int maxPartition = 1;
    private EventBusSubscriber subscriber;
    private String connString = "localhost:9092";

    private volatile boolean isInit = false;

    public EventReceiver(int maxEventNum, int maxPartition, String connString, String eventName){
        this.maxEventNum = maxEventNum;
        this.maxPartition = maxPartition;
        this.connString = connString;
        this.eventName = eventName;
    }

    public void init(){
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, connString);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group_1");
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 1000);

        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        subscriber = new EventBusSubscriber(maxPartition, eventName, props);
        isInit = true;
    }

    @Override
    public void run() {
        if (! isInit) init();
        int count = 0;
        while (count < maxEventNum){
            List<EventData> events = subscriber.pullEvents();
            count += events.size();
            LOG.info("Receive count now " + count);
        }
        System.err.println("Receive all done, use time : " + new Date(System.currentTimeMillis()) + " second ");
    }

}
