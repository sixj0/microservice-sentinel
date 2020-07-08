package com.sixj.sentinel.service8101.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.sixj.sentinel.service8101.handler.CustomerBlockHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sixiaojie
 * @date 2020-07-05-20:43
 */
@RestController
public class FlowLimitController {
    @Value("${spring.cloud.sentinel.datasource.flow.nacos.namespace}")
    private String namespace;

    @GetMapping("/testA")
    public String testA(){
        return "====testA,namespace:"+namespace;
    }

    @GetMapping("/testB")
    public String testB(){
        return "====testB";
    }

    /**
     * blockHandler只管配置违规情况，不管业务异常
     * @param p1
     * @param p2
     * @return
     */
    @GetMapping("/testHotKey")
    @SentinelResource(value = "testHotKey",blockHandler = "deal_testHotKey")
    public String testHotKey(@RequestParam(value = "p1",required = false)String p1,
                             @RequestParam(value = "p2",required = false)String p2 ){
        return "=====testHotKey";
    }

    public String deal_testHotKey(String p1, String p2, BlockException exception){
        return "=====deal_testHotKey";
    }

    /**
     * blockHandlerClass使用
     * @return
     */
    @GetMapping("/customerBlockHandler")
    @SentinelResource(value = "customerBlockHandler",
            blockHandlerClass = CustomerBlockHandler.class,
            blockHandler = "handlerException")
    public String customerBlockHandler(){
        return "自定义限流处理逻辑";
    }

    /**
     * fallBack只负责业务异常
     * @return
     */
    @GetMapping("/testFallBack")
    @SentinelResource(value = "testFallBack",fallback = "handlerFallback")
    public String testFallBack(){
//        int i = 4/0;
        throw new RuntimeException("自定义异常");
    }

    public String handlerFallback(Throwable e){
        return "兜底异常handlerFallback，exception内容:"+e.getMessage();
    }

}
