package cn.edu.zju.vlis.ptest.dcep;

import cn.edu.zju.vlis.eventhub.EventData;
import cn.edu.zju.vlis.eventhub.EventSchema;

/**
 * Created by wangxiaoyi on 2016/12/20.
 */
public class EventGenerator {

    //construct StockTickEvent event schema
    public static EventSchema getStoickTickEventSchema(){
        EventSchema schema = new EventSchema("StockTick");
        schema.addAttribute("name", String.class);
        schema.addAttribute("id", Integer.class);
        schema.addAttribute("stockType", String.class);
        schema.addAttribute("timestamp", Long.class);
        schema.addAttribute("price", Double.class);
        schema.addAttribute("priceChangeRatio", Double.class);
        schema.addAttribute("priceChange", Double.class);
        schema.addAttribute("volume", Integer.class);
        schema.addAttribute("amount", Double.class);
        schema.addAttribute("type", String.class);// "BUY" or "SELL"
        return schema;
    }

    //construct stock tick event entity
    public static EventData getStockTick(String name, Integer id, String stockType,
                                         Double price, Double priceChangeRatio, Double priceChange,
                                         Integer volume, Double amount, String type, Long timestamp){
        EventData event = new EventData("StockTick");
        event.addData("name", name);
        event.addData("id", id);
        event.addData("stockType", stockType);
        event.addData("timestamp", timestamp);
        event.addData("price", price);
        event.addData("priceChangeRatio", priceChangeRatio);
        event.addData("priceChange", priceChange);
        event.addData("volume", volume);
        event.addData("amount", amount);
        event.addData("type", type);
        return event;
    }

    //construct MarketIndexTick event schema
    public static EventSchema getMarketIndexTickSchema(){
        EventSchema schema = new EventSchema("MarketIndexTick");
        schema.addAttribute("name", String.class);
        schema.addAttribute("id", Integer.class);
        schema.addAttribute("timestamp", Long.class);
        schema.addAttribute("value", Double.class);
        schema.addAttribute("volume", Long.class);
        return schema;
    }

    //construct market index tick event entity
    public static EventData getMarketIndexTick(String name, Integer id, Double value, Long volume, Long timestamp){
        EventData event = new EventData("MarketIndexTick");
        event.addData("name", name);
        event.addData("id", id);
        event.addData("value", value);
        event.addData("volume", volume);
        event.addData("timestamp", timestamp);
        return event;
    }

    //construct CashFlowTick event schema
    public static EventSchema getCashFlowTickSchema(){
        EventSchema schema = new EventSchema("CacheFlow");
        schema.addAttribute("moduleName", String.class);
        schema.addAttribute("avgPrice", Double.class);
        schema.addAttribute("priceChangeRatio", Double.class);
        schema.addAttribute("inflows", Double.class);
        schema.addAttribute("outflows", Double.class);
        schema.addAttribute("netInflows", Double.class);
        schema.addAttribute("netInflowsRatio", Double.class);
        schema.addAttribute("timestamp", Long.class);
        return schema;
    }

    public static EventData getCashFlowTick(String moduleName, Double avgPrice, Double priceChangeRatio,
                                            Double inflows, Double outflows, Double netInflows,
                                            Double netInflowsRatio, Long timestamp){

        EventData event = new EventData("CacheFlow");
        event.addData("moduleName", moduleName);
        event.addData("avgPrice", avgPrice);
        event.addData("priceChangeRatio", priceChangeRatio);
        event.addData("inflows", inflows);
        event.addData("outflows", outflows);
        event.addData("netInflows", netInflows);
        event.addData("netInflowsRatio", netInflowsRatio);
        event.addData("timestamp", timestamp);
        return event;
    }
}
