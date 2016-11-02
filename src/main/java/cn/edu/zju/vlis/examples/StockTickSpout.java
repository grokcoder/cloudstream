package cn.edu.zju.vlis.examples;


import cn.edu.zju.vlis.examples.generator.StockTickGenerator;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.util.Map;

/**
 * Created by wangxiaoyi on 16/5/27.
 */
public class StockTickSpout extends BaseRichSpout{

    private StockTickGenerator generator;
    private SpoutOutputCollector collector;

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("StockTick"));
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        generator = new StockTickGenerator();
        this.collector = collector;
    }

    @Override
    public void nextTuple() {
        collector.emit(new Values(generator.next()));
    }
}
