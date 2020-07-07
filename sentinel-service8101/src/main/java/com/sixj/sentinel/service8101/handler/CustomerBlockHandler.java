package com.sixj.sentinel.service8101.handler;

import com.alibaba.csp.sentinel.slots.block.BlockException;

/**
 * @author sixiaojie
 * @date 2020-07-06-15:41
 */
public class CustomerBlockHandler {

    public static String handlerException(BlockException exception){
        return "CustomerBlockHandler.handlerException=====";
    }
}
