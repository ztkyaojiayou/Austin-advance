package com.java3y.austin.handler.messageconsume.messagereceiver.kafka;

import com.java3y.austin.handler.utils.GroupIdMappingUtils;
import com.java3y.austin.support.constans.MessageQueuePipeline;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListenerAnnotationBeanPostProcessor;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

/**
 * 启动消费者
 *
 * @author 3y
 * @date 2021/12/4
 */
@Service
@ConditionalOnProperty(name = "austin.mq.pipeline", havingValue = MessageQueuePipeline.KAFKA)
@Slf4j
public class ReceiverStart {

    @Autowired
    private ApplicationContext context;
    // 监听器工厂
    @Autowired
    private ConsumerFactory consumerFactory;

    /**
     * receiver的消费方法常量
     */
    private static final String RECEIVER_METHOD_NAME = "Receiver.consumer";
    /**
     * 下标(用于迭代groupIds位置)
     */
    private static Integer index = 0;

    /**
     * 1.获取所有的消费者组id/groupId
     */
    private static List<String> allGroupIds = GroupIdMappingUtils.getAllGroupIds();

    /**
     * 2.为每个渠道不同的消息类型 创建一个Receiver对象
     * 也即会创建36个消费者组！！！也即会有36个Receiver对象！！！
     * todo 没太懂？怎么就生成了36个Receiver对象？
     */
    @PostConstruct
    public void init() {
        for (int i = 0; i < allGroupIds.size(); i++) {
            // todo 这就生成了？？？
            context.getBean(Receiver.class);
        }
    }

    /**
     * 3.再给每个Receiver对象的consumer方法 @KafkaListener赋值相应的groupId
     *
     */
    @Bean
    public static KafkaListenerAnnotationBeanPostProcessor.AnnotationEnhancer groupIdEnhancer() {
        //AnnotationEnhancer是一个函数式接口，且为Function-apply型
        //因为定义了36个消费者组，因此这里的attrs即有36个
        return (attrs, element) -> {
            if (element instanceof Method) {
                //1.获取消费者对象的“类名+方法名”，且用句号拼接，易知即为"Receiver.consumer"
                String name = ((Method) element).getDeclaringClass().getSimpleName() + "." + ((Method) element).getName();
                if (RECEIVER_METHOD_NAME.equals(name)) {
                    //2.对当前消费者组设置相应的groupId
                    //具体是设置在kafka的Header中！！！
                    //之后就可以通过该字段进行获取它！
                    attrs.put("groupId", allGroupIds.get(index++));
                }
            }
            return attrs;
        };
    }

  /**
   * 自定义消息过滤器
   * 针对tag消息过滤 producer 将tag写进header里
   *
   * 关于kafka的消息过滤器：
   * 可以让消息在抵达监听容器前被拦截，过滤器根据系统业务逻辑去筛选出需要的数据交由 KafkaListener
   * 处理，不需要的消息则会过滤掉。使用消息过滤器也很简单，只要两步：
   * 1.定义一个过滤器并注入到ioc容器：也即ConcurrentKafkaListenerContainerFactory
   * 2.@KafkaListener注解中通过containerFactory属性引用即可

   * 参考链接：https://blog.csdn.net/H900302/article/details/110791942
   *
   * @return
   */
  @Bean
  public ConcurrentKafkaListenerContainerFactory filterContainerFactory(
      @Value("${austin.business.tagId.key}") String tagIdKey,
      @Value("${austin.business.tagId.value}") String tagIdValue) {
      //1.new一个消息过滤器
        ConcurrentKafkaListenerContainerFactory factory = new ConcurrentKafkaListenerContainerFactory();
        factory.setConsumerFactory(consumerFactory);
        //被过滤的消息进行丢弃
        factory.setAckDiscarded(true);
        //2.定义过滤/筛选策略
        factory.setRecordFilterStrategy(consumerRecord -> {
            if (Optional.ofNullable(consumerRecord.value()).isPresent()) {
                for (Header header : consumerRecord.headers()) {
                    //只消费自己发送的消息！！！由配置信息中的tagIdKey和tagIdValue区分！！！
                    if (header.key().equals(tagIdKey) && new String(header.value()).equals(new String(tagIdValue.getBytes(StandardCharsets.UTF_8)))) {
                        return false;
                    }
                }
            }
            //返回true将会被丢弃
            return true;
        });
        //返回该自定义过滤器，注入到ioc容器！
        return factory;
    }
}
