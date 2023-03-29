package com.java3y.austin.handler.messageconsume.messagereceiver.springeventbus;

import com.alibaba.fastjson.JSON;
import com.java3y.austin.common.domain.MessageInfo;
import com.java3y.austin.support.common.constans.MessageQueuePipeline;
import com.java3y.austin.support.domain.MessageTemplate;
import com.java3y.austin.support.messagesend.springeventbus.AustinSpringEventBusEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

/**
 * 描述：spring事件监听器！
 * 首先，事件是从austin.support.mq.springeventbus包下进行发布的
 * 这里就是监听这些信息！通过实现 ApplicationListener来绑定一个事件进行监听！
 * ApplicationContext中的事件处理是通过ApplicationEvent类和ApplicationListener接口来提供的，
 * 通过ApplicationContext的publishEvent()方法发布到ApplicationListener;
 * 参考链接：https://blog.csdn.net/jike11231/article/details/124872298
 * @author tony
 * @date 2023/2/6 11:19
 */
@Service
@ConditionalOnProperty(name = "austin.mq.pipeline", havingValue = MessageQueuePipeline.SPRING_EVENT_BUS)
public class SpringEventBusReceiverListener implements ApplicationListener<AustinSpringEventBusEvent> {

    @Autowired
    private SpringEventBusReceiver springEventBusReceiver;

    @Value("${austin.business.topic.name}")
    private String sendTopic;
    @Value("${austin.business.recall.topic.name}")
    private String recallTopic;

    /**
     * 监听消息并消费！
     * @param event
     */
    @Override
    public void onApplicationEvent(AustinSpringEventBusEvent event) {
        String topic = event.getAustinSpringEventSource().getTopic();
        String jsonValue = event.getAustinSpringEventSource().getJsonValue();
        if (topic.equals(sendTopic)) {
            //具体的消费方法
            springEventBusReceiver.consume(JSON.parseArray(jsonValue, MessageInfo.class));
        } else if (topic.equals(recallTopic)) {
            springEventBusReceiver.recall(JSON.parseObject(jsonValue, MessageTemplate.class));
        }
    }
}
