package com.java3y.austin.handler.messageconsume.messagereceiver.eventbus;

import com.google.common.eventbus.Subscribe;
import com.java3y.austin.common.domain.MessageInfo;
import com.java3y.austin.handler.messageconsume.consume.ConsumeService;
import com.java3y.austin.support.common.constans.MessageQueuePipeline;
import com.java3y.austin.support.domain.MessageTemplate;
import com.java3y.austin.support.messagesend.eventbus.EventBusListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 3y
 */
@Component
@ConditionalOnProperty(name = "austin.mq.pipeline", havingValue = MessageQueuePipeline.EVENT_BUS)
public class EventBusReceiver implements EventBusListener {

    @Autowired
    private ConsumeService consumeService;

    @Override
    @Subscribe
    public void consume(List<MessageInfo> lists) {
        consumeService.consume2Send(lists);

    }

    @Override
    @Subscribe
    public void recall(MessageTemplate messageTemplate) {
        consumeService.consume2recall(messageTemplate);
    }
}
