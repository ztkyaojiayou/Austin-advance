package com.java3y.austin.handler.messageconsume.messagehandle.shield;

import com.java3y.austin.common.domain.MessageInfo;

/**
 * 消费消息--屏蔽
 *
 * @author 3y
 */
public interface ShieldService {


    /**
     * 屏蔽消息
     *
     * @param messageInfo
     */
    void shield(MessageInfo messageInfo);
}
