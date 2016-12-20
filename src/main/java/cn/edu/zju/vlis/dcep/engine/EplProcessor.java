package cn.edu.zju.vlis.dcep.engine;

import cn.edu.zju.vlis.dcep.dispolicy.TupleTransformer;
import cn.edu.zju.vlis.eventhub.*;
import com.espertech.esper.client.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Created by wangxiaoyi on 16/5/25.
 */

public class EplProcessor extends Processor {

    private static final Logger LOG = LogManager.getLogger(EplProcessor.class.getName());

    private BasicEsperManager esperMgr;
    private EventHandler eventHandler;

    private OutputCollector collector;
    private final Fields fields;  // output schema
    private final String inputKey;
    private final Map<String, Class> eventTypes;
    private final List<EventSchema> eventSchemas;
    private final Map<String, EventSchema> schemaMap = new HashMap<>();
    private final List<String> epls;

    private EplProcessor(EplProcessorBuilder builder){
        this.fields = builder.outputFields;
        this.eventTypes = builder.eventTypes;
        this.epls = builder.epls;
        this.eventHandler = builder.eventHandler;
        this.inputKey = builder.inputKey;
        this.eventSchemas = builder.eventSchemas;
        for (EventSchema schema: eventSchemas){
            schemaMap.put(schema.getEventName(), schema);
        }
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
            if(event instanceof EventData){
                sendEvent((EventData) event);
            }else {
                epService.getEPRuntime().sendEvent(event);
            }
        }

        public void sendEvent(EventData eventData){
            epService.getEPRuntime().sendEvent(eventData.getDataMap(), eventData.getEventSchemaName());
        }

        public void registerEPL(String EPL){
            registerEPL(EPL, new BasicUpdateListener(EplProcessor.this.eventHandler));
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
        //transfer input into the corresponding EventData instance
        if(schemaMap.containsKey(input.getValueByField(EventConstant.EVENT_NAME))){
            EventSchema schema = schemaMap.get(input.getValueByField(EventConstant.EVENT_NAME));
            esperMgr.sendEvent(TupleTransformer.tupleToEventData(input, schema));
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
    public static class EplProcessorBuilder {

        private Fields outputFields;
        private String inputKey;
        private Map<String, Class> eventTypes;
        private List<String> epls;
        private EventHandler eventHandler;

        private List<EventSchema> eventSchemas;

        public EplProcessorBuilder(){
            eventTypes = new HashMap<>();
            epls = new LinkedList<>();
            eventSchemas = new LinkedList<>();
        }

        public EplProcessorBuilder EPL(String epl){
            if(epls == null) epls = new LinkedList<>();
            if(epl != null && !epl.isEmpty() && !epls.contains(epl)){
                epls.add(epl);
            }
            return this;
        }

        public EplProcessorBuilder registerEventType(String eventTypeName, Class classz){
            eventTypes.put(eventTypeName, classz);
            return this;
        }

        public EplProcessorBuilder registerEventSchema(EventSchema eSchema){
            eventSchemas.add(eSchema);
            return this;
        }


        public EplProcessorBuilder setEventHandler(EventHandler eventHandler) {
            this.eventHandler = eventHandler;
            return this;
        }

        public EplProcessorBuilder setInputKey(String inputKey) {
            this.inputKey = inputKey;
            return this;
        }

        /**
         * specify emit outputFields
         * @param fields
         * @return
         */
        public EplProcessorBuilder setEmitFields(Fields fields) {
            this.outputFields = fields;
            return this;
        }

        public EplProcessor build(){
            return new EplProcessor(this);
        }

    }
}