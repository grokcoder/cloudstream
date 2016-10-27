package cn.edu.zju.vlis.events;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangxiaoyi on 16/10/27.
 * contains only the event data
 */
public class EventData implements Serializable{

    private String eventSchemaName;
    private Map<String, Object> dataMap;// support event of map data type

    public EventData(String eName, Map<String, Object> dataMap){
        this.eventSchemaName = eName;
        this.dataMap = dataMap;
    }

    public void addData(String key, Object value){
        if(dataMap == null) dataMap = new HashMap<>();
        dataMap.put(key, value);
    }

    public String getEventSchemaName() {
        return eventSchemaName;
    }

    public void setEventSchemaName(String eventSchemaName) {
        this.eventSchemaName = eventSchemaName;
    }


    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    @Override
    public String toString() {
        return "EventData{" +
                "eventSchemaName='" + eventSchemaName + '\'' +
                ", dataMap=" + dataMap +
                '}';
    }
}
