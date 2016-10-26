package cn.edu.zju.vlis.examples.smesper;

import cn.edu.zju.vlis.events.EventSchema;
import cn.edu.zju.vlis.storm.esper.EsperBolt;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.apache.storm.utils.Utils;

/**
 * Created by wangxiaoyi on 16/5/27.
 */
public class WordCountCep {

    private static final int TEN_MINUTES = 600000;

    public static void main(String []args){
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("stock_ticker", new StockTickSpout());

       /* EsperBolt countBolt = new EsperBolt
                .EsperBoltBuilder()
                .registerEventType("StockTick", StockTick.class)
                .EPL("select stockSymbol, count(*) from StockTick group by stockSymbol")
                .build();
*/

        EventSchema eventSchema = new EventSchema("StockTick");
        eventSchema.addAttribute("stockSymbol", String.class);
        eventSchema.addAttribute("price", Double.class);
        eventSchema.addAttribute("time", Long.class);


        EsperBolt countBolt = new EsperBolt
                .EsperBoltBuilder()
                .registerEventSchema(eventSchema)
                .EPL("select stockSymbol, count(*) from StockTick group by stockSymbol")
                .build();



        builder.setBolt("stock_counter", countBolt)
                .fieldsGrouping("stock_ticker", new Fields("StockTick"));

        Config config = new Config();
        config.setDebug(true);

        //config.setClasspath("./target");
        StormTopology topology = builder.createTopology();
        LocalCluster cluster = new LocalCluster();

        cluster.submitTopology("stock-counter", config, topology);
        Utils.sleep(TEN_MINUTES);
        cluster.killTopology("stock-counter");
        cluster.shutdown();

    }
}
