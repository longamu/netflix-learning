package com.yourbatman.hystrix;

import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.properties.HystrixDynamicProperties;
import org.junit.Test;

public class TestSPI {

    @Test
    public void fun1() {
        HystrixPlugins instance = HystrixPlugins.getInstance();
        HystrixDynamicProperties dynamicProperties = instance.getDynamicProperties();
        System.out.println(dynamicProperties.getString("name", null).get());

        System.out.println("===========================================");
        // 类型
        System.out.println(dynamicProperties.getClass());
        System.out.println(instance.getMetricsPublisher().getClass());
        System.out.println(instance.getEventNotifier().getClass());
        System.out.println(instance.getConcurrencyStrategy().getClass());
    }
}
