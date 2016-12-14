package cn.edu.zju.vlis.dcep.engine;

import cn.edu.zju.vlis.dcep.dispolicy.TupleTransformer;
import cn.edu.zju.vlis.eventhub.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by wangxiaoyi on 16/6/2.
 * basic Implementation of SpoutProcessor
 */
public class SpoutProcessor extends BaseRichSpout implements Serializable{

    private String connString;// connection string of the eventbus
    private EventBusSubscriber subscriber; //stub used to poll event frm the event bus
    private EventSchema eventSchema;

    private SpoutOutputCollector collector;

    public SpoutProcessor(String connString, EventSchema eventSchema, int maxPartitionNum){
        Properties props = new Properties();
        this.connString = connString;
        this.eventSchema = eventSchema;
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.connString);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group_1");
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 1000);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        this.subscriber = new EventBusSubscriber(maxPartitionNum, eventSchema.getEventName(), props);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(TupleTransformer.schemaToFileds(eventSchema));
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        subscriber.start();
        this.collector = collector;
    }

    @Override
    public void nextTuple() {
        List<EventData> events = subscriber.pullEvents();
        for (EventData event: events) {
            collector.emit(TupleTransformer.eventDataToOutputValues(event));
        }
    }
}
