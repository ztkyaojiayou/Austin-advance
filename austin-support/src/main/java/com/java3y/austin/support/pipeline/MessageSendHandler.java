package com.java3y.austin.support.pipeline;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.java3y.austin.common.enums.RespStatusEnum;
import com.java3y.austin.common.vo.BasicResultVO;
import com.java3y.austin.support.config.exception.ProcessException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 消息发送流程控制器
 *
 * @author 3y
 */
@Slf4j
@Data
public class MessageSendHandler {

    /**
     * 模板映射--该参数会被设置好，然后会把该对象纳入ioc容器管理
     * 其中，key即为：标识责任链的code，也即发送类型，value即为：责任链的各节点，也即对应的处理步骤list
     * 具体是在PipelineConfig类中进行的初始化，
     * 这里的MessageSendStepTemplate也会先在PipelineConfig配置类中进行注入！
     *
     *
     */
    private Map<String, MessageSendStepTemplate> messageSendStepConfigMap = null;


    /**
     * 执行消息发送的责任链（非常关键的方法！！！）
     *
     * @param context
     * @return 返回上下文内容
     */
    public MessageSendContext executeMessageSendStep(MessageSendContext context) {

        /**
         * 前置检查（注意：该前置检查并非责任链中定义的前置检查！！！）
         * 这只是单纯在执行责任链之前进行一些必要的检查而已！！！
         */
        try {
            preCheck(context);
        } catch (ProcessException e) {
            return e.getProcessContext();
        }

        /**
         * 遍历各流程节点
         * 真正在执行时，templateConfig对象是有值了的！！！
         */
        //1.先获取对应的流程节点lsit
        List<MessageSendStep> processList = messageSendStepConfigMap.get(context.getCode()).getProcessList();
        //2.再挨个执行！
        for (MessageSendStep messageSendStep : processList) {
            messageSendStep.execute(context);
            if (context.getNeedBreak()) {
                break;
            }
        }
        return context;
    }


    /**
     * 执行前检查，出错则抛出异常
     *
     * @param context 执行上下文
     * @throws ProcessException 异常信息
     */
    private void preCheck(MessageSendContext context) throws ProcessException {
        // 检查上下文
        if (Objects.isNull(context)) {
            context = new MessageSendContext();
            context.setResponse(BasicResultVO.fail(RespStatusEnum.CONTEXT_IS_NULL));
            throw new ProcessException(context);
        }

        // 检查业务代码--也即前端传入的业务执行的类型code
        String businessCode = context.getCode();
        if (StrUtil.isBlank(businessCode)) {
            context.setResponse(BasicResultVO.fail(RespStatusEnum.BUSINESS_CODE_IS_NULL));
            throw new ProcessException(context);
        }

        // 检查执行模板--也即检查是否配置了该业务类型code对应的业务执行模板
        MessageSendStepTemplate messageSendStepTemplate = messageSendStepConfigMap.get(businessCode);
        if (Objects.isNull(messageSendStepTemplate)) {
            context.setResponse(BasicResultVO.fail(RespStatusEnum.PROCESS_TEMPLATE_IS_NULL));
            throw new ProcessException(context);
        }

        // 检查执行模板列表--也即再进一步检查该业务类型code对应的业务执行模板是否为空！
        List<MessageSendStep> processList = messageSendStepTemplate.getProcessList();
        if (CollUtil.isEmpty(processList)) {
            context.setResponse(BasicResultVO.fail(RespStatusEnum.PROCESS_LIST_IS_NULL));
            throw new ProcessException(context);
        }

    }


}
