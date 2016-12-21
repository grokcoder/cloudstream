package cn.edu.zju.vlis.dcep.engine.handler;

import cn.edu.zju.vlis.dcep.engine.OutputSchema;
import cn.edu.zju.vlis.eventhub.EventHandler;
import com.espertech.esper.client.EventBean;
import org.apache.storm.task.OutputCollector;

import java.util.*;

/**
 * Created by wangxiaoyi on 2016/12/21.
 *
 * Storm Event Handler send the event detected into next
 * storm bolt
 */
public class StormEventHandler implements EventHandler{

    private OutputCollector collector;
    private OutputSchema outputSchema;

    public StormEventHandler(OutputCollector collector, OutputSchema outputSchema){
        this.collector = collector;
        this.outputSchema = outputSchema;
    }

    /**
     * handle events
     *
     * @param newEvents
     */
    @Override
    public void handle(EventBean[] newEvents) {
       collector.emit(eventBeanToValues(newEvents));
    }

    /**
     * transfer the data in map into tuple
     * @param newEvents
     * @return
     */
    public List<Object> eventBeanToValues(EventBean[] newEvents){
        List<Object> values = new LinkedList<>();
        if(newEvents != null){
            for (EventBean eventBean: newEvents){
                Map<String, Object> map = (Map<String, Object>) eventBean.getUnderlying();
                for (String key: outputSchema.getOeSchema().getTypeMap().keySet()){
                    values.add(map.get(key));
                }
            }
            values.add(outputSchema.getOeSchema().getEventName());
        }
        return values;
    }
}
