package com.java3y.austin.handler.messageconsume.messagehandle.deduplication.limit;

import com.java3y.austin.common.domain.MessageInfo;
import com.java3y.austin.handler.messageconsume.messagehandle.deduplication.DeduplicationParam;
import com.java3y.austin.handler.messageconsume.messagehandle.deduplication.service.AbstractDeduplicationService;

import java.util.Set;

/**
 * @author cao
 * @date 2022-04-20 11:58
 */
public interface LimitService {


    /**
     * 去重限制
     *
     * @param service  去重器对象
     * @param messageInfo
     * @param param    去重参数
     * @return 返回不符合条件的手机号码
     */
    Set<String> limitFilter(AbstractDeduplicationService service, MessageInfo messageInfo, DeduplicationParam param);

}
