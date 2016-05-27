package cn.edu.zju.vlis.examples.smesper;

import cn.edu.zju.vlis.examples.generator.eventbean.StockTick;
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

        EsperBolt countBoult = new EsperBolt();
        countBoult.addEventType("StockTick", StockTick.class);
        countBoult.addEPL("select stockSymbol, count(*) from StockTick group by stockSymbol");

        builder.setBolt("stock_counter", countBoult)
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
