package com.java3y.austin.service.api.impl.service;

import com.java3y.austin.common.vo.BasicResultVO;
import com.java3y.austin.service.api.domain.SendRequest;
import com.java3y.austin.service.api.domain.SendResponse;
import com.java3y.austin.service.api.impl.domain.MessageSendParamInfo;
import com.java3y.austin.service.api.service.RecallService;
import com.java3y.austin.support.pipeline.MessageSendContext;
import com.java3y.austin.support.pipeline.MessageSendHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 撤回接口
 *
 * @author 3y
 */
@Service
public class RecallServiceImpl implements RecallService {

    @Autowired
    private MessageSendHandler messageSendHandler;

    @Override
    public SendResponse recall(SendRequest sendRequest) {
        /** 1）这里的核心任务也是通过前端传入的参数构建出ProcessContext对象！！！ */
        MessageSendParamInfo messageSendParamInfo = MessageSendParamInfo.builder()
                .messageTemplateId(sendRequest.getMessageTemplateId())
                .build();
        MessageSendContext context = MessageSendContext.builder()
                .code(sendRequest.getCode())
                .messageSendModel(messageSendParamInfo)
                .needBreak(false)
                .response(BasicResultVO.success()).build();
        /** 2）再执行责任链 */
        MessageSendContext process = messageSendHandler.executeMessageSendStep(context);
        return new SendResponse(process.getResponse().getStatus(), process.getResponse().getMsg());
    }
}
