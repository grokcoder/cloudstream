package cn.edu.zju.vlis.xiaoyi.esper.mycase;

import cn.edu.zju.vlis.xiaoyi.util.generator.StockTickerGenerator;
import cn.edu.zju.vlis.xiaoyi.util.generator.StreamEventGenerator;
import cn.edu.zju.vlis.xiaoyi.util.generator.eventbean.StockTick;
import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wangxiaoyi on 16/4/27.
 */

public class EsperServer {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final String engineURI;
    private Configuration configuration = new Configuration();
    private static EPServiceProvider epService;


    public EsperServer(String uri){
        engineURI = uri;
        LOG.info("Setting up EPL");
        configuration.addEventType("StockTick", StockTick.class.getName());
        epService = EPServiceProviderManager.getProvider(engineURI, configuration);
    }

    public void init(){
        LOG.info("initializing EsperServer ... ");
        epService.initialize();

        new StockMonitor(epService, new StockListener());

    }

    public static void main(String []args){
        EsperServer server = new EsperServer("localhost");
        server.init();
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(new StockProducer());
    }



    static class StockProducer implements Runnable{

        private StreamEventGenerator generator;

        public StockProducer(){
            generator = new StockTickerGenerator();
        }
        /**
         * When an object implementing interface <code>Runnable</code> is used
         * to create a thread, starting the thread causes the object's
         * <code>run</code> method to be called in that separately executing
         * thread.
         * <p>
         * The general contract of the method <code>run</code> is that it may
         * take any action whatsoever.
         *
         * @see Thread#run()
         */
        @Override
        public void run() {
            while (true){
                StockTick st = (StockTick) generator.next();
                epService.getEPRuntime().sendEvent(st);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
