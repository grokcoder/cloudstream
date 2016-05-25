package cn.edu.zju.vlis.example.esper;

import cn.edu.zju.vlis.example.generator.StockTickerGenerator;
import cn.edu.zju.vlis.example.generator.StreamEventGenerator;
import cn.edu.zju.vlis.example.generator.eventbean.StockInfo;
import cn.edu.zju.vlis.example.generator.eventbean.StockTick;
import com.espertech.esper.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by wangxiaoyi on 16/4/27.
 */

public class EsperServer {

    private static final Logger LOG = LoggerFactory.getLogger(EsperServer.class.getSimpleName());

    private final String engineURI;
    private Configuration configuration = new Configuration();
    private static EPServiceProvider epService;

    private static final BlockingQueue<Object> eventQueue = new LinkedBlockingQueue<>();


    public EsperServer(String uri){
        engineURI = uri;
        LOG.info("Setting up EPL");
        configuration.addEventType("StockTick", StockTick.class.getName());
        configuration.addEventType("StockInfo", StockInfo.class.getName());
        epService = EPServiceProviderManager.getProvider(engineURI, configuration);

    }

    public void init(){
        LOG.info("initializing EsperServer ... ");
        epService.initialize();

       /* String expressionText = "every stock=StockTick()";
        EPStatement factory = epService.getEPAdministrator().createPattern(expressionText);
        factory.addListener(new StockListener());
*/


        String epl = "select si.industry, st.stockSymbol, st.price from StockInfo.win:length(10) as si inner join StockTick.win:length(10) as st on si.symbol = st.stockSymbol";
        //"select * from StockTick(price > 10)"
        EPStatement filterESP = epService.getEPAdministrator().createEPL(epl);

       // select * from StockTick(stockSymbol = 'S3')

        filterESP.addListener(new UpdateListener() {
            @Override
            public void update(EventBean[] newEvents, EventBean[] oldEvents) {

                //eventQueue.add(newEvents[0].getUnderlying());
                LOG.info(newEvents[0].getUnderlying() + "");
            }
        });

    }


    public static synchronized void sendEvent(Object event){
        epService.getEPRuntime().sendEvent(event);
    }

    public static void main(String []args){
        EsperServer server = new EsperServer("localhost");
        server.init();
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(new StockProducer());

        /*executor.submit(() -> {
            while (true) {
                Object event = null;
                try {
                    event = eventQueue.take();
                    System.err.print(event.getClass());
                } catch (InterruptedException e) {
                   // e.printStackTrace();
                }
                sendEvent(event);
            }
        });*/
        String [] names = {"S1", "S2", "S3","S4","S5"};
        String [] industries = {"IN1", "IN2","IN3","IN4"};
      /*  executor.submit(() -> {
           while (true){

               StockInfo info = new StockInfo(names[RandomHelper.getIntFromRange(0, 4)],
                       industries[RandomHelper.getIntFromRange(0, 4)]);
               sendEvent(info);
               Thread.sleep(10);
           }

        });*/




     /*   Scanner in = new Scanner(System.in);
        while (in.hasNext()){
            String sql = in.nextLine();
            EPStatement filterESP = epService.getEPAdministrator().createEPL(sql);

            filterESP.addListener(new UpdateListener() {
                @Override
                public void update(EventBean[] newEvents, EventBean[] oldEvents) {
                    LOG.info(newEvents[0].getUnderlying() + "");
                }
            });

        }*/
    }


    static class StockProducer implements Runnable{

        private StreamEventGenerator generator;

        public StockProducer(){
            generator = new StockTickerGenerator();
        }

        @Override
        public void run() {
            //while (true){
               //simulateByRandom();
               // simulate();
            //}
            simulateByRandom();
        }


        public void simulateByRandom(){
            while (true) {
                StockTick st = (StockTick) generator.next();

                sendEvent(st);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


        public void simulate(){
            for (int i = 0; i < 10; i ++){
                StockTick stockTick = new StockTick("C1", 5 + i, System.currentTimeMillis());
                epService.getEPRuntime().sendEvent(stockTick);
            }
        }
    }
}
