package com.java3y.austin.handler.messageconsume.sendhandler.email;


import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.RateLimiter;
import com.java3y.austin.common.domain.MessageInfo;
import com.java3y.austin.common.dto.model.EmailContentModel;
import com.java3y.austin.common.enums.ChannelType;
import com.java3y.austin.handler.common.enums.RateLimitStrategy;
import com.java3y.austin.handler.messageconsume.messagehandle.flowcontrol.FlowControlParam;
import com.java3y.austin.handler.messageconsume.sendhandler.BaseMsgSendChannelHandler;
import com.java3y.austin.handler.messageconsume.sendhandler.MsgSendChannelHandler;
import com.java3y.austin.support.domain.MessageTemplate;
import com.java3y.austin.support.utils.AccountUtils;
import com.java3y.austin.support.utils.AustinFileUtils;
import com.sun.mail.util.MailSSLSocketFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Objects;

/**
 * 邮件发送处理
 *
 * @author 3y
 */
@Component
@Slf4j
public class EmailMsgSendChannelHandler extends BaseMsgSendChannelHandler implements MsgSendChannelHandler {

    @Autowired
    private AccountUtils accountUtils;

    @Value("${austin.business.upload.crowd.path}")
    private String dataPath;

    public EmailMsgSendChannelHandler() {
        channelCode = ChannelType.EMAIL.getCode();

        // 按照请求限流，默认单机 3 qps （具体数值配置在apollo动态调整)
        Double rateInitValue = Double.valueOf(3);
        flowControlParam = FlowControlParam.builder().rateInitValue(rateInitValue)
                .rateLimitStrategy(RateLimitStrategy.REQUEST_RATE_LIMIT)
                .rateLimiter(RateLimiter.create(rateInitValue)).build();

    }

    @Override
    public boolean channel4MsgHandler(MessageInfo messageInfo) {
        EmailContentModel emailContentModel = (EmailContentModel) messageInfo.getContentModel();
        MailAccount account = getAccountConfig(messageInfo.getSendAccount());
        try {
            File file = StrUtil.isNotBlank(emailContentModel.getUrl()) ? AustinFileUtils.getRemoteUrl2File(dataPath, emailContentModel.getUrl()) : null;
            String result = Objects.isNull(file) ? MailUtil.send(account, messageInfo.getReceiver(), emailContentModel.getTitle(), emailContentModel.getContent(), true) :
                    MailUtil.send(account, messageInfo.getReceiver(), emailContentModel.getTitle(), emailContentModel.getContent(), true, file);
        } catch (Exception e) {
            log.error("EmailHandler#handler fail!{},params:{}", Throwables.getStackTraceAsString(e), messageInfo);
            return false;
        }
        return true;
    }

    /**
     * 获取账号信息合配置
     *
     * @return
     */
    private MailAccount getAccountConfig(Integer sendAccount) {
        MailAccount account = accountUtils.getAccountById(sendAccount, MailAccount.class);
        try {
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            account.setAuth(account.isAuth()).setStarttlsEnable(account.isStarttlsEnable()).setSslEnable(account.isSslEnable()).setCustomProperty("mail.smtp.ssl.socketFactory", sf);
            account.setTimeout(25000).setConnectionTimeout(25000);
        } catch (Exception e) {
            log.error("EmailHandler#getAccount fail!{}", Throwables.getStackTraceAsString(e));
        }
        return account;
    }

    @Override
    public void doRecall(MessageTemplate messageTemplate) {

    }
}
