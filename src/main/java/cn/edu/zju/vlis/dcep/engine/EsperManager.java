package cn.edu.zju.vlis.dcep.engine;

import com.espertech.esper.client.UpdateListener;

/**
 * Created by wangxiaoyi on 16/5/30.
 * manage esper engine in storm bolt
 */
public interface EsperManager {

    /**
     * add event type for esper
     * @param eventTypeName event type identifier
     * @param classz class of the event
     */
    void addEventType(String eventTypeName, Class classz);

    void startEsper();

    /**
     * send event to esper
     * @param event
     */
    void sendEvent(Object event);

    /**
     * register EPL for this esper
     * @param EPL
     */
    void registerEPL(String EPL);

    /**
     * register EPL and related listener
     * @param EPL
     * @param listener
     */
    void registerEPL(String EPL, UpdateListener listener);

    void stopEsper();
}
