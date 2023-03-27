package com.java3y.austin.service.api.impl.step;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.base.Throwables;
import com.java3y.austin.common.enums.RespStatusEnum;
import com.java3y.austin.common.vo.BasicResultVO;
import com.java3y.austin.service.api.enums.BusinessCode;
import com.java3y.austin.service.api.impl.domain.MessageSendParamInfo;
import com.java3y.austin.support.messagesend.SendMqService;
import com.java3y.austin.support.pipeline.MessageSendStep;
import com.java3y.austin.support.pipeline.MessageSendContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author 3y
 * 4-将消息发送到MQ
 * 分为两种：普通发送和撤回消息
 */
@Slf4j
@Service
public class SendMqAction implements MessageSendStep<MessageSendParamInfo> {


    @Autowired
    private SendMqService sendMqService;

    @Value("${austin.business.topic.name}")
    private String sendMessageTopic;

    @Value("${austin.business.recall.topic.name}")
    private String austinRecall;
    @Value("${austin.business.tagId.value}")
    private String tagId;

    @Value("${austin.mq.pipeline}")
    private String mqPipeline;


    @Override
    public void execute(MessageSendContext<MessageSendParamInfo> context) {
        MessageSendParamInfo messageSendParamInfo = context.getMessageSendModel();
        try {
            //普通发送
            if (BusinessCode.COMMON_SEND.getCode().equals(context.getCode())) {
                String message = JSON.toJSONString(messageSendParamInfo.getMessageInfo(), SerializerFeature.WriteClassName);
                //发送消息到mq
                sendMqService.send(sendMessageTopic, message, tagId);
                //回撤消息（也只是一种消息类型而已！！！），同理
            } else if (BusinessCode.RECALL.getCode().equals(context.getCode())) {
                String message = JSON.toJSONString(messageSendParamInfo.getMessageTemplate(), SerializerFeature.WriteClassName);
                sendMqService.send(austinRecall, message, tagId);
            }
        } catch (Exception e) {
            context.setNeedBreak(true).setResponse(BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR));
            log.error("send {} fail! e:{},params:{}", mqPipeline, Throwables.getStackTraceAsString(e)
                    , JSON.toJSONString(CollUtil.getFirst(messageSendParamInfo.getMessageInfo().listIterator())));
        }
    }

}
