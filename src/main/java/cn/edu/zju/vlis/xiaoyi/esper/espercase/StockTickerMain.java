package cn.edu.zju.vlis.xiaoyi.esper.espercase;

import cn.edu.zju.vlis.xiaoyi.util.generator.eventbean.PriceLimit;
import cn.edu.zju.vlis.xiaoyi.util.generator.eventbean.StockTick;
import cn.edu.zju.vlis.xiaoyi.esper.espercase.monitor.StockTickerMonitor;
import cn.edu.zju.vlis.xiaoyi.esper.espercase.monitor.StockTickerResultListener;
import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.LinkedList;

public class StockTickerMain implements Runnable {
    private static final Log log = LogFactory.getLog(StockTickerMain.class);

    private final String engineURI;
    private final boolean continuousSimulation;

    public static void main(String[] args) {
        new StockTickerMain("StockTicker", false).run();
    }

    public StockTickerMain(String engineURI, boolean continuousSimulation) {
        this.engineURI = engineURI;
        this.continuousSimulation = continuousSimulation;
    }

    public void run() {

        Configuration configuration = new Configuration();
        configuration.addEventType("PriceLimit", PriceLimit.class.getName());
        configuration.addEventType("StockTick", StockTick.class.getName());

        log.info("Setting up EPL");
        EPServiceProvider epService = EPServiceProviderManager.getProvider(engineURI, configuration);
        epService.initialize();
        new StockTickerMonitor(epService, new StockTickerResultListener());

        log.info("Generating test events: 1 million ticks, ratio 2 hits, 100 stocks");
        StockTickerEventGenerator generator = new StockTickerEventGenerator();
        LinkedList stream = generator.makeEventStream(1000000, 500000, 100, 25, 30, 48, 52, false);
        log.info("Generating " + stream.size() + " events");

        log.info("Sending " + stream.size() + " limit and tick events");
        for (Object theEvent : stream) {
            epService.getEPRuntime().sendEvent(theEvent);

            if (continuousSimulation) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    log.debug("Interrupted", e);
                    break;
                }
            }
        }

        log.info("Done.");
    }
}
