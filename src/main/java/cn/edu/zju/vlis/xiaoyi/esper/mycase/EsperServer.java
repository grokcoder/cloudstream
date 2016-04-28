package cn.edu.zju.vlis.xiaoyi.esper.mycase;

import cn.edu.zju.vlis.xiaoyi.util.generator.StockTickerGenerator;
import cn.edu.zju.vlis.xiaoyi.util.generator.StreamEventGenerator;
import cn.edu.zju.vlis.xiaoyi.util.generator.eventbean.StockTick;
import com.espertech.esper.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wangxiaoyi on 16/4/27.
 */

public class EsperServer {

    private static final Logger LOG = LoggerFactory.getLogger(EsperServer.class.getSimpleName());

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

       /* String expressionText = "every stock=StockTick()";
        EPStatement factory = epService.getEPAdministrator().createPattern(expressionText);
        factory.addListener(new StockListener());
*/
        EPStatement filterESP = epService.getEPAdministrator().createEPL("select stockSymbol, avg(price) as avg from StockTick(stockSymbol = 'S1')");

       // select * from StockTick(stockSymbol = 'S3')

        filterESP.addListener(new UpdateListener() {
            @Override
            public void update(EventBean[] newEvents, EventBean[] oldEvents) {
                LOG.info(newEvents[0].getUnderlying() + "");
            }
        });

    }

    public static void main(String []args){
        EsperServer server = new EsperServer("localhost");
        server.init();
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(new StockProducer());

        Scanner in = new Scanner(System.in);
        while (in.hasNext()){
            String sql = in.nextLine();
            EPStatement filterESP = epService.getEPAdministrator().createEPL(sql);

            filterESP.addListener(new UpdateListener() {
                @Override
                public void update(EventBean[] newEvents, EventBean[] oldEvents) {
                    LOG.info(newEvents[0].getUnderlying() + "");
                }
            });
        }
    }


    static class StockProducer implements Runnable{

        private StreamEventGenerator generator;

        public StockProducer(){
            generator = new StockTickerGenerator();
        }

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
