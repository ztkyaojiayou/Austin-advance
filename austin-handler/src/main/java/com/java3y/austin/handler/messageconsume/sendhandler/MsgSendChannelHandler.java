package com.java3y.austin.handler.messageconsume.sendhandler;

import com.java3y.austin.common.domain.MessageInfo;
import com.java3y.austin.support.domain.MessageTemplate;

/**
 * @author 3y
 * 消息处理器（属于消费消息的一部分）
 * 即一个消息由谁处理，或者说由上面渠道发，比如飞书、钉钉等，目前支持11种渠道！
 */
public interface MsgSendChannelHandler {

    /**
     * 发送消息（其子类实现为：真正具体由哪个渠道发了！）
     *
     * @param messageInfo
     */
    void doSend(MessageInfo messageInfo);

    /**
     * 撤回消息
     *
     * @param messageTemplate
     * @return
     */
    void doRecall(MessageTemplate messageTemplate);

}
