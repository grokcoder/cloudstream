package cn.edu.zju.vlis.xiaoyi.esper;

import cn.edu.zju.vlis.xiaoyi.util.generator.eventbean.StockTick;
import cn.edu.zju.vlis.xiaoyi.esper.monitor.StockTickerMonitor;
import cn.edu.zju.vlis.xiaoyi.esper.monitor.StockTickerResultListener;
import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

/**
 * Created by wangxiaoyi on 16/4/27.
 */
public class EsperServer {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final String engineURI;
    private Configuration configuration = new Configuration();
    private EPServiceProvider epService;


    public EsperServer(String uri){
        engineURI = uri;
        LOG.info("Setting up EPL");
        configuration.addEventType("StockTick", StockTick.class.getName());
        epService = EPServiceProviderManager.getProvider(engineURI, configuration);
    }

    public void init(){
        LOG.info("initializing EsperServer ... ");
        epService.initialize();
    }

    public static void main(String []args){



    }


    public void run(){

        new StockTickerMonitor(epService, new StockTickerResultListener());

        LOG.info("Generating test events: 1 million ticks, ratio 2 hits, 100 stocks");
        StockTickerEventGenerator generator = new StockTickerEventGenerator();
        LinkedList stream = generator.makeEventStream(1000000, 500000, 100, 25, 30, 48, 52, false);
        LOG.info("Generating " + stream.size() + " events");

        LOG.info("Sending " + stream.size() + " limit and tick events");
        for (Object theEvent : stream) {
            epService.getEPRuntime().sendEvent(theEvent);
        }

        LOG.info("Done.");
    }
}
