package cn.edu.zju.vlis.eventhub;

import java.io.Serializable;


/**
 * Created by wangxiaoyi on 16/6/2.
 * client for obtain event from the event bus
 */
public interface IEventBusProducer<T> extends Serializable{

    void connect(String connectionString);
    void connect();

    void send(T event, String topic);// syn send
    void sendAsync(T event, String topic);
    void send(T event);// syn send

    void close();

}
