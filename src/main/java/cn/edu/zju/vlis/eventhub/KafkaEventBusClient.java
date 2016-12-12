package cn.edu.zju.vlis.eventhub;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by wangxiaoyi on 16/6/2.
 * get event from kafka
 */
public class KafkaEventBusClient implements IEventBusClient<EventData> {

    private static final Logger LOG = Logger.getLogger(KafkaEventBusClient.class.getSimpleName());

    private static KafkaProducer<String, byte[]> producer;   // <eventName, EventData>
    private static KafkaConsumer<String, byte[]> subscriber; // <eventName, EventData>
    private ClientType clientType;
    private String defaultTopic = "";
    private Properties props;

    public enum ClientType {
        PRODUCER,
        SUBSCRIBER
    }

    public KafkaEventBusClient(){}

    public KafkaEventBusClient(ClientType clientType, Properties props){
        this.clientType = clientType;
        this.props = props;
        this.defaultTopic = props.getProperty("topic", "defaultEvent");
    }

    public void init(){
        if(clientType == ClientType.PRODUCER) initProducer();
        else initSubscriber();
    }

    private void initProducer(){
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        producer = new KafkaProducer<>(props);
    }

    private void initSubscriber(){
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        subscriber = new KafkaConsumer<>(props);
    }

    @Override
    public void connect() {
        connect("localhost:9092");
    }

    @Override
    public void connect(String connectionString) {
        if(connectionString == null || connectionString.equals("")){
           props.put("bootstrap.servers", "localhost:9092");
        }else {
            props.put("bootstrap.servers", connectionString);
        }
        init();
    }

    @Override
    public void subscribe(List<EventSchema> events) {
        List<String> topics = new LinkedList<>();
        for (EventSchema event: events){
            topics.add(event.getEventName());
        }
        if(subscriber == null) init();
        subscriber.subscribe(topics);
    }

    @Override
    public void send(EventData event, String topic) {
        Objects.requireNonNull(event);
        try {
            producer.send(new ProducerRecord<>(topic, event.getEventSchemaName(),
                    EventSerializer.toBytes(event))).get();
        } catch (InterruptedException e) {
            LOG.warn(e);
        } catch (ExecutionException e) {
            LOG.warn(e);
        }
    }

    public void send(EventData event) {
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
    public List<EventData> pollEvents() {
        ConsumerRecords<String, byte[]>
                records = subscriber.poll(Long.parseLong(props.getProperty("poll_timeout", "3000")));
        List<EventData> eventDatas = new LinkedList<>();
        for (ConsumerRecord<String, byte[]> record : records){
            eventDatas.add(EventSerializer.toEventData(record.value()));
        }
        return eventDatas;
    }

    @Override
    public void close() {
        switch (clientType){
            case PRODUCER: {
                producer.close();
                return;
            }
            case SUBSCRIBER: {
                subscriber.close();
                return;
            }
        }
    }
}