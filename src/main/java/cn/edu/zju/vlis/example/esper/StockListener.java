package cn.edu.zju.vlis.example.esper;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by wangxiaoyi on 16/4/27.
 */
public class StockListener implements UpdateListener{
    private static final Log LOG = LogFactory.getLog(StockListener.class);

    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {

        LOG.info(newEvents[0].getUnderlying());
    }
}
