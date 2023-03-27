package com.java3y.austin.handler.messageconsume.consume.impl;

import cn.hutool.core.collection.CollUtil;
import com.java3y.austin.common.domain.AnchorInfo;
import com.java3y.austin.common.domain.LogParam;
import com.java3y.austin.common.domain.MessageInfo;
import com.java3y.austin.common.enums.AnchorState;
import com.java3y.austin.handler.messageconsume.messagehandle.handlestream.MessageHandleStreamThreadTask;
import com.java3y.austin.handler.messageconsume.messagehandle.handlestream.ThreadPool4MsgHandler;
import com.java3y.austin.handler.messageconsume.sendhandler.HandlerHolder;
import com.java3y.austin.handler.messageconsume.consume.ConsumeService;
import com.java3y.austin.handler.utils.GroupIdMappingUtils;
import com.java3y.austin.support.domain.MessageTemplate;
import com.java3y.austin.support.utils.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 消费消息实现类
 * 即：各费者组从mq中拿到自己关心的消息后再干什么？
 *
 * @author 3y
 */
@Service
public class ConsumeServiceImpl implements ConsumeService {
  private static final String LOG_BIZ_TYPE = "Receiver#consumer";
  private static final String LOG_BIZ_RECALL_TYPE = "Receiver#recall";
  @Autowired private ApplicationContext context;

  @Autowired private ThreadPool4MsgHandler threadPool4MsgHandler;

  @Autowired private LogUtils logUtils;

  @Autowired private HandlerHolder handlerHolder;

  @Override
  public void consume2Send(List<MessageInfo> messageInfoLists) {
    // 1.获取到当前消息所属的消费者组，也即确定该消息由哪个消费组进行消费！
    String topicGroupId =
        GroupIdMappingUtils.getGroupIdByTaskInfo(CollUtil.getFirst(messageInfoLists.iterator()));
    // 2.遍历各条消息进行消费
    for (MessageInfo messageInfo : messageInfoLists) {
      // 2.1先记录一下日志
      logUtils.print(
          LogParam.builder().bizType(LOG_BIZ_TYPE).object(messageInfo).build(),
          AnchorInfo.builder()
              .ids(messageInfo.getReceiver())
              .businessId(messageInfo.getBusinessId())
              .state(AnchorState.RECEIVE.getCode())
              .build());
      //2.2处理消息！异步处理
      MessageHandleStreamThreadTask task = context.getBean(MessageHandleStreamThreadTask.class).setMessageInfo(messageInfo);
      //执行线程任务！
      //谁去执行？线程池去执行，具体来看：当前是给每个消费者组都配置一个线程池进行处理！！！
      threadPool4MsgHandler.getThreadPoolByGroupId(topicGroupId).execute(task);
    }
  }

  @Override
  public void consume2recall(MessageTemplate messageTemplate) {
    logUtils.print(LogParam.builder().bizType(LOG_BIZ_RECALL_TYPE).object(messageTemplate).build());
    //获取对应的发送渠道进行消息的发送
    //handlerHolder即为发送渠道->对应的发送接口的映射关系
    //todo 那么该HandlerHolder中的map在哪儿初始化的？
    handlerHolder.route(messageTemplate.getSendChannel()).doRecall(messageTemplate);
  }
}
