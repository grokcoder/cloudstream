package cn.edu.zju.vlis.events;

/**
 * Created by wangxiaoyi on 16/6/2.
 * client for obtain event from the event bus
 */
public interface IEventBusClient {

    void connect(String connectionString);

    /**
     * subscribe event by envent name
     * @param eventName name of event
     */
    void subscribe(String eventName);

    EventSchema receive();

    void close();

}
