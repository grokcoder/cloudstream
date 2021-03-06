package cn.edu.zju.vlis.dcep.engine;

import cn.edu.zju.vlis.eventhub.EventHandler;
import com.espertech.esper.client.EventBean;

/**
 * Created by wangxiaoyi on 16/5/31.
 */
public class EmitEventHandler implements EventHandler {

    /**
     * handle events
     * emit the new Event
     * @param newEvents
     */
    @Override
    public void handle(EventBean[] newEvents) {

        for (EventBean evnt: newEvents){
            System.out.println("Event detected : " + evnt.getUnderlying());
        }
    }
}
