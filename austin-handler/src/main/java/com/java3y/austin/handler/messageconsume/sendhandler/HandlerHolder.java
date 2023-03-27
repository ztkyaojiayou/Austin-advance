package com.java3y.austin.handler.messageconsume.sendhandler;


import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * channel->Handler的映射关系
 *
 * @author 3y
 */
@Component
public class HandlerHolder {

    //todo 那么该HandlerHolder中的map在哪儿初始化的？
    private Map<Integer, MsgSendChannelHandler> handlers = new HashMap<>(128);

    public void putHandler(Integer channelCode, MsgSendChannelHandler msgSendChannelHandler) {
        handlers.put(channelCode, msgSendChannelHandler);
    }

    public MsgSendChannelHandler route(Integer channelCode) {
        return handlers.get(channelCode);
    }
}
