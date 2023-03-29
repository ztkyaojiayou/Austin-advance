package com.java3y.austin.support.messagesend.springeventbus;

import com.java3y.austin.support.common.constans.MessageQueuePipeline;
import com.java3y.austin.support.messagesend.SendMqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * 描述：发布spring事件 使用ApplicationContext的publishEvent方法发送！
 *
 * @author tony
 * @date 2023/2/6 11:11
 */
@Slf4j
@Service
@ConditionalOnProperty(
    name = "austin.mq.pipeline",
    havingValue = MessageQueuePipeline.SPRING_EVENT_BUS)
public class SpringEventBusSendMqServiceImpl implements SendMqService {

  @Autowired private ApplicationContext applicationContext;

  @Override
  public void send(String topic, String jsonValue, String tagId) {
    AustinSpringEventSource source =
        AustinSpringEventSource.builder().topic(topic).jsonValue(jsonValue).tagId(tagId).build();
    AustinSpringEventBusEvent austinSpringEventBusEvent =
        new AustinSpringEventBusEvent(this, source);
    // 发布事件
    // 要注意的是，Spring事件机制中的发布事件不是说是把消息发送到了MQ中，
    // 而只是将消息封装到了AustinSpringEventBusEvent对象中，
    // 在监听器端会以同步的方式拿到该对象并进行消费！！！
    // （也即直接拿到并消费，而并不走MQ，因为这单纯是spring自己的事件机制，与MQ毫无关联！！！）
    applicationContext.publishEvent(austinSpringEventBusEvent);
  }

  @Override
  public void send(String topic, String jsonValue) {
    send(topic, jsonValue, null);
  }
}
