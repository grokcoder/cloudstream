package cn.edu.zju.vlis.eventhub;

/**
 * Created by wangxiaoyi on 16/10/27.
 * represent the event entity
 */
public class Event {

    private EventData data;
    private EventSchema schema;

    public Event(EventData data, EventSchema schema){
        this.data = data;
        this.schema = schema;
    }

    public EventData getData() {
        return data;
    }

    public void setData(EventData data) {
        this.data = data;
    }

    public EventSchema getSchema() {
        return schema;
    }

    public void setSchema(EventSchema schema) {
        this.schema = schema;
    }
}
