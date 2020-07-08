package com.sixj.sentinel.service8101.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 以代码的形式替换application.properties中臃肿的配置，这些配置用来从nacos获取规则信息。
 * 实现 BeanFactoryPostProcessor 接口的目的是让加载时机提前，高于Bean的初始化
 * @author sixiaojie
 * @date 2020-07-08-10:41
 */
@Component
public class SentinelNacosConfig implements EnvironmentAware, BeanFactoryPostProcessor {

    private static final String GROUP_ID = "SENTINEL_GROUP";
    private String serverAddr;
    private String namespace;
    private String applicationName;

    @Override
    public void setEnvironment(Environment environment) {

        Properties p = new Properties();
        serverAddr = environment.getProperty("spring.cloud.nacos.discovery.server-addr");
        namespace = environment.getProperty("spring.cloud.nacos.discovery.namespace");
        applicationName = environment.getProperty("spring.application.name");

        // 限流规则
        putProperties(p,"flow");

        // 降级规则
        putProperties(p,"degrade");

        // 系统规则
        putProperties(p,"system");

        // 授权规则
        putProperties(p,"authority");

        // 热点规则
        putProperties(p,"param_flow");

        //defaultProperties是默认的属性命名空间
        ((ConfigurableEnvironment) environment).getPropertySources()
                .addFirst(new PropertiesPropertySource("defaultProperties", p));
    }

    private void putProperties(Properties p,String ruleType){
        p.put("spring.cloud.sentinel.datasource."+ruleType+".nacos.server-addr", serverAddr);
        p.put("spring.cloud.sentinel.datasource."+ruleType+".nacos.dataId", applicationName+"-"+ruleType+"-rules");
        p.put("spring.cloud.sentinel.datasource."+ruleType+".nacos.groupId", GROUP_ID);
        p.put("spring.cloud.sentinel.datasource."+ruleType+".nacos.rule-type", ruleType);
        p.put("spring.cloud.sentinel.datasource."+ruleType+".nacos.namespace", namespace);
    }

    /**
     * 被替换的配置
     * # 限流规则
     * spring.cloud.sentinel.datasource.flow.nacos.server-addr=${spring.cloud.nacos.discovery.server-addr}
     * spring.cloud.sentinel.datasource.flow.nacos.dataId=${spring.application.name}-flow-rules
     * spring.cloud.sentinel.datasource.flow.nacos.groupId=SENTINEL_GROUP
     * spring.cloud.sentinel.datasource.flow.nacos.rule-type=flow
     * spring.cloud.sentinel.datasource.flow.nacos.namespace=${spring.cloud.nacos.discovery.namespace}
     * # 降级规则
     * spring.cloud.sentinel.datasource.degrade.nacos.server-addr=${spring.cloud.nacos.discovery.server-addr}
     * spring.cloud.sentinel.datasource.degrade.nacos.dataId=${spring.application.name}-degrade-rules
     * spring.cloud.sentinel.datasource.degrade.nacos.groupId=SENTINEL_GROUP
     * spring.cloud.sentinel.datasource.degrade.nacos.rule-type=degrade
     * spring.cloud.sentinel.datasource.degrade.nacos.namespace=${spring.cloud.nacos.discovery.namespace}
     *
     * # 系统规则
     * spring.cloud.sentinel.datasource.system.nacos.server-addr=${spring.cloud.nacos.discovery.server-addr}
     * spring.cloud.sentinel.datasource.system.nacos.dataId=${spring.application.name}-system-rules
     * spring.cloud.sentinel.datasource.system.nacos.groupId=SENTINEL_GROUP
     * spring.cloud.sentinel.datasource.system.nacos.rule-type=system
     * spring.cloud.sentinel.datasource.system.nacos.namespace=${spring.cloud.nacos.discovery.namespace}
     *
     * # 授权规则
     * spring.cloud.sentinel.datasource.authority.nacos.server-addr=${spring.cloud.nacos.discovery.server-addr}
     * spring.cloud.sentinel.datasource.authority.nacos.dataId=${spring.application.name}-authority-rules
     * spring.cloud.sentinel.datasource.authority.nacos.groupId=SENTINEL_GROUP
     * spring.cloud.sentinel.datasource.authority.nacos.rule-type=authority
     * spring.cloud.sentinel.datasource.authority.nacos.namespace=${spring.cloud.nacos.discovery.namespace}
     *
     * # 热点规则
     * spring.cloud.sentinel.datasource.param-flow.nacos.server-addr=${spring.cloud.nacos.discovery.server-addr}
     * spring.cloud.sentinel.datasource.param-flow.nacos.dataId=${spring.application.name}-param-flow-rules
     * spring.cloud.sentinel.datasource.param-flow.nacos.groupId=SENTINEL_GROUP
     * spring.cloud.sentinel.datasource.param-flow.nacos.rule-type=param_flow
     * spring.cloud.sentinel.datasource.param-flow.nacos.namespace=${spring.cloud.nacos.discovery.namespace}
     */



    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }
}
