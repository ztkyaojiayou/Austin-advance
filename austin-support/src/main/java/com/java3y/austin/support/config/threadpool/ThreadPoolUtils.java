package com.java3y.austin.support.config.threadpool;

import com.dtp.core.DtpRegistry;
import com.dtp.core.thread.DtpExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 线程池工具类
 * 这里使用了注著名的动态线程池dtp！！！
 * @author 3y
 */
@Component
public class ThreadPoolUtils {

    @Autowired
    private ThreadPoolExecutorShutdownDefinition shutdownDefinition;

    private static final String SOURCE_NAME = "austin";


    /**
     * 1. 将当前线程池 加入到 动态线程池内
     * 2. 注册 线程池 被Spring管理，优雅关闭
     */
    public void register(DtpExecutor dtpExecutor) {
        //1. 将当前线程池 加入到 动态线程池内
        DtpRegistry.register(dtpExecutor, SOURCE_NAME);
        //2. 注册 线程池 被Spring管理，优雅关闭
        shutdownDefinition.registryExecutor(dtpExecutor);
    }
}
