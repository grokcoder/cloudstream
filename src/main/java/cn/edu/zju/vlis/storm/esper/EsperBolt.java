package cn.edu.zju.vlis.storm.esper;

import com.espertech.esper.client.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Created by wangxiaoyi on 16/5/25.
 */
public class EsperBolt extends BaseRichBolt {

    private static final Logger LOG = LogManager.getLogger(EsperBolt.class);

    private EsperContainer esperContainer;
    private OutputCollector collector;

    private Fields fields;  // output schema

    private String inputKey;


    private Map<String, Class> eventTypes;

    private List<String> epls;


    public EsperBolt(){
        eventTypes = new HashMap<>();
    }

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
                   handleResult(newEvents);
                });
            }
        }
    }


    /**
     * 处理esper语句执行的结果
     * 1. 发送给下一级bolt
     * 2. 用户自己决定如何处理
     * @param newEvents
     */
    private void handleResult(EventBean[] newEvents){
        for (EventBean eventBean: newEvents){
            LOG.info(eventBean.getUnderlying());
            //collector.emit(new Values(eventBean));
        }
    }

    /**
     * 启动esper 流程
     * 1. 注册事件类型
     * 2. 创建EPServiceProvider 实例
     * 3. 注册esper语句以及listener实例
     */
    private void initEsper(){

        esperContainer = new EsperContainer("localhost");

        for (Map.Entry<String, Class> eventType: eventTypes.entrySet()){
            esperContainer.addEventType(eventType.getKey(), eventType.getValue());
        }

        esperContainer.initEsper();
        for (String epl: epls)
            esperContainer.registerEPL(epl);
    }


    public void setFields(Fields fields) {
        this.fields = fields;
    }

    public void setInputKey(String inputKey) {
        this.inputKey = inputKey;
    }


    public void addEventType(String eventTypeName, Class classz){
        eventTypes.put(eventTypeName, classz);
    }

    public void addEPL(String epl){
        if(epls == null) epls = new LinkedList<>();
        if(epl != null && !epl.isEmpty() && !epls.contains(epl)){
            epls.add(epl);
        }
    }


    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        initEsper();
    }


    @Override
    public void execute(Tuple input) {
        List<Object> events = input.getValues();
        for (Object event: events)
            esperContainer.sendEvent(event);
    }


    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        if(fields != null)
            declarer.declare(fields);
    }
}
