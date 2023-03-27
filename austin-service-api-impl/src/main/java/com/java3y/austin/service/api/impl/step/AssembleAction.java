package com.java3y.austin.service.api.impl.step;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Throwables;
import com.java3y.austin.common.constant.CommonConstant;
import com.java3y.austin.common.domain.MessageInfo;
import com.java3y.austin.common.dto.model.ContentModel;
import com.java3y.austin.common.enums.ChannelType;
import com.java3y.austin.common.enums.RespStatusEnum;
import com.java3y.austin.common.vo.BasicResultVO;
import com.java3y.austin.service.api.domain.MessageSendParam;
import com.java3y.austin.service.api.enums.BusinessCode;
import com.java3y.austin.service.api.impl.domain.MessageSendParamInfo;
import com.java3y.austin.support.dao.MessageTemplateDao;
import com.java3y.austin.support.domain.MessageTemplate;
import com.java3y.austin.support.pipeline.MessageSendStep;
import com.java3y.austin.support.pipeline.MessageSendContext;
import com.java3y.austin.support.utils.ContentHolderUtil;
import com.java3y.austin.support.utils.TaskInfoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author 3y
 * @date 2021/11/22
 * @description 2-拼装参数
 */
@Slf4j
@Service
public class AssembleAction implements MessageSendStep<MessageSendParamInfo> {

    private static final String LINK_NAME = "url";

    @Autowired
    private MessageTemplateDao messageTemplateDao;

    @Override
    public void execute(MessageSendContext<MessageSendParamInfo> context) {
        MessageSendParamInfo messageSendParamInfo = context.getMessageSendModel();
        Long messageTemplateId = messageSendParamInfo.getMessageTemplateId();

        try {
            Optional<MessageTemplate> messageTemplate = messageTemplateDao.findById(messageTemplateId);
            if (!messageTemplate.isPresent() || messageTemplate.get().getIsDeleted().equals(CommonConstant.TRUE)) {
                context.setNeedBreak(true).setResponse(BasicResultVO.fail(RespStatusEnum.TEMPLATE_NOT_FOUND));
                return;
            }
            if (BusinessCode.COMMON_SEND.getCode().equals(context.getCode())) {
                List<MessageInfo> messageInfos = assembleTaskInfo(messageSendParamInfo, messageTemplate.get());
                messageSendParamInfo.setMessageInfo(messageInfos);
            } else if (BusinessCode.RECALL.getCode().equals(context.getCode())) {
                messageSendParamInfo.setMessageTemplate(messageTemplate.get());
            }
        } catch (Exception e) {
            context.setNeedBreak(true).setResponse(BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR));
            log.error("assemble task fail! templateId:{}, e:{}", messageTemplateId, Throwables.getStackTraceAsString(e));
        }

    }

    /**
     * 组装 TaskInfo 任务消息
     *
     * @param messageSendParamInfo
     * @param messageTemplate
     */
    private List<MessageInfo> assembleTaskInfo(MessageSendParamInfo messageSendParamInfo, MessageTemplate messageTemplate) {
        List<MessageSendParam> messageSendParamList = messageSendParamInfo.getMessageSendParamList();
        List<MessageInfo> messageInfoList = new ArrayList<>();

        for (MessageSendParam messageSendParam : messageSendParamList) {

            MessageInfo messageInfo = MessageInfo.builder()
                    .messageTemplateId(messageTemplate.getId())
                    .businessId(TaskInfoUtils.generateBusinessId(messageTemplate.getId(), messageTemplate.getTemplateType()))
                    .receiver(new HashSet<>(Arrays.asList(messageSendParam.getReceiver().split(String.valueOf(StrUtil.C_COMMA)))))
                    .idType(messageTemplate.getIdType())
                    .sendChannel(messageTemplate.getSendChannel())
                    .templateType(messageTemplate.getTemplateType())
                    .msgType(messageTemplate.getMsgType())
                    .shieldType(messageTemplate.getShieldType())
                    .sendAccount(messageTemplate.getSendAccount())
                    .contentModel(getContentModelValue(messageTemplate, messageSendParam)).build();

            messageInfoList.add(messageInfo);
        }

        return messageInfoList;

    }


    /**
     * 获取 contentModel，替换模板msgContent中占位符信息
     */
    private static ContentModel getContentModelValue(MessageTemplate messageTemplate, MessageSendParam messageSendParam) {

        // 得到真正的ContentModel 类型
        Integer sendChannel = messageTemplate.getSendChannel();
        Class<? extends ContentModel> contentModelClass = ChannelType.getChanelModelClassByCode(sendChannel);

        // 得到模板的 msgContent 和 入参
        Map<String, String> variables = messageSendParam.getVariables();
        JSONObject jsonObject = JSON.parseObject(messageTemplate.getMsgContent());


        // 通过反射 组装出 contentModel
        Field[] fields = ReflectUtil.getFields(contentModelClass);
        ContentModel contentModel = ReflectUtil.newInstance(contentModelClass);
        for (Field field : fields) {
            String originValue = jsonObject.getString(field.getName());

            if (StrUtil.isNotBlank(originValue)) {
                String resultValue = ContentHolderUtil.replacePlaceHolder(originValue, variables);
                Object resultObj = JSONUtil.isJsonObj(resultValue) ? JSONUtil.toBean(resultValue, field.getType()) : resultValue;
                ReflectUtil.setFieldValue(contentModel, field, resultObj);
            }
        }

        // 如果 url 字段存在，则在url拼接对应的埋点参数
        //获取对象中的指定字段名的值
        String url = (String) ReflectUtil.getFieldValue(contentModel, LINK_NAME);
        if (StrUtil.isNotBlank(url)) {
            //生成带业务字段的url，这也称之为埋点参数
            String resultUrl = TaskInfoUtils.generateUrl(url, messageTemplate.getId(), messageTemplate.getTemplateType());
            //设置对象中的指定字段名的值
            ReflectUtil.setFieldValue(contentModel, LINK_NAME, resultUrl);
        }
        return contentModel;
    }
}
