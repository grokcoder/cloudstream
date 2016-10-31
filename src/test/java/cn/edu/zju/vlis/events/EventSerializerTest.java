package cn.edu.zju.vlis.events;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by wangxiaoyi on 16/10/31.
 */
public class EventSerializerTest {


    @Test
    public void testToBytes(){
        EventData eventData = new EventData("Event1");
        eventData.addData("id", 123);
        eventData.addData("name", "haaa");

        byte[] bytes = EventSerializer.toBytes(eventData);
        EventData refEventData = EventSerializer.toEventData(bytes);
        Assert.assertEquals(refEventData.equals(eventData), true);
    }
}
