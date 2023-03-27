package com.java3y.austin.support.pipeline;

import com.java3y.austin.common.vo.BasicResultVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 消息发送的责任链上下文/参数--在发送消息时就要构造出该对象！！！
 * 注意：一个类上是可以定义泛型的，它是为了该类中的成员服务的！
 * @author 3y
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class MessageSendContext<T extends MessageSendModel> {
    /**
     * 标识责任链的code
     * 也即前端传入的业务执行的类型：是发送消息还是撤回消息！！！
     * send:发送消息
     * recall:撤回消息
     */
    private String code;

    /**
     * 存储责任链上下文数据的模型
     * 易知，该类就需要继承自ProcessModel类
     */
    private T messageSendModel;

    /**
     * 责任链中断的标识
     */
    private Boolean needBreak;

    /**
     * 流程处理的结果
     * 用于封装结果（这么耦合似乎不太好吧？？？）
     */
    BasicResultVO response;

}
