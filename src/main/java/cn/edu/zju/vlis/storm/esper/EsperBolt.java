package cn.edu.zju.vlis.storm.esper;

import cn.edu.zju.vlis.example.generator.eventbean.StockTick;
import com.espertech.esper.client.*;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import java.util.Map;

/**
 * Created by wangxiaoyi on 16/5/25.
 */
public class EsperBolt extends BaseRichBolt {

    private EsperContainer esperContainer;
    private OutputCollector collector;

    private Fields fields;  // output schema


    /**
     * container for esper
     */
    public class EsperContainer{

        private EPServiceProvider epService;
        private final String esperURI;
        private Configuration esperConfig;


        public EsperContainer(String engineURI){
            this.esperURI = engineURI;
            this.esperConfig = new Configuration();
        }


        public void addEventType(String eventTypeName, Class classz){
            esperConfig.addEventType(eventTypeName, classz.getName());
        }


        public void initEsper(){
            epService = EPServiceProviderManager.getProvider(esperURI, esperConfig);
            epService.initialize();
        }

        public void sendEvent(Object event){
            epService.getEPRuntime().sendEvent(event);
        }


        public void registerEPL(String epl){
            registerEPL(epl, null);
        }

        public void registerEPL(String epl, UpdateListener listener){
            EPStatement statement = epService.getEPAdministrator().createEPL(epl);
            if(listener != null) {
                statement.addListener(listener);
            }else {
                statement.addListener((EventBean[] newEvents, EventBean[] oldEvents) -> {
                   handleResult(newEvents[0]);
                });
            }
        }
    }

    /**
     * 处理esper语句执行的结果
     * 1. 发送给下一级bolt
     * 2. 用户自己决定如何处理
     * @param eventBean
     */
    public void handleResult(EventBean eventBean){

    }

    /**
     * 启动esper 流程
     * 1. 注册事件类型
     * 2. 创建EPServiceProvider 实例
     * 3. 注册esper语句以及listener实例
     */
    public void initEsper(){
        esperContainer = new EsperContainer("localhost");
        esperContainer.initEsper();
        esperContainer.addEventType("type", StockTick.class);
        esperContainer.registerEPL("select * from StockTick(stockSymbol = 'S3')");
    }



    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        initEsper();
    }


    @Override
    public void execute(Tuple input) {

    }


    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(fields);
    }
}
