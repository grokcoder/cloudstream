package cn.edu.zju.vlis.eventhub;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by wangxiaoyi on 16/6/2.
 * Event Bus Producer implementation based on Kafka
 */
public class EventBusKafkaProducer implements IEventBusProducer<EventData> {

    private static final Logger LOG = Logger.getLogger(EventBusKafkaProducer.class.getSimpleName());

    private static KafkaProducer<String, byte[]> producer;   // <eventName, EventData>
    private String defaultTopic = "";
    private Properties props;
    private int partitionNum = 1;
    private int currPartition = 0;
    private volatile boolean isConnected = false;


    public EventBusKafkaProducer(){}

    public EventBusKafkaProducer(Properties props){
         this(props, 1);
    }

    public EventBusKafkaProducer(Properties props, int partitionNum){
        this.partitionNum = partitionNum;
        this.props = props;
        this.defaultTopic = props.getProperty("topic", "defaultEvent");
    }


    private void init(){
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        producer = new KafkaProducer<>(props);
    }

    @Override
    public void connect() {
        connect(props.getProperty("bootstrap.servers"));
    }

    @Override
    public void connect(String connectionString) {
        if(connectionString == null || connectionString.equals("")){
           props.put("bootstrap.servers", "localhost:9092");
        }else {
            props.put("bootstrap.servers", connectionString);
        }
        init();
        isConnected = true;
    }

    @Override
    public void send(EventData event, String topic) {
        Objects.requireNonNull(event);
        if (!isConnected) connect();
        try {
            currPartition = (currPartition + 1) % partitionNum;
            producer.send(new ProducerRecord<>(topic, currPartition, event.getEventSchemaName(),
                    EventSerializer.toBytes(event))).get();
        } catch (InterruptedException e) {
            LOG.warn(e);
        } catch (ExecutionException e) {
            LOG.warn(e);
        }
    }

    public void send(EventData event) {
        if(!isConnected) connect();
        Objects.requireNonNull(event);
        try {
            if(defaultTopic.equals("defaultEvent")){
                LOG.error("Topic not defined yet!");
                return;
            }
            producer.send(new ProducerRecord<>(defaultTopic, event.getEventSchemaName(),
                    EventSerializer.toBytes(event))).get();
        } catch (InterruptedException e) {
            LOG.warn(e);
        } catch (ExecutionException e) {
            LOG.warn(e);
        }
    }


    @Override
    public void close() {
        producer.close();
    }
}