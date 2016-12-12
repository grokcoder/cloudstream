package cn.edu.zju.vlis.eventhub;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wangxiaoyi on 16/6/2.
 * client for obtain event from the event bus
 */
public interface IEventBusClient<T> extends Serializable{

    void connect(String connectionString);
    void connect();

    void subscribe(List<EventSchema> interestedEvents);

    void send(T event, String topic);// syn send
    void send(T event);// syn send

    List<T> pollEvents();

    void close();

}
