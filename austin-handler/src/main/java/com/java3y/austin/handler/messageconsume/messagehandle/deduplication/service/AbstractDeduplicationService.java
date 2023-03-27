package com.java3y.austin.handler.messageconsume.messagehandle.deduplication.service;

import cn.hutool.core.collection.CollUtil;
import com.java3y.austin.common.domain.AnchorInfo;
import com.java3y.austin.common.domain.MessageInfo;
import com.java3y.austin.handler.messageconsume.messagehandle.deduplication.DeduplicationHolder;
import com.java3y.austin.handler.messageconsume.messagehandle.deduplication.DeduplicationParam;
import com.java3y.austin.handler.messageconsume.messagehandle.deduplication.limit.LimitService;
import com.java3y.austin.support.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Set;

/**
 * @author 3y
 * @date 2021/12/9
 * 去重服务
 */
@Slf4j
public abstract class AbstractDeduplicationService implements DeduplicationService {

    protected Integer deduplicationType;

    protected LimitService limitService;

    @Autowired
    private DeduplicationHolder deduplicationHolder;

    @PostConstruct
    private void init() {
        deduplicationHolder.putService(deduplicationType, this);
    }

    @Autowired
    private LogUtils logUtils;


    @Override
    public void deduplication(DeduplicationParam param) {
        MessageInfo messageInfo = param.getMessageInfo();

        Set<String> filterReceiver = limitService.limitFilter(this, messageInfo, param);

        // 剔除符合去重条件的用户
        if (CollUtil.isNotEmpty(filterReceiver)) {
            messageInfo.getReceiver().removeAll(filterReceiver);
            logUtils.print(AnchorInfo.builder().businessId(messageInfo.getBusinessId()).ids(filterReceiver).state(param.getAnchorState().getCode()).build());
        }
    }


    /**
     * 构建去重的Key
     *
     * @param messageInfo
     * @param receiver
     * @return
     */
    public abstract String deduplicationSingleKey(MessageInfo messageInfo, String receiver);


}
