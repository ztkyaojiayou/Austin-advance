package com.java3y.austin.handler.messageconsume.messagehandle.discard;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.java3y.austin.common.constant.CommonConstant;
import com.java3y.austin.common.domain.AnchorInfo;
import com.java3y.austin.common.domain.MessageInfo;
import com.java3y.austin.common.enums.AnchorState;
import com.java3y.austin.support.config.configfetch.ConfigService;
import com.java3y.austin.support.utils.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 消费消息--丢弃模板消息
 *
 * @author 3y.
 */
@Service
public class DiscardMessageService {
  private static final String DISCARD_MESSAGE_KEY = "discardMsgIds";

  @Autowired private ConfigService config;

  @Autowired private LogUtils logUtils;

  /**
   * 丢弃消息，配置在apollo
   * 即丢弃一些配置中配置的模板id对应的消息
   * @param messageInfo
   * @return
   */
  public boolean isDiscard(MessageInfo messageInfo) {
    // 获取配置信息中配置的需要丢弃的模板id
    // 配置示例:	["1","2"]
    JSONArray array =
        JSON.parseArray(
            config.getProperty(DISCARD_MESSAGE_KEY, CommonConstant.EMPTY_VALUE_JSON_ARRAY));
    // 丢弃，同时记录一下
    if (array.contains(String.valueOf(messageInfo.getMessageTemplateId()))) {
      logUtils.print(
          AnchorInfo.builder()
              .businessId(messageInfo.getBusinessId())
              .ids(messageInfo.getReceiver())
              .state(AnchorState.DISCARD.getCode())
              .build());
      return true;
    }
    return false;
  }
}
