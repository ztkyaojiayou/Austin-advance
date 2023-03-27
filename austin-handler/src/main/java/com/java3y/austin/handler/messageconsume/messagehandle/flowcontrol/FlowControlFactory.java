package com.java3y.austin.handler.messageconsume.messagehandle.flowcontrol;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.RateLimiter;
import com.java3y.austin.common.constant.CommonConstant;
import com.java3y.austin.common.domain.MessageInfo;
import com.java3y.austin.common.enums.ChannelType;
import com.java3y.austin.handler.common.enums.RateLimitStrategy;
import com.java3y.austin.handler.messageconsume.messagehandle.flowcontrol.annotations.LocalRateLimit;
import com.java3y.austin.support.config.configfetch.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 3y
 * @date 2022/4/18
 */
@Service
@Slf4j
public class FlowControlFactory implements ApplicationContextAware {

    private static final String FLOW_CONTROL_KEY = "flowControlRule";
    private static final String FLOW_CONTROL_PREFIX = "flow_control_";

    /**
     * 存储限流策略类型和其对应的具体限流策略map
     */
    private final Map<RateLimitStrategy, FlowControlService> flowControlServiceMap = new ConcurrentHashMap<>();

    @Autowired
    private ConfigService config;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 初始化
     */
    @PostConstruct
    private void init() {
        //getBeansWithAnnotation():获取ioc容器中使用了某个注解的所有bean!!!
        //key:bean名称，value：对应的对象（这里即为限流接口FlowControlService的两个具体实现类对象）
        Map<String, Object> serviceMap = this.applicationContext.getBeansWithAnnotation(LocalRateLimit.class);
        serviceMap.forEach((name, service) -> {
            if (service instanceof FlowControlService) {
                //获取该类上的该注解
                LocalRateLimit localRateLimit = AopUtils.getTargetClass(service).getAnnotation(LocalRateLimit.class);
                //拿到该注解上设置的参数信息
                RateLimitStrategy rateLimitStrategy = localRateLimit.rateLimitStrategy();
                //通常情况下 实现的限流service与rateLimitStrategy一一对应
                flowControlServiceMap.put(rateLimitStrategy, (FlowControlService) service);
            }
        });
    }

    public void flowControl(MessageInfo messageInfo, FlowControlParam flowControlParam) {
        RateLimiter rateLimiter;
        Double rateInitValue = flowControlParam.getRateInitValue();
        // 对比 初始限流值 与 配置限流值，以 配置中心的限流值为准
        Double rateLimitConfig = getRateLimitConfig(messageInfo.getSendChannel());
        if (Objects.nonNull(rateLimitConfig) && !rateInitValue.equals(rateLimitConfig)) {
            rateLimiter = RateLimiter.create(rateLimitConfig);
            flowControlParam.setRateInitValue(rateLimitConfig);
            flowControlParam.setRateLimiter(rateLimiter);
        }
        FlowControlService flowControlService = flowControlServiceMap.get(flowControlParam.getRateLimitStrategy());
        if (Objects.isNull(flowControlService)) {
            log.error("没有找到对应的单机限流策略");
            return;
        }
        double costTime = flowControlService.flowControl(messageInfo, flowControlParam);
        if (costTime > 0) {
            log.info("consumer {} flow control time {}",
                    ChannelType.getEnumByCode(messageInfo.getSendChannel()).getDescription(), costTime);
        }
    }

    /**
     * 得到限流值的配置
     * <p>
     * apollo配置样例     key：flowControl value：{"flow_control_40":1}
     * <p>
     * 渠道枚举可看：com.java3y.austin.common.enums.ChannelType
     *
     * @param channelCode
     */
    private Double getRateLimitConfig(Integer channelCode) {
        String flowControlConfig = config.getProperty(FLOW_CONTROL_KEY, CommonConstant.EMPTY_JSON_OBJECT);
        JSONObject jsonObject = JSON.parseObject(flowControlConfig);
        if (Objects.isNull(jsonObject.getDouble(FLOW_CONTROL_PREFIX + channelCode))) {
            return null;
        }
        return jsonObject.getDouble(FLOW_CONTROL_PREFIX + channelCode);
    }

}
