package cn.edu.zju.vlis.examples.kafka;

import cn.edu.zju.vlis.eventhub.EventData;
import cn.edu.zju.vlis.eventhub.EventSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Created by wangxiaoyi on 16/6/17.
 */
public class KafkaProducerTest {
    private KafkaProducer<Integer, byte[]> producer;
    private final String topic = "Person3";

    public void init() {
        Properties props = new Properties();
        //10.214.208.14:9092,10.214.208.13:9092,10.214.208.12:9092,10.214.208.11:9092
        props.put("bootstrap.servers", "172.16.0.9:9092,cn8:9092,cn7:9092");
        props.put("client.id", "DemoProducer");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
        //props.put("value.serializer", "org.apache.kafka.common.serialization.BytesSerializer");

        props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");

       // org.apache.kafka.common.serialization.ByteArraySerializer
        producer = new KafkaProducer<>(props);
    }


    public void start() {
        int messageNum = 100000;
        while (true) {
            String messageStr = "Message_" + messageNum++;
            //long startTime = System.currentTimeMillis();
            try {
                //producer.send(new ProducerRecord<>(topic, messageStr)).get();

                EventData eventData = new EventData("Person1");
                eventData.addData("name", "wangxiaoyi");
                eventData.addData("age", 123);


                producer.send(new ProducerRecord<>(topic, messageNum, EventSerializer.toBytes(eventData))).get();

                System.out.println("Sent message: (" + messageNum + ", " + messageStr + ")");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String []args){

        KafkaProducerTest test = new KafkaProducerTest();
        test.init();
        test.start();

    }
}
