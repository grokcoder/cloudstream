package cn.edu.zju.vlis.ptest.eventbus;

import cn.edu.zju.vlis.eventhub.EventBusKafkaProducer;
import cn.edu.zju.vlis.eventhub.EventData;
import cn.edu.zju.vlis.eventhub.IEventBusProducer;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.Properties;

/**
 * Created by wangxiaoyi on 2016/12/19.
 */
public class EventSender implements Runnable{

    private static final Logger LOG = Logger.getLogger(EventSender.class);
    private int maxEventNum = 0;
    private int partitionNum = 1;
    private String connString = "localhost:9092";
    private int id = 0;
    private IEventBusProducer<EventData> producer;
    private boolean isInit = false;
    private String eventName = "";

    private  EventData event;

    public EventSender(int maxEventNum, int partitionNum, String connString, int id, String eventName){
        this.maxEventNum = maxEventNum;
        this.partitionNum = partitionNum;
        this.connString = connString;
        this.id = id;
        this.eventName = eventName;
    }

    public void init(){
        Properties props = new Properties();
        props.put("bootstrap.servers", connString);
        props.put("client.id", "EventProducer");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        producer = new EventBusKafkaProducer(props, partitionNum);
        event = new EventData(eventName);
        event.addData("age", 1);
        isInit = true;
    }

    @Override
    public void run() {
        if(! isInit) init();
        int count = 1;
        LOG.info("start sending " + id);
        LOG.error("start time:  " + new Date(System.currentTimeMillis()));
       // long startTime = System.currentTimeMillis();
        while (count <= maxEventNum){
            count ++;
            event.getDataMap().put("age", count);
            producer.sendAsync(event, event.getEventSchemaName());
            //producer.send(event, event.getEventSchemaName());
            if(count % 10000 == 0)
                LOG.info("send " + event);
        }
        //long endTime = System.currentTimeMillis();
        //LOG.info("sending done, total tps : " + ((1000.0 * maxEventNum) /(1.0 * (endTime - startTime))));

    }
}
