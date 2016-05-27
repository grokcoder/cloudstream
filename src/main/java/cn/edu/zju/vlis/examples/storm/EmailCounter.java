package cn.edu.zju.vlis.examples.storm;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangxiaoyi on 16/5/13.
 */
public class EmailCounter extends BaseBasicBolt{

    private Map<String, Integer> counts;

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
        String email = input.getStringByField("email");
        int count = counts.get(email) == null ? 0 : counts.get(email);
        counts.put(email, count + 1);
        printCounts();
    }

    /**
     * Declare the output schema for all the streams of this topology.
     *
     * @param declarer this is used to declare output stream ids, output fields, and whether or not each output stream is a direct stream
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        counts = new HashMap<>();
    }

    private void printCounts() {
        for (String email : counts.keySet()) {
            System.out.println(
                    String.format("%s has count of %s", email, counts.get(email)));
        } }
}
