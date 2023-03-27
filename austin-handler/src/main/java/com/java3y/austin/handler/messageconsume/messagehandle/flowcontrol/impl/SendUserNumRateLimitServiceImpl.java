package com.java3y.austin.handler.messageconsume.messagehandle.flowcontrol.impl;

import com.google.common.util.concurrent.RateLimiter;
import com.java3y.austin.common.domain.MessageInfo;
import com.java3y.austin.handler.common.enums.RateLimitStrategy;
import com.java3y.austin.handler.messageconsume.messagehandle.flowcontrol.FlowControlParam;
import com.java3y.austin.handler.messageconsume.messagehandle.flowcontrol.FlowControlService;
import com.java3y.austin.handler.messageconsume.messagehandle.flowcontrol.annotations.LocalRateLimit;

/**
 * Created by TOM
 * On 2022/7/21 17:14
 *
 * @author TOM
 */
@LocalRateLimit(rateLimitStrategy = RateLimitStrategy.SEND_USER_NUM_RATE_LIMIT)
public class SendUserNumRateLimitServiceImpl implements FlowControlService {

    /**
     * 根据渠道进行流量控制
     *
     * @param messageInfo
     * @param flowControlParam
     */
    @Override
    public Double flowControl(MessageInfo messageInfo, FlowControlParam flowControlParam) {
        RateLimiter rateLimiter = flowControlParam.getRateLimiter();
        return rateLimiter.acquire(messageInfo.getReceiver().size());
    }
}
