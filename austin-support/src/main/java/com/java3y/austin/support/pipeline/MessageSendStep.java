package com.java3y.austin.support.pipeline;

/**
 * 业务执行器接口
 * 也即发送消息时的核心步骤接口
 * 也即定义责任链中各节点逻辑的方法
 * @author 3y
 */
public interface MessageSendStep<T extends MessageSendModel> {

    /**
     * 执行
     *
     * @param context
     */
    void execute(MessageSendContext<T> context);
}
