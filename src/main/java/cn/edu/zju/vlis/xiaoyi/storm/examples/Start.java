package cn.edu.zju.vlis.xiaoyi.storm.examples;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.shade.org.apache.commons.io.IOUtils;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.apache.storm.utils.Utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by wangxiaoyi on 16/5/13.
 */
public class Start {

    private static final int TEN_MINUTES = 600000;


    public static void main(String []args) throws IOException{

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("email-feed-listener", new CommitFeedListener());
        builder.setBolt("email-extractor", new EmailExtractor())
                .shuffleGrouping("email-feed-listener");

        builder.setBolt("email-counter", new EmailCounter())
                .fieldsGrouping("email-extractor", new Fields("email"));

        Config config = new Config();
        config.setDebug(true);

        config.setMaxTaskParallelism(3);

        StormTopology topology = builder.createTopology();
        LocalCluster cluster = new LocalCluster();




        cluster.submitTopology("github-commit-count-topology", config, topology);

        Utils.sleep(TEN_MINUTES); cluster.killTopology("github-commit-count-topology");
        cluster.shutdown();

    }
}
