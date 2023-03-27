package com.java3y.austin.service.api.impl.step;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.java3y.austin.common.constant.AustinConstant;
import com.java3y.austin.common.enums.RespStatusEnum;
import com.java3y.austin.common.vo.BasicResultVO;
import com.java3y.austin.service.api.domain.MessageSendParam;
import com.java3y.austin.service.api.impl.domain.MessageSendParamInfo;
import com.java3y.austin.support.pipeline.MessageSendStep;
import com.java3y.austin.support.pipeline.MessageSendContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author 3y
 * @date 2021/11/22
 * @description 1-前置参数校验
 */
@Slf4j
@Service
public class PreParamCheckAction implements MessageSendStep<MessageSendParamInfo> {

    @Override
    public void execute(MessageSendContext<MessageSendParamInfo> context) {
        MessageSendParamInfo messageSendParamInfo = context.getMessageSendModel();

        Long messageTemplateId = messageSendParamInfo.getMessageTemplateId();
        List<MessageSendParam> messageSendParamList = messageSendParamInfo.getMessageSendParamList();

        // 1.没有传入消息模板Id 或者 messageParam
        if (Objects.isNull(messageTemplateId) || CollUtil.isEmpty(messageSendParamList)) {
            context.setNeedBreak(true).setResponse(BasicResultVO.fail(RespStatusEnum.CLIENT_BAD_PARAMETERS));
            return;
        }

        // 2.过滤 receiver=null 的messageParam
        List<MessageSendParam> resultMessageSendParamList = messageSendParamList.stream()
                .filter(messageParam -> !StrUtil.isBlank(messageParam.getReceiver()))
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(resultMessageSendParamList)) {
            context.setNeedBreak(true).setResponse(BasicResultVO.fail(RespStatusEnum.CLIENT_BAD_PARAMETERS));
            return;
        }

        // 3.过滤receiver大于100的请求
        if (resultMessageSendParamList.stream().anyMatch(messageParam -> messageParam.getReceiver().split(StrUtil.COMMA).length > AustinConstant.BATCH_RECEIVER_SIZE)) {
            context.setNeedBreak(true).setResponse(BasicResultVO.fail(RespStatusEnum.TOO_MANY_RECEIVER));
            return;
        }

        messageSendParamInfo.setMessageSendParamList(resultMessageSendParamList);

    }
}
