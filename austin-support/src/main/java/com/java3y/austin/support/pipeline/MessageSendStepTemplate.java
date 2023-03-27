package com.java3y.austin.support.pipeline;

import java.util.List;

/**
 * 业务执行模板（即把责任链的逻辑串起来）
 * 即封装各消息发送类型对应的步骤！！！
 * 分为：普通发送和撤回两种方式
 *
 * @author 3y
 */
public class MessageSendStepTemplate {

    private List<MessageSendStep> processList;

    public List<MessageSendStep> getProcessList() {
        return processList;
    }

    public void setProcessList(List<MessageSendStep> processList) {
        this.processList = processList;
    }
}