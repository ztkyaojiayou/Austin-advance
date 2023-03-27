package com.java3y.austin.handler.messageconsume.messagehandle.flowcontrol;

import com.java3y.austin.common.domain.MessageInfo;

/**
 * @author 3y
 * 流量控制服务
 */
public interface FlowControlService {


    /**
     * 根据渠道进行流量控制
     *
     * @param messageInfo
     * @param flowControlParam
     * @return 耗费的时间
     */
    Double flowControl(MessageInfo messageInfo, FlowControlParam flowControlParam);

}
