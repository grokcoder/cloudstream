package cn.edu.zju.vlis.eventhub;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangxiaoyi on 16/6/2.
 * wrapper for an event
 */

public class EventSchema implements Serializable{
    private String eventName;
    private Map<String, Object> typeMap;


    public EventSchema(String eventName, Map<String, Object> typeMap) {
        this.eventName = eventName;
        this.typeMap = typeMap;
    }

    public EventSchema(String eventName){
        this.eventName = eventName;
        this.typeMap = new HashMap<>();
    }

    /**
     * add more attribute
     * @param key attribute name
     * @param type attribute type, eg: int.class
     */
    public void addAttribute(String key, Object type){
        typeMap.put(key, type);
    }

    public void removeAttribute(String key){
        typeMap.remove(key);
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Map<String, Object> getTypeMap() {
        return typeMap;
    }

    public void setTypeMap(Map<String, Object> typeMap) {
        this.typeMap = typeMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventSchema)) return false;

        EventSchema event = (EventSchema) o;

        if (getEventName() != null ? !getEventName().equals(event.getEventName()) : event.getEventName() != null)
            return false;
        return getTypeMap() != null ? getTypeMap().equals(event.getTypeMap()) : event.getTypeMap() == null;

    }

    @Override
    public int hashCode() {
        int result = getEventName() != null ? getEventName().hashCode() : 0;
        result = 31 * result + (getTypeMap() != null ? getTypeMap().hashCode() : 0);
        return result;
    }
}
