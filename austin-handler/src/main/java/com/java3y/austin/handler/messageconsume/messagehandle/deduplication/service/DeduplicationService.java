package com.java3y.austin.handler.messageconsume.messagehandle.deduplication.service;


import com.java3y.austin.handler.messageconsume.messagehandle.deduplication.DeduplicationParam;

/**
 * @author huskey
 * @date 2022/1/18
 */
public interface DeduplicationService {

    /**
     * 去重
     *
     * @param param
     */
    void deduplication(DeduplicationParam param);
}
