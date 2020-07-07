package com.sixj.sentinel.service8101.config;

import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author sixiaojie
 * @date 2020-07-07-10:09
 */
@Configuration
public class SentinelNacosRuleInitConfig implements InitFunc {
    @Override
    public void init() throws Exception {
        System.err.println("===========InitFunc=============");
        String remoteAddress = "127.0.0.1:8848";
        String groupId = "DEFAULT_GROUP";
        String dataId = "sentinel-service8101";
        Converter<String, List<FlowRule>> parser = source -> JSON.parseObject(source,new TypeReference<List<FlowRule>>() {});
        ReadableDataSource<String, List<FlowRule>> nacosDataSource = new NacosDataSource<>(remoteAddress, groupId, dataId, parser);
        FlowRuleManager.register2Property(nacosDataSource.getProperty());
    }
}
