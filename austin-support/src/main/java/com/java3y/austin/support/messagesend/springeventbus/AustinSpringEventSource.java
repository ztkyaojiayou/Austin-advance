package com.java3y.austin.support.messagesend.springeventbus;

import lombok.Builder;
import lombok.Data;

/**
 * 消息/事件定义
 * @author 3y
 */
@Data
@Builder
public class AustinSpringEventSource {
    public String topic;
    public String jsonValue;
    public String tagId;
}
