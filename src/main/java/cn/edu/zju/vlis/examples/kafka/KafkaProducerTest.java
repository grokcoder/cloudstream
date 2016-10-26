package cn.edu.zju.vlis.examples.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Created by wangxiaoyi on 16/6/17.
 */
public class KafkaProducerTest {
    private KafkaProducer<Integer, String> producer;
    private final String topic = "test_topic_1";


    public void init() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "10.214.208.14:9092,10.214.208.13:9092,10.214.208.12:9092,10.214.208.11:9092");
        props.put("client.id", "DemoProducer");
        props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        producer = new KafkaProducer<>(props);
    }


    public void start() {
        int messageNum = 100000;
        while (true) {
            String messageStr = "Message_" + messageNum++;
            //long startTime = System.currentTimeMillis();
            try {
                producer.send(new ProducerRecord<>(topic, messageStr)).get();
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
