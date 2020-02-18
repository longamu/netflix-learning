package com.yourbatman.configuration;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.event.ConfigurationEvent;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.Test;

public class TestCoreAPI {

    @Test
    public void fun() throws ConfigurationException {
        Configurations configs = new Configurations();
        PropertiesConfiguration config = configs.properties("1.properties");

        // 监听ADD_PROPERTY添加属性事件
        config.addEventListener(ConfigurationEvent.ADD_PROPERTY, event -> {
            if (!event.isBeforeUpdate()) {
                System.out.printf("成功添加属性：%s = %s", event.getPropertyName(), event.getPropertyValue());
            }
        });


        config.addProperty("name", "YourBatman");
    }
}
