package cn.edu.zju.vlis.dcep.engine.handler;

import cn.edu.zju.vlis.eventhub.EventHandler;
import com.espertech.esper.client.EventBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wangxiaoyi on 16/5/31.
 */
public class LogEventHandler implements EventHandler {

    private static Logger LOG = LoggerFactory.getLogger(LogEventHandler.class.getName());

    /**
     * handle events
     * just add new event info into logs
     * @param newEvents
     */
    @Override
    public void handle(EventBean[] newEvents) {
        for (EventBean eventBean: newEvents){
            System.out.println("Complex event detected: " + eventBean.getUnderlying().toString());
        }
    }
}
