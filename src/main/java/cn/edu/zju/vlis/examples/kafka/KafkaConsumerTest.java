package cn.edu.zju.vlis.examples.kafka;

import cn.edu.zju.vlis.eventhub.EventData;
import cn.edu.zju.vlis.eventhub.EventSerializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Collections;
import java.util.Properties;

/**
 * Created by wangxiaoyi on 16/6/17.
 */
public class KafkaConsumerTest {

    private final KafkaConsumer<Integer, byte[]> consumer;
    private final String topic;

    public KafkaConsumerTest(){

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "localhost:9092,10.214.208.14:9092,10.214.208.13:9092,10.214.208.12:9092,10.214.208.11:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group_1");
        //props.put(ConsumerConfig.CLIENT_ID_CONFIG, "group_1_client_1");
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 1000);

        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.IntegerDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArrayDeserializer");


        consumer = new KafkaConsumer<>(props);
        topic = "test1";

    }

    public void start(){
        while (true) {
            consumer.subscribe(Collections.singletonList(topic));
            ConsumerRecords<Integer, byte[]> records = consumer.poll(1000);

            for (ConsumerRecord<Integer, byte[]> record : records) {
                EventData eventData = EventSerializer.toEventData(record.value());
                System.out.println(eventData);
                //System.out.println("Received message: (" + record.key() + ", " + record.value() + ") at offset " + record.offset());
            }
        }
    }

    public static void main(String []args){
        KafkaConsumerTest comsumerTest = new KafkaConsumerTest();
        comsumerTest.start();
    }


}
