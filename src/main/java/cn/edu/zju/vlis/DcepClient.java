package cn.edu.zju.vlis;

import cn.edu.zju.vlis.dcep.BasicEventSpout;
import cn.edu.zju.vlis.dcep.engine.EsperBolt;
import cn.edu.zju.vlis.eventhub.EventSchema;

import cn.edu.zju.vlis.eventhub.KafkaEventBusClient;
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
        builder.setSpout("cep_in", new BasicEventSpout("localhost:9092", interestedEvents));

        EsperBolt countBolt = new EsperBolt
                .EsperBoltBuilder()
                .registerEventSchema(eventSchema)
                .EPL("select name, count(*) from Person group by name")
                .build();


        builder.setBolt("person_counter", countBolt)
                .fieldsGrouping("cep_in", new Fields("EventName"));


        Config config = new Config();
        config.setDebug(true);

        config.registerSerialization(KafkaEventBusClient.class);
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