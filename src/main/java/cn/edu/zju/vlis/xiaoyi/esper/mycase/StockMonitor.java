package cn.edu.zju.vlis.xiaoyi.esper.mycase;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wangxiaoyi on 16/4/27.
 */
public class StockMonitor {

    private static final Logger LOG = LoggerFactory.getLogger(StockMonitor.class);
    private final EPServiceProvider epService;

    private final StockListener listener;

    public StockMonitor(EPServiceProvider epService, StockListener stockListener){
        this.epService = epService;
        this.listener = stockListener;

        // Listen to all limits to be set
        String expressionText = "every stock=StockTick()";
        EPStatement factory = epService.getEPAdministrator().createPattern(expressionText);
        factory.addListener(listener);

    }

}
