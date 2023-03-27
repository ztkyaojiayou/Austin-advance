package com.java3y.austin.handler.messageconsume.messagehandle.deduplication.limit;

import com.java3y.austin.common.domain.MessageInfo;
import com.java3y.austin.handler.messageconsume.messagehandle.deduplication.service.AbstractDeduplicationService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cao
 * @date 2022-04-20 12:00
 */
public abstract class AbstractLimitService implements LimitService {


    /**
     * 获取得到当前消息模板所有的去重Key
     *
     * @param messageInfo
     * @return
     */
    protected List<String> deduplicationAllKey(AbstractDeduplicationService service, MessageInfo messageInfo) {
        List<String> result = new ArrayList<>(messageInfo.getReceiver().size());
        for (String receiver : messageInfo.getReceiver()) {
            //构建去重的Key
            String key = deduplicationSingleKey(service, messageInfo, receiver);
            result.add(key);
        }
        return result;
    }


    protected String deduplicationSingleKey(AbstractDeduplicationService service, MessageInfo messageInfo, String receiver) {

        return service.deduplicationSingleKey(messageInfo, receiver);

    }
}
