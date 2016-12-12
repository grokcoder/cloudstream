package cn.edu.zju.vlis.eventhub;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.junit.Test;

import java.util.List;
import java.util.Properties;

/**
 * Created by wangxiaoyi on 2016/12/12.
 */
public class EventPartitionSubscriberTest {

    @Test
    public void testPollEvents(){

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group_1");
        //props.put(ConsumerConfig.CLIENT_ID_CONFIG, "group_1_client_1");
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 1000);

        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");


        EventPartitionSubscriber subscriber = new EventPartitionSubscriber(new TopicPartition("Person-2", 1), props);
        subscriber.connect();
        while (true) {
            List<EventData> events = subscriber.pollEvents();
            for (EventData event : events) {
                System.out.println(event);
            }
        }
    }

}
