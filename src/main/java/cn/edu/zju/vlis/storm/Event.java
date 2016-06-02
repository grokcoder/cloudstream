package cn.edu.zju.vlis.storm;

/**
 * Created by wangxiaoyi on 16/6/2.
 * wrapper for an event
 */
public class Event {
    private Object eventInstance;
    private String eventType;

    public Event(Object eventInstance, String eventType) {
        this.eventInstance = eventInstance;
        this.eventType = eventType;
    }

    public Object getEventInstance() {
        return eventInstance;
    }

    public String getEventType() {
        return eventType;
    }
}
