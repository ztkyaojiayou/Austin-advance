package com.java3y.austin.service.api.impl.domain;

import com.java3y.austin.common.domain.MessageInfo;
import com.java3y.austin.service.api.domain.MessageSendParam;
import com.java3y.austin.support.domain.MessageTemplate;
import com.java3y.austin.support.pipeline.MessageSendModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 就是用于封装前端的参数
 * 且实际使用到的属性就是前两个，也即消息模板Id和请求参数
 * @author 3y
 * @date 2021/11/22
 * @description 发送消息任务的模型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageSendParamInfo implements MessageSendModel {

    /**
     * 消息模板Id
     */
    private Long messageTemplateId;

    /**
     * 请求参数
     * 虽然定义为list，但一般前端就传一个元素！
     */
    private List<MessageSendParam> messageSendParamList;

    /**
     * 发送任务的信息
     */
    private List<MessageInfo> messageInfo;

    /**
     * 撤回任务的信息
     */
    private MessageTemplate messageTemplate;

}
