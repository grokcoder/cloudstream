package cn.edu.zju.vlis.storm.esper;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

/**
 * Created by wangxiaoyi on 16/5/25.
 */
public class BasicUpdateListener implements UpdateListener{

    private EsperBolt esperBolt;

    public BasicUpdateListener(EsperBolt esperBolt){
        this.esperBolt = esperBolt;
    }

    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        esperBolt.handleResult(newEvents);
    }
}
