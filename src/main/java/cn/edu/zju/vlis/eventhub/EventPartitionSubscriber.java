package cn.edu.zju.vlis.eventhub;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Created by wangxiaoyi on 2016/12/12.
 * this subscriber just subscribe to specific
 * partition of some event
 *  1. subscriber = new EventPartitionSubscriber(...);
 *  2. subscriber.connect();
 *  3. subscriber.pullEvent();
 */
public class EventPartitionSubscriber implements Serializable{
    private Properties props;
    private TopicPartition tp;
    private KafkaConsumer<String, byte[]> subscriber;

    public EventPartitionSubscriber(int partitionNum, Properties props, String eventName){
        this.tp = new TopicPartition(eventName, partitionNum);
        this.props = props;
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArrayDeserializer");
    }

    public EventPartitionSubscriber(TopicPartition tp, Properties props){
        this.tp = tp;
        this.props = props;
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArrayDeserializer");
    }

    public void connect() {
        connect("localhost:9092");
    }

    public void connect(String connectionString) {
        if(connectionString == null || connectionString.equals("")){
            props.put("bootstrap.servers", "localhost:9092");
        }else {
            props.put("bootstrap.servers", connectionString);
        }
        init();
    }

    private void init(){
        subscriber = new KafkaConsumer<>(props);
        List<TopicPartition> partitions = new LinkedList<>();
        partitions.add(tp);
        subscriber.assign(partitions);// specify the partition info
    }

    public List<EventData> pollEvents() {
        ConsumerRecords<String, byte[]>
                records = subscriber.poll(Long.parseLong(props.getProperty("poll_timeout", "3000")));
        List<EventData> eventDatas = new LinkedList<>();
        for (ConsumerRecord<String, byte[]> record : records) {
            eventDatas.add(EventSerializer.toEventData(record.value()));
        }
        return eventDatas;
    }

    public void close(){
        subscriber.close();
    }

    public TopicPartition getTp() {
        return tp;
    }
}
