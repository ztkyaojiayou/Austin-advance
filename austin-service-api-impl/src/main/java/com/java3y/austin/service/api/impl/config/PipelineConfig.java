package com.java3y.austin.service.api.impl.config;

import com.java3y.austin.service.api.enums.BusinessCode;
import com.java3y.austin.service.api.impl.step.AfterParamCheckAction;
import com.java3y.austin.service.api.impl.step.AssembleAction;
import com.java3y.austin.service.api.impl.step.PreParamCheckAction;
import com.java3y.austin.service.api.impl.step.SendMqAction;
import com.java3y.austin.support.pipeline.MessageSendHandler;
import com.java3y.austin.support.pipeline.MessageSendStepTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * api层的pipeline配置类 即责任链流程配置，将其放入ioc容器!
 * 在web模块的SendController中的send方法时就会使用到该配置类中配置的ProcessController对象！！！
 *
 * @author 3y
 */
@Configuration
public class PipelineConfig {

  /** 1.把责任链上的各个流程对象具体实现类都注入进来 */
  @Autowired private PreParamCheckAction preParamCheckAction;

  @Autowired private AssembleAction assembleAction;
  @Autowired private AfterParamCheckAction afterParamCheckAction;
  @Autowired private SendMqAction sendMqAction;

  /**
   * 2.组装各发送类型的步骤模板--注入ioc容器
   *
   * <p>2.1普通发送执行流程（四步） 1. 前置参数校验 2. 组装参数 3. 后置参数校验 4. 发送消息至MQ
   * 默认Bean的名称就是方法的名称，不过也可以指定Bean的名称
   * @return
   */
  @Bean("commonSendTemplate")
  public MessageSendStepTemplate commonSendTemplate() {
    MessageSendStepTemplate messageSendStepTemplate = new MessageSendStepTemplate();
    messageSendStepTemplate.setProcessList(
        Arrays.asList(preParamCheckAction, assembleAction, afterParamCheckAction, sendMqAction));
    return messageSendStepTemplate;
  }

  /**
   * 2.2消息撤回执行流程(两步） 1.组装参数 2.发送MQ
   *
   * @return
   */
  @Bean("recallMessageTemplate")
  public MessageSendStepTemplate recallMessageTemplate() {
    MessageSendStepTemplate messageSendStepTemplate = new MessageSendStepTemplate();
    messageSendStepTemplate.setProcessList(Arrays.asList(assembleAction, sendMqAction));
    return messageSendStepTemplate;
  }

  /**
   * 3.把MessageSendHandler流程控制器对象加入spring管理！！！
   * 主要是初始化MessageSendHandler类中的templateConfig变量！
   * 后续扩展则加BusinessCode和ProcessTemplate
   *
   * @return
   */
  @Bean
  public MessageSendHandler processController() {
    // 构造流程控制器
    MessageSendHandler messageSendHandler = new MessageSendHandler();
    // 用于存储各流程list，其中key即为：标识责任链的code，也即发送类型，value即为：责任链的各节点，也即对应的处理步骤list
    Map<String, MessageSendStepTemplate> templateConfig = new HashMap<>(4);
    // 1.普通发送执行流程
    // todo 明明上面已经将对应的MessageSendStepTemplate对象注入了ioc容器，为什么不直接使用呢？
    //  因为：这些对象也都是在当前类中注入的，因此会形成循环依赖，即会报错！！！而若拆分为两个配置类的话就没问题！！！
    templateConfig.put(BusinessCode.COMMON_SEND.getCode(), commonSendTemplate());
    // 2.消息撤回执行流程
    templateConfig.put(BusinessCode.RECALL.getCode(), recallMessageTemplate());
    // 构建流程控制器中的模板映射map
    messageSendHandler.setMessageSendStepConfigMap(templateConfig);
    return messageSendHandler;
  }
}
