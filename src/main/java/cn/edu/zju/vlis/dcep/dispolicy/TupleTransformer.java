package cn.edu.zju.vlis.dcep.dispolicy;

import cn.edu.zju.vlis.eventhub.EventConstant;
import cn.edu.zju.vlis.eventhub.EventData;
import cn.edu.zju.vlis.eventhub.EventSchema;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by wangxiaoyi on 16/11/22.
 * EventData -> Tuple Output
 * Tuple, EventSchema -> EventData
 * EventSchema to Fields
 */
public class TupleTransformer {

    /**
     * EventData -> Tuple Output
     * @param eventData
     * @return
     */
    public static Values eventDataToOutputValues(EventData eventData){
        List<Object> values = new LinkedList<>();
        for(String key: eventData.getDataMap().keySet()){
            values.add(eventData.getDataMap().get(key));
        }
        values.add(eventData.getEventSchemaName());
        Values output = new Values(values.toArray());
        return output;
    }

    /**
     * EventSchema to Fields
     * @param schema
     * @return
     */
    public static Fields schemaToFileds(EventSchema schema){
        List<String> filedNames = new LinkedList<>();
        for(String key: schema.getTypeMap().keySet()){
            filedNames.add(key);
        }
        filedNames.add(EventConstant.EVENT_NAME);
        Fields fields = new Fields(filedNames);
        return fields;
    }

    /**
     * Tuple, EventSchema -> EventData
     * @param tuple
     * @param schema
     * @return
     */
    public static EventData tupleToEventData(Tuple tuple, EventSchema schema){
        EventData eventData = new EventData(schema.getEventName());
        for (String key: schema.getTypeMap().keySet()){
            eventData.addData(key, tuple.getValueByField(key));
        }
        return eventData;
    }
}
