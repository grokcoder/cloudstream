package cn.edu.zju.vlis.dcep.engine;

import cn.edu.zju.vlis.dcep.engine.handler.EventHandlerType;
import cn.edu.zju.vlis.eventhub.EventConstant;
import cn.edu.zju.vlis.eventhub.EventSchema;

import org.apache.log4j.Logger;
import org.apache.storm.tuple.Fields;


import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wangxiaoyi on 2016/12/21.
 * define output related issues
 * 1.output event schema
 * 2.event handler type
 */
public class OutputSchema implements Serializable{

    private static final Logger LOG = Logger.getLogger(OutputSchema.class.getSimpleName());
    private EventHandlerType eth;
    private EventSchema oeSchema;// output event schema

    public OutputSchema(EventHandlerType eht, EventSchema oeSchema){
        this.eth = eht;
        this.oeSchema = oeSchema;
    }

    public Fields getOutputFileds(){
        if (oeSchema != null){
            List<String> filedNames = new LinkedList<>();
            for(String key: oeSchema.getTypeMap().keySet()){
                filedNames.add(key);
            }
            filedNames.add(EventConstant.EVENT_NAME);
            Fields fields = new Fields(filedNames);
            return fields;
        }
        LOG.warn("no output field defined");
        return new Fields("");
    }

    public EventHandlerType getEth() {
        return eth;
    }

    public void setEth(EventHandlerType eth) {
        this.eth = eth;
    }

    public EventSchema getOeSchema() {
        return oeSchema;
    }

    public void setOeSchema(EventSchema oeSchema) {
        this.oeSchema = oeSchema;
    }
}
