package cn.edu.zju.vlis.events;

import com.espertech.esper.client.EventBean;

/**
 * Created by wangxiaoyi on 16/5/31.
 */
public interface EventHandler {
    /**
     * handle events
     * @param newEvents
     */
    void handle(EventBean[] newEvents);
}
