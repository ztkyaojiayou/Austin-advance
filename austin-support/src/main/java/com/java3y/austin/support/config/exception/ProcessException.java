package com.java3y.austin.support.config.exception;

import com.java3y.austin.common.enums.RespStatusEnum;
import com.java3y.austin.support.pipeline.MessageSendContext;

import java.util.Objects;

/**
 * @author SamLee
 * @since 2022-03-29
 */
public class ProcessException extends RuntimeException {

    /**
     * 流程处理上下文
     */
    private final MessageSendContext messageSendContext;

    public ProcessException(MessageSendContext messageSendContext) {
        super();
        this.messageSendContext = messageSendContext;
    }

    public ProcessException(MessageSendContext messageSendContext, Throwable cause) {
        super(cause);
        this.messageSendContext = messageSendContext;
    }

    @Override
    public String getMessage() {
        if (Objects.nonNull(this.messageSendContext)) {
            return this.messageSendContext.getResponse().getMsg();
        }
        return RespStatusEnum.CONTEXT_IS_NULL.getMsg();

    }

    public MessageSendContext getProcessContext() {
        return messageSendContext;
    }
}
