package com.java3y.austin.handler.messageconsume.messagehandle.handlestream;

import com.dtp.core.thread.DtpExecutor;
import com.java3y.austin.handler.config.threadpool.HandlerThreadPoolConfig;
import com.java3y.austin.handler.utils.GroupIdMappingUtils;
import com.java3y.austin.support.config.threadpool.ThreadPoolUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;


/**
 * 存储 每种消息类型(也即每个消费者组） 与 专门用于处理该消费者组的消息的线程池 的关系
 * 也即给每一个消费者组配置一个专门的线程池来处理它的消息，实现异步处理，提高效率！
 * @author 3y
 */
@Component
public class ThreadPool4MsgHandler {

    @Autowired
    private ThreadPoolUtils threadPoolUtils;

    /**
     * 每个消费者组对应一个线程池，使用map存储
     * key：消费者组id
     * value：对应的线程池，用于处理该消费者组的消息！
     */
    private Map<String, ExecutorService> threadPool4MsgHandleMap = new HashMap<>(32);

    /**
     * 获取得到所有的groupId
     */
    private static List<String> allGroupIds = GroupIdMappingUtils.getAllGroupIds();

    /**
     * 使用@Component+@PostConstruct初始化！
     * 给每个渠道，每种消息类型初始化一个线程池：异步处理~
     *
     */
    @PostConstruct
    public void init() {
        /**
         * example ThreadPoolName:austin.im.notice
         *
         * 可以通过apollo配置：dynamic-tp-apollo-dtp.yml  动态修改线程池的信息
         */
        for (String groupId : allGroupIds) {
            //获取一个线程池
            DtpExecutor executor = HandlerThreadPoolConfig.getExecutor(groupId);
            /**
             * 1. 将当前线程池 加入到 动态线程池内
             * 2. 注册 线程池 被Spring管理，优雅关闭
             */
            threadPoolUtils.register(executor);
            threadPool4MsgHandleMap.put(groupId, executor);
        }
    }

    /**
     * 得到当前消费者组对应的线程池，用于处理当前消费者组的消息
     *
     * @param groupId
     * @return
     */
    public ExecutorService getThreadPoolByGroupId(String groupId) {
        return threadPool4MsgHandleMap.get(groupId);
    }


}
