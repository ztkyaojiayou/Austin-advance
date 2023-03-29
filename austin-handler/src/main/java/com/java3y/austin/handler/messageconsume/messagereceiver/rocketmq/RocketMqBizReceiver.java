package com.java3y.austin.handler.messageconsume.messagereceiver.rocketmq;

import com.alibaba.fastjson.JSON;
import com.java3y.austin.common.domain.MessageInfo;
import com.java3y.austin.handler.messageconsume.consume.ConsumeService;
import com.java3y.austin.support.common.constans.MessageQueuePipeline;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description:
 *
 * @author elpsycongroo
 * create date: 2022/7/16
 */
@Component
@ConditionalOnProperty(name = "austin.mq.pipeline", havingValue = MessageQueuePipeline.ROCKET_MQ)
@RocketMQMessageListener(topic = "${austin.business.topic.name}",
        consumerGroup = "${austin.rocketmq.biz.consumer.group}",
        selectorType = SelectorType.TAG,
        selectorExpression = "${austin.business.tagId.value}"
)
public class RocketMqBizReceiver implements RocketMQListener<String> {

    @Autowired
    private ConsumeService consumeService;

    @Override
    public void onMessage(String message) {
        if (StringUtils.isBlank(message)) {
            return;
        }
        List<MessageInfo> messageInfoLists = JSON.parseArray(message, MessageInfo.class);
        consumeService.consume2Send(messageInfoLists);
    }
}
