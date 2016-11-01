package cn.edu.zju.vlis.eventhub;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;


/**
 * Created by wangxiaoyi on 16/10/31.
 */
public class EventSerializer{

    private static Kryo kryo = new Kryo();

    static {
        kryo.register(EventData.class);
        kryo.register(Event.class);
        kryo.register(EventSchema.class);
    }

    public static byte[] toBytes(EventData eventData){
        Output output = new Output(1024 * 4);//todo: make it configurable
        kryo.writeObject(output, eventData);
        output.close();
        return output.toBytes();
    }

    public static EventData toEventData(byte[] bytes){
        Input input = new Input(bytes);
        EventData eventData = kryo.readObject(input, EventData.class);
        input.close();
        return eventData;
    }

}
