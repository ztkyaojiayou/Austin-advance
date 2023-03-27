package com.java3y.austin.handler.messageconsume.messagehandle.deduplication.build;

import com.alibaba.fastjson.JSONObject;
import com.java3y.austin.common.domain.MessageInfo;
import com.java3y.austin.handler.messageconsume.messagehandle.deduplication.DeduplicationHolder;
import com.java3y.austin.handler.messageconsume.messagehandle.deduplication.DeduplicationParam;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Objects;

/**
 * @author 3y
 * @date 2022/1/19
 */
public abstract class AbstractDeduplicationBuilder implements Builder {

    protected Integer deduplicationType;

    @Autowired
    private DeduplicationHolder deduplicationHolder;

    @PostConstruct
    public void init() {
        deduplicationHolder.putBuilder(deduplicationType, this);
    }

    public DeduplicationParam getParamsFromConfig(Integer key, String duplicationConfig, MessageInfo messageInfo) {
        JSONObject object = JSONObject.parseObject(duplicationConfig);
        if (Objects.isNull(object)) {
            return null;
        }
        DeduplicationParam deduplicationParam = JSONObject.parseObject(object.getString(DEDUPLICATION_CONFIG_PRE + key), DeduplicationParam.class);
        if (Objects.isNull(deduplicationParam)) {
            return null;
        }
        deduplicationParam.setMessageInfo(messageInfo);
        return deduplicationParam;
    }

}
