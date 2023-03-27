package com.java3y.austin.handler.messageconsume.sendhandler.alipay;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import com.java3y.austin.common.domain.MessageInfo;
import com.java3y.austin.common.dto.model.AlipayMiniProgramContentModel;
import com.java3y.austin.common.enums.ChannelType;
import com.java3y.austin.handler.common.domain.alipay.AlipayMiniProgramParam;
import com.java3y.austin.handler.messageconsume.sendhandler.BaseMsgSendChannelHandler;
import com.java3y.austin.handler.messageconsume.sendhandler.MsgSendChannelHandler;
import com.java3y.austin.support.domain.MessageTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author jwq
 * 支付宝小程序发送订阅消息
 */
@Component
@Slf4j
public class AlipayMiniProgramAccountMsgSendChannelHandler extends BaseMsgSendChannelHandler implements MsgSendChannelHandler {

    @Autowired
    private AlipayMiniProgramAccountService alipayMiniProgramAccountService;

    public AlipayMiniProgramAccountMsgSendChannelHandler() {
        channelCode = ChannelType.ALIPAY_MINI_PROGRAM.getCode();
    }

    @Override
    public boolean channel4MsgHandler(MessageInfo messageInfo) {
        AlipayMiniProgramParam miniProgramParam = buildMiniProgramParam(messageInfo);
        try {
            //发送消息
            alipayMiniProgramAccountService.send(miniProgramParam);
        } catch (Exception e) {
            log.error("AlipayMiniProgramAccountHandler#handler fail:{},params:{}",
                    Throwables.getStackTraceAsString(e), JSON.toJSONString(messageInfo));
            return false;
        }
        return true;
    }

    /**
     * 通过taskInfo构建小程序订阅消息
     *
     * @param messageInfo 任务信息
     * @return AlipayMiniProgramParam
     */
    private AlipayMiniProgramParam buildMiniProgramParam(MessageInfo messageInfo) {
        AlipayMiniProgramParam param = AlipayMiniProgramParam.builder()
                .toUserId(messageInfo.getReceiver())
                .messageTemplateId(messageInfo.getMessageTemplateId())
                .sendAccount(messageInfo.getSendAccount())
                .build();

        AlipayMiniProgramContentModel contentModel = (AlipayMiniProgramContentModel) messageInfo.getContentModel();
        param.setData(contentModel.getMap());
        return param;
    }

    @Override
    public void doRecall(MessageTemplate messageTemplate) {

    }
}
