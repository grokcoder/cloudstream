package cn.edu.zju.vlis.storm.esper;

import com.espertech.esper.client.EventBean;

/**
 * Created by wangxiaoyi on 16/5/31.
 */
public class EmitEventHandler implements EventHandler{

    /**
     * handle events
     * emit the new Event
     * @param newEvents
     */
    @Override
    public void handle(EventBean[] newEvents) {
    }
}
