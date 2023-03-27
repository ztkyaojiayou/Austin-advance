package com.java3y.austin.handler.messageconsume.messagehandle.deduplication.service;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.java3y.austin.common.domain.MessageInfo;
import com.java3y.austin.common.enums.DeduplicationType;
import com.java3y.austin.handler.messageconsume.messagehandle.deduplication.limit.LimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author 3y
 * @date 2021/12/11
 * 内容去重服务（默认5分钟相同的文案发给相同的用户去重）
 */
@Service
public class ContentDeduplicationService extends AbstractDeduplicationService {


    @Autowired
    public ContentDeduplicationService(@Qualifier("SlideWindowLimitService") LimitService limitService) {
        this.limitService = limitService;
        deduplicationType = DeduplicationType.CONTENT.getCode();
    }

    /**
     * 内容去重 构建key
     * <p>
     * key: md5(templateId + receiver + content)
     * <p>
     * 相同的内容相同的模板短时间内发给同一个人
     *
     * @param messageInfo
     * @return
     */
    @Override
    public String deduplicationSingleKey(MessageInfo messageInfo, String receiver) {
        return DigestUtil.md5Hex(messageInfo.getMessageTemplateId() + receiver
                + JSON.toJSONString(messageInfo.getContentModel()));
    }
}
