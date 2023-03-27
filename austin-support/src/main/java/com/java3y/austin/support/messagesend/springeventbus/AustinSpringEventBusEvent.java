package com.java3y.austin.support.messagesend.springeventbus;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 描述：spring事件中的事件体定义
 * 主要就是定义一个事件的基础信息!
 * 可以继承 ApplicationEvent
 * 也可以不继承，这里继承了！
 * @author tony
 * @date 2023/2/6 19:59
 */
@Getter
public class AustinSpringEventBusEvent extends ApplicationEvent {

    private AustinSpringEventSource austinSpringEventSource;

    public AustinSpringEventBusEvent(Object source, AustinSpringEventSource austinSpringEventSource) {
        super(source);
        this.austinSpringEventSource = austinSpringEventSource;
    }

}
