package cn.edu.zju.vlis.xiaoyi.util;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Created by wangxiaoyi on 16/4/27.
 */
public class MathHelperTest {

    @Test
    public void testGetDouble(){
        Assert.assertEquals(98.12, MathHelper.getDouble(98.123, "#.##"));
    }
}
