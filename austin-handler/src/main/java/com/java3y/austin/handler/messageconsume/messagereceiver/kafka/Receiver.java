package com.java3y.austin.handler.messageconsume.messagereceiver.kafka;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.java3y.austin.common.domain.MessageInfo;
import com.java3y.austin.handler.messageconsume.consume.ConsumeService;
import com.java3y.austin.handler.utils.GroupIdMappingUtils;
import com.java3y.austin.support.common.constans.MessageQueuePipeline;
import com.java3y.austin.support.domain.MessageTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * @author 3y
 * 消费MQ的消息
 */
@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@ConditionalOnProperty(name = "austin.mq.pipeline", havingValue = MessageQueuePipeline.KAFKA)
public class Receiver {
    @Autowired
    private ConsumeService consumeService;

    /**
     * 消费消息
     * 1.当前用户只消费自己发送的消息
     * 2.当前消费者只消费
     * @param consumerRecord
     * @param topicGroupId 当前消费者组的GroupId，它在初始化时被写在了Header中，因此这里就使用@Header获取/监听
     */
    @KafkaListener(topics = "#{'${austin.business.topic.name}'}", containerFactory = "filterContainerFactory")
    public void consumer(ConsumerRecord<?, String> consumerRecord, @Header(KafkaHeaders.GROUP_ID) String topicGroupId) {
        Optional<String> kafkaMessage = Optional.ofNullable(consumerRecord.value());
        if (kafkaMessage.isPresent()) {
            //序列化成自定义对象
            //todo 为什么是list？？？不是单条吗？
            List<MessageInfo> messageInfoLists = JSON.parseArray(kafkaMessage.get(), MessageInfo.class);
            //1.根据TaskInfo获取当前消息的消费者组id/groupId，也即消息分发，也即当前消息该由哪个消费者组消费！！！
            String messageGroupId = GroupIdMappingUtils.getGroupIdByTaskInfo(CollUtil.getFirst(messageInfoLists.iterator()));
            /**
             * 2，对比当前消费者组的只消费topicGroupId，只有和messageGroupId相等时才消费！！！
             *
             * 每个消费者组会消费监听（其实也是消费）所有消息，但只真正处理/消费他们自身关心的消息--关键，掌握！！！
             * 注意：这和自定义过滤器中的消息过滤是两回事，切勿混淆！！！
             */
            if (topicGroupId.equals(messageGroupId)) {
                //调用真正的消费服务进行消费！！！
                consumeService.consume2Send(messageInfoLists);
            }
        }
    }

    /**
     * 撤回消息
     *
     * @param consumerRecord
     */
    @KafkaListener(topics = "#{'${austin.business.recall.topic.name}'}", groupId = "#{'${austin.business.recall.group.name}'}", containerFactory = "filterContainerFactory")
    public void recall(ConsumerRecord<?, String> consumerRecord) {
        Optional<String> kafkaMessage = Optional.ofNullable(consumerRecord.value());
        if (kafkaMessage.isPresent()) {
            MessageTemplate messageTemplate = JSON.parseObject(kafkaMessage.get(), MessageTemplate.class);
            consumeService.consume2recall(messageTemplate);
        }
    }
}
