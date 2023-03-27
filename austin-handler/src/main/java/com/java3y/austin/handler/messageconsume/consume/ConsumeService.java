package com.java3y.austin.handler.messageconsume.consume;


import com.java3y.austin.common.domain.MessageInfo;
import com.java3y.austin.support.domain.MessageTemplate;

import java.util.List;

/**
 * 消费消息服务
 * 也即真正消费消息啦！！！
 * 消费来自不同渠道的消息（
 * @author 3y
 */
public interface ConsumeService {

    /**
     * 从MQ拉到消息进行消费，发送消息
     *
     * @param messageInfoLists
     */
    void consume2Send(List<MessageInfo> messageInfoLists);


    /**
     * 从MQ拉到消息进行消费，撤回消息
     *
     * @param messageTemplate
     */
    void consume2recall(MessageTemplate messageTemplate);


}
