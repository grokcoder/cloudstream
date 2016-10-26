package cn.edu.zju.vlis.storm.esper;

import cn.edu.zju.vlis.events.EventSchema;
import cn.edu.zju.vlis.events.EventHandler;
import cn.edu.zju.vlis.events.LogEventHandler;
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

    private static final Logger LOG = LogManager.getLogger(EsperBolt.class.getName());

    private BasicEsperManager esperMgr;
    private EventHandler eventHandler;


    private OutputCollector collector;
    private final Fields fields;  // output schema
    private final String inputKey;
    private final Map<String, Class> eventTypes;
    private final List<EventSchema> eventSchemas;
    private final List<String> epls;

    private EsperBolt(EsperBoltBuilder builder){
        this.fields = builder.outputFields;
        this.eventTypes = builder.eventTypes;
        this.epls = builder.epls;
        this.eventHandler = builder.eventHandler;
        this.inputKey = builder.inputKey;
        this.eventSchemas = builder.eventSchemas;
    }


    public class BasicEsperManager implements EsperManager{

        private EPServiceProvider epService;
        private final String esperURI;
        private final Configuration esperConfig;

        public BasicEsperManager(String engineURI){
            this.esperURI = engineURI;
            this.esperConfig = new Configuration();
        }


        public void addEventType(String eventTypeName, Class classz){
            esperConfig.addEventType(eventTypeName, classz.getName());
        }

        public void addEventType(EventSchema eventSchema){
            esperConfig.addEventType(eventSchema.getEventName(), eventSchema.getTypeMap());
        }

        public void startEsper(){
            epService = EPServiceProviderManager.getProvider(esperURI, esperConfig);
            epService.initialize();
        }

        public void sendEvent(Object event){
            epService.getEPRuntime().sendEvent(event);
        }

        public void registerEPL(String EPL){
            registerEPL(EPL, new BasicUpdateListener(EsperBolt.this.eventHandler));
        }

        public void registerEPL(String EPL, UpdateListener listener){
            EPStatement statement = epService.getEPAdministrator().createEPL(EPL);
            if(listener != null) {
                statement.addListener(listener);
            }else {
               registerEPL(EPL);
            }
        }

        public void stopEsper(){
            epService.destroy();
        }
    }


    /**
     * flow of starting esper manager
     * 1. make sure we have a event handler
     * 2. init esper manager
     * 3. start esper
     * 4. register EPL
     */
    private void startEsperMgr(){
        LOG.info("Try to starting esper manager ... ");
        if(eventHandler == null)
            eventHandler = new LogEventHandler();
        esperMgr = new BasicEsperManager("localhost");
        for (Map.Entry<String, Class> eventType: eventTypes.entrySet()){
            esperMgr.addEventType(eventType.getKey(), eventType.getValue());
        }
        for (EventSchema eSchema: eventSchemas){
            esperMgr.addEventType(eSchema);
        }
        esperMgr.startEsper();
        for (String epl: epls)
            esperMgr.registerEPL(epl);

        LOG.info("Esper manager started ... ");
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        startEsperMgr();
    }


    @Override
    public void execute(Tuple input) {
        if(inputKey == null) {
            List<Object> events = input.getValues();
            for (Object event : events)
                esperMgr.sendEvent(event);
        }else {
            Object event = input.getValueByField(inputKey);
            esperMgr.sendEvent(event);
        }
    }


    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        if(fields != null)
            declarer.declare(fields);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        esperMgr.stopEsper();
    }

    public void emit(List<Object> tuples){
        collector.emit(tuples);
    }

    /**
     * builder for esperbolt
     */
    public static class EsperBoltBuilder {

        private Fields outputFields;
        private String inputKey;
        private Map<String, Class> eventTypes;
        private List<String> epls;
        private EventHandler eventHandler;

        private List<EventSchema> eventSchemas;

        public EsperBoltBuilder(){
            eventTypes = new HashMap<>();
            epls = new LinkedList<>();
            eventSchemas = new LinkedList<>();
        }

        public EsperBoltBuilder EPL(String epl){
            if(epls == null) epls = new LinkedList<>();
            if(epl != null && !epl.isEmpty() && !epls.contains(epl)){
                epls.add(epl);
            }
            return this;
        }

        public EsperBoltBuilder registerEventType(String eventTypeName, Class classz){
            eventTypes.put(eventTypeName, classz);
            return this;
        }

        public EsperBoltBuilder registerEventSchema(EventSchema eSchema){
            eventSchemas.add(eSchema);
            return this;
        }


        public EsperBoltBuilder setEventHandler(EventHandler eventHandler) {
            this.eventHandler = eventHandler;
            return this;
        }

        public EsperBoltBuilder setInputKey(String inputKey) {
            this.inputKey = inputKey;
            return this;
        }

        /**
         * specify emit outputFields
         * @param fields
         * @return
         */
        public EsperBoltBuilder setEmitFields(Fields fields) {
            this.outputFields = fields;
            return this;
        }

        public EsperBolt build(){
            return new EsperBolt(this);
        }

    }
}
