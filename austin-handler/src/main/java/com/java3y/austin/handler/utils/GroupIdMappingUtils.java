package com.java3y.austin.handler.utils;


import com.java3y.austin.common.domain.MessageInfo;
import com.java3y.austin.common.enums.ChannelType;
import com.java3y.austin.common.enums.MessageType;

import java.util.ArrayList;
import java.util.List;

/**
 * groupId 标识着每一个消费者组
 *
 * @author 3y
 */
public class GroupIdMappingUtils {

    /**
     * 获取所有的消费者组id/groupIds
     * 不同的渠道不同的消息类型拥有自己的groupId，
     * 作用是：确保不同的消息由不同的消费者组消息，做到精准消费！！！
     *
     */
    public static List<String> getAllGroupIds() {
        List<String> groupIds = new ArrayList<>();
        //发送渠道（12种）
        for (ChannelType channelType : ChannelType.values()) {
            //消息类型（3种）
            for (MessageType messageType : MessageType.values()) {
                groupIds.add(channelType.getCodeEn() + "." + messageType.getCodeEn());
            }
        }
        //易知，就会有36个消费者组进行消费
        return groupIds;
    }


    /**
     * 根据TaskInfo获取当前消息的消费者组id/groupId
     * 作用：在消费时精准消费，逻辑是：各个消费者组只消费对应的消息！！！
     * @param messageInfo
     * @return
     */
    public static String getGroupIdByTaskInfo(MessageInfo messageInfo) {
        //获取该消息所属的发送渠道
        String channelCodeEn = ChannelType.getEnumByCode(messageInfo.getSendChannel()).getCodeEn();
        //获取该消息所属的消息类型
        String msgCodeEn = MessageType.getEnumByCode(messageInfo.getMsgType()).getCodeEn();
        //生成消费者组id，由“发送渠道+消息类型”构成
        //易知会有（发送渠道个数*消息类型个数）的消费者组！
        return channelCodeEn + "." + msgCodeEn;
    }
}
