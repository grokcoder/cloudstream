package cn.edu.zju.vlis.xiaoyi.esper.mycase;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by wangxiaoyi on 16/4/27.
 */
public class StockListener implements UpdateListener{
    private static final Log LOG = LogFactory.getLog(StockListener.class);

    /**
     * Notify that new events are available or old events are removed.
     * If the call to update contains new (inserted) events, then the first argument will be a non-empty list and
     * the second will be empty. Similarly, if the call is a notification of deleted events, then the first argument
     * will be empty and the second will be non-empty.
     * <p>
     * Either the newEvents or oldEvents will be non-null. This method won't be called with both arguments being null
     * (unless using output rate limiting or force-output options),
     * but either one could be null. The same is true for zero-length arrays.
     * Either newEvents or oldEvents will be non-empty. If both are non-empty, then the update is a modification
     * notification.
     *
     * @param newEvents is any new events. This will be null or empty if the update is for old events only.
     * @param oldEvents is any old events. This will be null or empty if the update is for new events only.
     */
    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        LOG.info(newEvents[0].getUnderlying());
    }
}
