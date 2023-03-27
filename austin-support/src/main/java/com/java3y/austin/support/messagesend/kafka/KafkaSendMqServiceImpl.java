package com.java3y.austin.support.messagesend.kafka;

import cn.hutool.core.util.StrUtil;
import com.java3y.austin.support.constans.MessageQueuePipeline;
import com.java3y.austin.support.messagesend.SendMqService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;


/**
 * @author 3y
 * kafka 发送实现类
 */
@Slf4j
@Service
//@ConditionalOnProperty注解允许根据配置属性的存在条件有条件地注册bean。
//该注解提供了havingValue属性，它定义了一个属性必须具有的值，以便将特定的Bean添加到Spring容器中。
//因此，易知，该对象虽然加了@Service，但此时还不一定能被加载至ioc容器，还必须满足配置文件中有相应的配置消息！！！
@ConditionalOnProperty(name = "austin.mq.pipeline", havingValue = MessageQueuePipeline.KAFKA)
public class KafkaSendMqServiceImpl implements SendMqService {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Value("${austin.business.tagId.key}")
    private String tagIdKey;

    /**
     * 这就是发送消息到kafka的最终方法，之后的代码不用再跟！
     * @param topic
     * @param jsonValue
     * @param tagId
     */
    @Override
    public void send(String topic, String jsonValue, String tagId) {
        if (StrUtil.isNotBlank(tagId)) {
            List<Header> headers = Collections.singletonList(new RecordHeader(tagIdKey, tagId.getBytes(StandardCharsets.UTF_8)));
            kafkaTemplate.send(new ProducerRecord(topic, null, null, null, jsonValue, headers));
        } else {
            kafkaTemplate.send(topic, jsonValue);
        }
    }

    @Override
    public void send(String topic, String jsonValue) {
        send(topic, jsonValue, null);
    }
}
