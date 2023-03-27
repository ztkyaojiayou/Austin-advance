package com.java3y.austin.handler.messageconsume.messagehandle.shield.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.java3y.austin.common.domain.AnchorInfo;
import com.java3y.austin.common.domain.MessageInfo;
import com.java3y.austin.common.enums.AnchorState;
import com.java3y.austin.common.enums.ShieldType;
import com.java3y.austin.handler.messageconsume.messagehandle.shield.ShieldService;
import com.java3y.austin.support.utils.LogUtils;
import com.java3y.austin.support.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;

/**
 * 屏蔽服务 指的是夜间是否屏蔽以及对应的处理逻辑
 *
 * @author 3y
 */
@Service
@Slf4j
public class ShieldServiceImpl implements ShieldService {

  private static final String NIGHT_SHIELD_BUT_NEXT_DAY_SEND_KEY = "night_shield_send";
  @Autowired private RedisUtils redisUtils;
  @Autowired private LogUtils logUtils;

  @Override
  public void shield(MessageInfo messageInfo) {
    // 1.夜间不屏蔽类型
    if (ShieldType.NIGHT_NO_SHIELD.getCode().equals(messageInfo.getShieldType())) {
      return;
    }

    /** example:当消息下发至austin平台时，已经是凌晨1点，业务希望此类消息在次日的早上9点推送 (配合 分布式任务定时任务框架搞定) */
    if (isNight()) {
      // 2.夜间屏蔽类型，记录一下日志，便于后续追踪！！！
      if (ShieldType.NIGHT_SHIELD.getCode().equals(messageInfo.getShieldType())) {
        logUtils.print(
            AnchorInfo.builder()
                .state(AnchorState.NIGHT_SHIELD.getCode())
                .businessId(messageInfo.getBusinessId())
                .ids(messageInfo.getReceiver())
                .build());
      }
      // 3.夜间屏蔽，但次日早上9点发送类型，由于还是需要发送，因此这些消息需要存起来，这里存在了redis！
      // 之后使用定时任务获取这些消息进行发送即可！！！
      if (ShieldType.NIGHT_SHIELD_BUT_NEXT_DAY_SEND.getCode().equals(messageInfo.getShieldType())) {
        // 使用list存储，相当于队列！
        redisUtils.lPush(
            NIGHT_SHIELD_BUT_NEXT_DAY_SEND_KEY,
            JSON.toJSONString(messageInfo, SerializerFeature.WriteClassName),
            // 保存1天
            (DateUtil.offsetDay(new Date(), 1).getTime() / 1000) - DateUtil.currentSeconds());
        // 同时也记录一下日志，便于后续追踪！！！
        logUtils.print(
            AnchorInfo.builder()
                .state(AnchorState.NIGHT_SHIELD_NEXT_SEND.getCode())
                .businessId(messageInfo.getBusinessId())
                .ids(messageInfo.getReceiver())
                .build());
      }
      //设置接收者？
      messageInfo.setReceiver(new HashSet<>());
    }
  }

  /**
   * 小时 < 8 默认就认为是凌晨(夜晚)
   *
   * @return
   */
  private boolean isNight() {
    return LocalDateTime.now().getHour() < 8;
  }
}
