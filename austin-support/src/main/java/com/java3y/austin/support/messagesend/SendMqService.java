package com.java3y.austin.support.messagesend;


/**
 * @author 3y
 * 发送数据至消息队列
 */
public interface SendMqService {
    /**
     * 发送消息--带tagId
     *
     * @param topic
     * @param jsonValue
     * @param tagId
     */
    void send(String topic, String jsonValue, String tagId);


    /**
     * 发送消息--常规发送
     * LogUtils日志消息的记录也会走该接口！
     * @param topic
     * @param jsonValue
     */
    void send(String topic, String jsonValue);

}
