package com.java3y.austin.service.api.impl.service;

import cn.monitor4all.logRecord.annotation.OperationLog;
import com.java3y.austin.common.vo.BasicResultVO;
import com.java3y.austin.service.api.domain.BatchSendRequest;
import com.java3y.austin.service.api.domain.SendRequest;
import com.java3y.austin.service.api.domain.SendResponse;
import com.java3y.austin.service.api.impl.domain.MessageSendParamInfo;
import com.java3y.austin.service.api.service.SendService;
import com.java3y.austin.support.pipeline.MessageSendHandler;
import com.java3y.austin.support.pipeline.MessageSendContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 发送接口
 *
 * @author 3y
 */
@Service
public class SendServiceImpl implements SendService {

  /**
   * 注入流程控制器对象，用于指向责任链的各流程节点！！！
   * 此时就需要去思考，在使用它只之前，它注入了哪些内容！！！
   * 易知就是将各任务任务对应的流程节点事先注入了！
   */
  @Autowired private MessageSendHandler messageSendHandler;

  @Override
  @OperationLog(
      bizType = "SendService#send",
      bizId = "#sendRequest.messageTemplateId",
      msg = "#sendRequest")
  public SendResponse send(SendRequest sendRequest) {
    /** 1）这里的核心任务就是通过前端传入的参数构建出ProcessContext对象！！！ */
    // 1.构建要发送的消息内容SendTaskModel--均实现ProcessModel接口
    // 使用了构造器设计模式！！！（但其实就是setXXX的链式调用而已！！！）
    MessageSendParamInfo messageSendParamInfo =
        MessageSendParamInfo.builder()
            .messageTemplateId(sendRequest.getMessageTemplateId())
            .messageSendParamList(Collections.singletonList(sendRequest.getMessageSendParam()))
            .build();

    // 2.再构建/封装责任链上下文--均实现ProcessModel接口
    MessageSendContext context =
        MessageSendContext.builder()
            .code(sendRequest.getCode())
            .messageSendModel(messageSendParamInfo)
            .needBreak(false)
            .response(BasicResultVO.success())
            .build();

    /** 2）执行责任链 */
    MessageSendContext process = messageSendHandler.executeMessageSendStep(context);

    return new SendResponse(process.getResponse().getStatus(), process.getResponse().getMsg());
  }

  @Override
  @OperationLog(
      bizType = "SendService#batchSend",
      bizId = "#batchSendRequest.messageTemplateId",
      msg = "#batchSendRequest")
  public SendResponse batchSend(BatchSendRequest batchSendRequest) {
    MessageSendParamInfo messageSendParamInfo =
        MessageSendParamInfo.builder()
            .messageTemplateId(batchSendRequest.getMessageTemplateId())
            .messageSendParamList(batchSendRequest.getMessageSendParamList())
            .build();

    MessageSendContext context =
        MessageSendContext.builder()
            .code(batchSendRequest.getCode())
            .messageSendModel(messageSendParamInfo)
            .needBreak(false)
            .response(BasicResultVO.success())
            .build();

    MessageSendContext process = messageSendHandler.executeMessageSendStep(context);

    return new SendResponse(process.getResponse().getStatus(), process.getResponse().getMsg());
  }
}
