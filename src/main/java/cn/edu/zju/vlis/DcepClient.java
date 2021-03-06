package cn.edu.zju.vlis;

import cn.edu.zju.vlis.dcep.engine.EplProcessor;
import cn.edu.zju.vlis.dcep.engine.SpoutProcessor;
import cn.edu.zju.vlis.eventhub.EventConstant;
import cn.edu.zju.vlis.eventhub.EventSchema;

import cn.edu.zju.vlis.eventhub.EventBusKafkaProducer;
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
 * Created by wangxiaoyi on 16/11/1.
 */
public class DcepClient {

    public static void main(String []args){
        //1.define data schema
        EventSchema eventSchema = new EventSchema("Person");
        eventSchema.addAttribute("name", String.class);
        eventSchema.addAttribute("age", Integer.class);

        //2.config interested events
        List<EventSchema> interestedEvents = new LinkedList<>();
        interestedEvents.add(eventSchema);

        TopologyBuilder builder = new TopologyBuilder();

        //3.build spout
        builder.setSpout("cep_in", new SpoutProcessor("cn8:9092", eventSchema, 4));

        EplProcessor countBolt = new EplProcessor.EplProcessorBuilder()
                .registerEventSchema(eventSchema)
                .EPL("select name, count(*) from Person group by name")
                .build();


        builder.setBolt("person_counter", countBolt)
                .fieldsGrouping("cep_in", new Fields(EventConstant.EVENT_NAME));


        Config config = new Config();
        config.setDebug(true);

        config.registerSerialization(EventBusKafkaProducer.class);
        config.registerSerialization(KafkaConsumer.class);
        config.registerSerialization(KafkaProducer.class);

        //config.registerSerialization(Kryo.class);

        StormTopology topology = builder.createTopology();


        LocalCluster cluster = new LocalCluster();

        cluster.submitTopology("cep", config, topology);
        Utils.sleep(10 * 60 * 1000);
        cluster.killTopology("cep");
        cluster.shutdown();
    }
}