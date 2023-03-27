package com.java3y.austin.handler.messageconsume.messagehandle.deduplication.build;

import com.java3y.austin.common.domain.MessageInfo;
import com.java3y.austin.common.enums.AnchorState;
import com.java3y.austin.common.enums.DeduplicationType;
import com.java3y.austin.handler.messageconsume.messagehandle.deduplication.DeduplicationParam;
import org.springframework.stereotype.Service;

import java.util.Objects;


/**
 * @author huskey
 * @date 2022/1/18
 */
@Service
public class ContentDeduplicationBuilder extends AbstractDeduplicationBuilder implements Builder {

    public ContentDeduplicationBuilder() {
        deduplicationType = DeduplicationType.CONTENT.getCode();
    }

    @Override
    public DeduplicationParam build(String deduplication, MessageInfo messageInfo) {
        DeduplicationParam deduplicationParam = getParamsFromConfig(deduplicationType, deduplication, messageInfo);
        if (Objects.isNull(deduplicationParam)) {
            return null;
        }
        deduplicationParam.setAnchorState(AnchorState.CONTENT_DEDUPLICATION);
        return deduplicationParam;

    }
}
