package cn.edu.zju.vlis.example.generator;

/**
 * Created by wangxiaoyi on 16/4/27.
 */
public abstract class  StreamEventGenerator<T>{

    /**
     * generate next event object
     * @return next event
     */
    public abstract T next();

}
