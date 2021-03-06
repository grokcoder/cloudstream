package cn.edu.zju.vlis.examples.storm;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


/**
 * Created by wangxiaoyi on 16/5/13.
 */
public class EmailExtractor extends BaseBasicBolt{

    private Logger LOG = LoggerFactory.getLogger(EmailExtractor.class.getName());

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        LOG.info("i am started");
    }

    /**
     * Declare the output schema for all the streams of this topology.
     *
     * @param declarer this is used to declare output stream ids, output fields, and whether or not each output stream is a direct stream
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("email"));
    }

    /**
     * Process the input tuple and optionally emit new tuples based on the input tuple.
     * <p>
     * All acking is managed for you. Throw a FailedException if you want to fail the tuple.
     *
     * @param input
     * @param collector
     */
    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        String commit = input.getStringByField("commit");
        String[] parts = commit.split(" ");
        collector.emit(new Values(parts[1]));
    }

}
