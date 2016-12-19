package cn.edu.zju.vlis;

import cn.edu.zju.vlis.eventhub.EventData;
import cn.edu.zju.vlis.eventhub.IEventBusProducer;
import cn.edu.zju.vlis.eventhub.EventBusKafkaProducer;

import java.util.Properties;

/**
 * Created by wangxiaoyi on 16/11/1.
 */
public class EventsFeeder {

    public static void main(String []args){
        eventFeed();
    }

    public static void eventFeed(){
        Properties props = new Properties();// // TODO: 16/11/1 provide a config file

        //10.214.208.14:9092,10.214.208.13:9092,10.214.208.12:9092,10.214.208.11:9092
        props.put("bootstrap.servers", "cn8:9092");
        //props.put("bootstrap.servers", "172.17.0.2:9092");
        props.put("client.id", "EventProducer");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);


        IEventBusProducer<EventData> producer =
                new EventBusKafkaProducer(props, 1);


        long num = 3000000l;
        long start = System.currentTimeMillis();
        for (int i = 1; i < num; ++i){
            EventData event = new EventData("Person1");
            event.addData("name", "wangxiaoyi" + i);
            event.addData("age", i);
            System.out.println("Sending " + event);
            producer.send(event, event.getEventSchemaName());

//            try {
//                Thread.currentThread().sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
        long end = System.currentTimeMillis();
        System.out.print((end - start) /(1.0 * num));



    }
}
