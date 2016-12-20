package cn.edu.zju.vlis.ptest.dcep;

import cn.edu.zju.vlis.dcep.engine.EplProcessor;
import cn.edu.zju.vlis.dcep.engine.SpoutProcessor;
import cn.edu.zju.vlis.eventhub.EventBusKafkaProducer;
import cn.edu.zju.vlis.eventhub.EventConstant;
import cn.edu.zju.vlis.eventhub.EventSchema;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.apache.storm.utils.Utils;


import java.util.LinkedList;
import java.util.List;

/**
 * Created by wangxiaoyi on 2016/12/20.
 */
public class FilterTester {


    public static void main(String []args){
        new FilterTester().run();
    }

    public void run(){

        //1. construct interested events
        EventSchema stockSchema = EventGenerator.getStoickTickEventSchema();
        List<EventSchema> interestedEvents = new LinkedList<>();
        interestedEvents.add(stockSchema);

        //2. build cep topology builder

        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout("StockTickStream", new SpoutProcessor("cn8:9092", stockSchema, 1));

        EplProcessor filterProcessor = new EplProcessor.EplProcessorBuilder()
                .registerEventSchema(stockSchema)
                .EPL("select * from " + stockSchema.getEventName() + " where name = 'A'")
                .build();

        builder.setBolt("filterStock", filterProcessor)
                .fieldsGrouping("StockTickStream", new Fields(EventConstant.EVENT_NAME));

        Config config = new Config();
        config.setDebug(true);


        config.registerSerialization(EventBusKafkaProducer.class);
        config.registerSerialization(KafkaConsumer.class);
        config.registerSerialization(KafkaProducer.class);

        StormTopology topology = builder.createTopology();
        LocalCluster cluster = new LocalCluster();

        cluster.submitTopology("FilterTester", config, topology);
        Utils.sleep(10 * 60 * 1000);
        cluster.killTopology("FilterTester");
        cluster.shutdown();
    }

}
