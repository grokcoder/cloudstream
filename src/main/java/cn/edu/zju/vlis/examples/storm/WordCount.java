package cn.edu.zju.vlis.examples.storm;

import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

/**
 * Created by wangxiaoyi on 16/5/13.
 */
public class WordCount {

    private static final int TEN_MINUTES = 600000;


    public static void main(String []args) throws Exception{

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("email-feed-listener", new CommitFeedListener());
        builder.setBolt("email-extractor", new EmailExtractor())
                .shuffleGrouping("email-feed-listener");

        builder.setBolt("email-counter", new EmailCounter())
                .fieldsGrouping("email-extractor", new Fields("email"));

        Config config = new Config();
        //config.setDebug(true);


        config.setMaxTaskParallelism(3);

        config.setNumWorkers(3);
        StormSubmitter.submitTopologyWithProgressBar("github-commit-count-topology-GIT18", config, builder.createTopology());



    /*    StormTopology topology = builder.createTopology();
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("github-commit-count-topology", config, topology);
        Utils.sleep(TEN_MINUTES); cluster.killTopology("github-commit-count-topology");
        cluster.shutdown();*/

    }
}
