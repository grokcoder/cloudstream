package cn.edu.zju.vlis.storm.esper;

import cn.edu.zju.vlis.events.EventHandler;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

/**
 * Created by wangxiaoyi on 16/5/25.
 */
public class BasicUpdateListener implements UpdateListener{

    private EventHandler handler;

    public BasicUpdateListener(EventHandler handler){
        this.handler = handler;
    }

    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        handler.handle(newEvents);
    }
}
