package com.java3y.austin.handler.messageconsume.messagehandle.handlestream;


import cn.hutool.core.collection.CollUtil;
import com.java3y.austin.common.domain.MessageInfo;
import com.java3y.austin.handler.messageconsume.messagehandle.deduplication.DeduplicationRuleService;
import com.java3y.austin.handler.messageconsume.messagehandle.discard.DiscardMessageService;
import com.java3y.austin.handler.messageconsume.sendhandler.HandlerHolder;
import com.java3y.austin.handler.messageconsume.messagehandle.shield.ShieldService;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 消息处理的线程任务
 * 也即在处理消息时，使用线程池进行处理
 * 该线程任务就是去定义：消费一条消息时具体做了哪些操作！！！
 * 0.丢弃消息 2.屏蔽消息 2.通用去重功能 3.发送消息
 *
 * @author 3y
 */
@Data
@Accessors(chain = true)
@Slf4j
@Component
// 将该bean设置为多例
//关于Spring Bean的作用域：
// 1.singleton：默认作用域Spring
// IOC容器仅存在一个Bean实例，Bean以单例方式存在，在创建容器时就同时自动创建了一个Bean对象。作用域范围是ApplicationContext中。
// 2.prototype：每次从容器中调用Bean时，都会返回一个新的实例，即每次调用getBean时。作用域返回是getBean方法调用直至方法结束。
// 相当于执行newXxxBean().Prototype是原型类型，再我们创建容器的时候并没有实例化，而是当我们获取Bean的时候才会去创建一个对象，而且我们每次获取到的对象都不是同一个对象。
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MessageHandleStreamThreadTask implements Runnable {
    /**
     * channel->Handler的映射关系
     */
    @Autowired
    private HandlerHolder handlerHolder;

    /**
     * 消息去重
     */
    @Autowired
    private DeduplicationRuleService deduplicationRuleService;

    /**
     * 丢弃消息
     */
    @Autowired
    private DiscardMessageService discardMessageService;

    /**
     * 屏蔽消息
     */
    @Autowired
    private ShieldService shieldService;

    /**
     * 消息实体
     */
    private MessageInfo messageInfo;


    /**
     * 线程任务
     */
    @Override
    public void run() {

        // 0. 丢弃一些不需要的消息
        //逻辑是：若当前消息是需要丢弃的，那么就返回true，此时直接终止流程即可！
        if (discardMessageService.isDiscard(messageInfo)) {
            return;
        }
        // 1. 屏蔽消息
        shieldService.shield(messageInfo);

        // 2.平台通用去重（重点理解并掌握!）
        if (CollUtil.isNotEmpty(messageInfo.getReceiver())) {
            deduplicationRuleService.duplication(messageInfo);
        }

        // 3. 真正发送消息，此时才真正分发给各渠道进行发送！！！
        if (CollUtil.isNotEmpty(messageInfo.getReceiver())) {
            handlerHolder.route(messageInfo.getSendChannel()).doSend(messageInfo);
        }

    }
}
