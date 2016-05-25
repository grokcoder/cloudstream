package cn.edu.zju.vlis.util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wangxiaoyi on 16/4/27.
 */
public class RandomHelperTest {

    private static final Logger LOG = LoggerFactory.getLogger(RandomHelperTest.class.getSimpleName());

    @Test
    public void testGetIntFromRange(){
        for (int i = 0; i < 10; i ++)
            LOG.debug(RandomHelper.getIntFromRange(2, 100) + "");
    }

    @Test
    public void testGetDoubleFromRange(){
        for (int i = 0; i < 10; i ++)
            LOG.debug(MathHelper.getDouble(RandomHelper.getDoubleFromRange(10.0, 100), "#.###") + "");
    }
}
