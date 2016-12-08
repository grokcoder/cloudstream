package cn.edu.zju.vlis.dcep;

import cn.edu.zju.vlis.dcep.dispolicy.TupleTransformer;
import cn.edu.zju.vlis.eventhub.EventData;
import cn.edu.zju.vlis.eventhub.EventSchema;
import cn.edu.zju.vlis.eventhub.IEventHubClient;
import cn.edu.zju.vlis.eventhub.KafkaEventBusClient;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by wangxiaoyi on 16/6/2.
 */
public class BasicEventSpout extends BaseRichSpout implements Serializable{

    private String connString;// connection string of the eventbus
    private IEventHubClient<EventData> subscriber; //stub used to poll event frm the event bus
    private List<EventSchema> interestedEvents; //// TODO: 16/11/22 just support one type event now

    private SpoutOutputCollector collector;

    public BasicEventSpout(String connString, List<EventSchema> events){
        this.connString = connString;
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, connString);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group_1");// // TODO: 16/11/1 make the group configurable
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 1000);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        subscriber = new KafkaEventBusClient(KafkaEventBusClient.ClientType.SUBSCRIBER, props);
        if (events == null || events.isEmpty()) throw new IllegalArgumentException("events should not be empty");
        this.interestedEvents = events;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        //@warn: ...
        declarer.declare(TupleTransformer.schemaToFileds(interestedEvents.get(0)));
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        subscriber.connect();
        subscriber.subscribe(interestedEvents);
        this.collector = collector;
    }

    @Override
    public void nextTuple() {
        List<EventData> events = subscriber.pollEvents();
        for (EventData event: events) {
            collector.emit(TupleTransformer.eventDataToOutputValues(event));
        }
    }

}
