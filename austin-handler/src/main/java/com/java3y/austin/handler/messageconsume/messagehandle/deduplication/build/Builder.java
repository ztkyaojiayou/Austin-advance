package com.java3y.austin.handler.messageconsume.messagehandle.deduplication.build;

import com.java3y.austin.common.domain.MessageInfo;
import com.java3y.austin.handler.messageconsume.messagehandle.deduplication.DeduplicationParam;

/**
 * @author luohaojie
 * @date 2022/1/18
 */
public interface Builder {

    String DEDUPLICATION_CONFIG_PRE = "deduplication_";

    /**
     * 根据配置构建去重参数
     *
     * @param deduplication
     * @param messageInfo
     * @return
     */
    DeduplicationParam build(String deduplication, MessageInfo messageInfo);
}
