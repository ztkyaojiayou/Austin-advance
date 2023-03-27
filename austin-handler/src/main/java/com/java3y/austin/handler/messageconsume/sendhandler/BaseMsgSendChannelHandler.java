package com.java3y.austin.handler.messageconsume.sendhandler;

import com.java3y.austin.common.domain.AnchorInfo;
import com.java3y.austin.common.domain.MessageInfo;
import com.java3y.austin.common.enums.AnchorState;
import com.java3y.austin.handler.messageconsume.messagehandle.flowcontrol.FlowControlFactory;
import com.java3y.austin.handler.messageconsume.messagehandle.flowcontrol.FlowControlParam;
import com.java3y.austin.support.utils.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Objects;

/**
 * @author 3y 发送各个渠道的handler
 */
public abstract class BaseMsgSendChannelHandler implements MsgSendChannelHandler {
  @Autowired private HandlerHolder handlerHolder;
  @Autowired private LogUtils logUtils;
  @Autowired private FlowControlFactory flowControlFactory;

  /** 标识渠道的Code 子类初始化的时候指定 */
  protected Integer channelCode;

  /** 限流相关的参数 子类初始化的时候指定 */
  protected FlowControlParam flowControlParam;

  /** 初始化消息发送渠道与对应的具体的Handler的映射关系 todo 这咋初始化？？？ */
  @PostConstruct
  private void init() {
    handlerHolder.putHandler(channelCode, this);
  }

  @Override
  public void doSend(MessageInfo messageInfo) {
    // 1.先进行流量控制
    this.flowControl(messageInfo);
    // 2.再将消息下发到具体的业务方，比如钉钉、飞书等11种方式
    // 若发送成功，则返回true，否则返回false！
    if (channel4MsgHandler(messageInfo)) {
      //2.1记录发送成功日志
      logUtils.print(
          AnchorInfo.builder()
              .state(AnchorState.SEND_SUCCESS.getCode())
              .businessId(messageInfo.getBusinessId())
              .ids(messageInfo.getReceiver())
              .build());
      return;
    }
    //2.2记录发送失败日志
    logUtils.print(
        AnchorInfo.builder()
            .state(AnchorState.SEND_FAIL.getCode())
            .businessId(messageInfo.getBusinessId())
            .ids(messageInfo.getReceiver())
            .build());
  }

  /**
   * 流量控制
   *
   * @param messageInfo
   */
  public void flowControl(MessageInfo messageInfo) {
    // 只有子类指定了限流参数，才需要限流
    if (Objects.nonNull(flowControlParam)) {
      flowControlFactory.flowControl(messageInfo, flowControlParam);
    }
  }

  /**
   * 将消息下发到具体的业务方的统一的接口
   *
   * @param messageInfo
   * @return
   */
  public abstract boolean channel4MsgHandler(MessageInfo messageInfo);
}
