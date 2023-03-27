package com.java3y.austin.service.api.impl.step;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReUtil;
import com.alibaba.fastjson.JSON;
import com.java3y.austin.common.domain.MessageInfo;
import com.java3y.austin.common.enums.IdType;
import com.java3y.austin.common.enums.RespStatusEnum;
import com.java3y.austin.common.vo.BasicResultVO;
import com.java3y.austin.service.api.impl.domain.MessageSendParamInfo;
import com.java3y.austin.support.pipeline.MessageSendStep;
import com.java3y.austin.support.pipeline.MessageSendContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 3y
 *     <p>3-后置参数检查
 */
@Slf4j
@Service
public class AfterParamCheckAction implements MessageSendStep<MessageSendParamInfo> {

  public static final String PHONE_REGEX_EXP =
      "^((13[0-9])|(14[5,7,9])|(15[0-3,5-9])|(166)|(17[0-9])|(18[0-9])|(19[1,8,9]))\\d{8}$";
  public static final String EMAIL_REGEX_EXP =
      "^[A-Za-z0-9-_\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";

  public static final HashMap<Integer, String> CHANNEL_REGEX_EXP = new HashMap<>();

  static {
    CHANNEL_REGEX_EXP.put(IdType.PHONE.getCode(), PHONE_REGEX_EXP);
    CHANNEL_REGEX_EXP.put(IdType.EMAIL.getCode(), EMAIL_REGEX_EXP);
  }

  @Override
  public void execute(MessageSendContext<MessageSendParamInfo> context) {
    MessageSendParamInfo messageSendParamInfo = context.getMessageSendModel();
    List<MessageInfo> messageInfo = messageSendParamInfo.getMessageInfo();

    // 1. 过滤掉不合法的手机号、邮件
    filterIllegalReceiver(messageInfo);

    if (CollUtil.isEmpty(messageInfo)) {
      context
          .setNeedBreak(true)
          .setResponse(BasicResultVO.fail(RespStatusEnum.CLIENT_BAD_PARAMETERS));
    }
  }

  /**
   * 如果指定类型是手机号，检测输入手机号是否合法 如果指定类型是邮件，检测输入邮件是否合法
   *
   * @param messageInfo
   */
  private void filterIllegalReceiver(List<MessageInfo> messageInfo) {
    Integer idType = CollUtil.getFirst(messageInfo.iterator()).getIdType();
    filter(messageInfo, CHANNEL_REGEX_EXP.get(idType));
  }

  /**
   * 利用正则过滤掉不合法的接收者
   * 对应list中元素的删除，有讲究的，需要使用迭代器进行删除！！！
   * 参考链接：https://blog.csdn.net/qq_35387940/article/details/112505679
   * @param messageInfo
   * @param regexExp
   */
  private void filter(List<MessageInfo> messageInfo, String regexExp) {
    Iterator<MessageInfo> iterator = messageInfo.iterator();
    while (iterator.hasNext()) {
      MessageInfo task = iterator.next();
      // 获取所有不合法的接收者
      Set<String> illegalPhone =
          task.getReceiver().stream()
              .filter(phone -> !ReUtil.isMatch(regexExp, phone))
              .collect(Collectors.toSet());
      // 若有不合法的接受者，则去除
      if (CollUtil.isNotEmpty(illegalPhone)) {
        task.getReceiver().removeAll(illegalPhone);
        log.error(
            "messageTemplateId:{} find illegal receiver!{}",
            task.getMessageTemplateId(),
            JSON.toJSONString(illegalPhone));
      }

      if (CollUtil.isEmpty(task.getReceiver())) {
        iterator.remove();
      }
    }
  }
}
