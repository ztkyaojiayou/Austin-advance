package com.java3y.austin.handler.messageconsume.messagereceiver.springeventbus;

import com.java3y.austin.common.domain.MessageInfo;
import com.java3y.austin.handler.messageconsume.consume.ConsumeService;
import com.java3y.austin.support.constans.MessageQueuePipeline;
import com.java3y.austin.support.domain.MessageTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 描述：
 *
 * @author tony
 * @date 2023/2/6 11:18
 */
@Component
@ConditionalOnProperty(name = "austin.mq.pipeline", havingValue = MessageQueuePipeline.SPRING_EVENT_BUS)
public class SpringEventBusReceiver {

    @Autowired
    private ConsumeService consumeService;

    public void consume(List<MessageInfo> lists) {
        consumeService.consume2Send(lists);
    }

    public void recall(MessageTemplate messageTemplate) {
        consumeService.consume2recall(messageTemplate);
    }
}
