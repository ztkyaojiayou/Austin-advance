package com.java3y.austin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author 3y
 */
@SpringBootApplication
@Slf4j
public class AustinApplication {
  public static void main(String[] args) {

    /**
     * 如果你需要启动Apollo动态配置 1、启动apollo 2、将application.properties配置文件的 austin.apollo.enabled 改为true
     * 3、下方的property替换真实的ip和port
     */
    System.setProperty("apollo.config-service", "http://austin.apollo.config:5001");
    // 1.常规启动方式
            SpringApplication.run(AustinApplication.class, args);

    // 2.若springboot的版本在2.6.0之后，则spring默认不自动处理循环依赖的问题，此时需要自行处理，
    // 可采取如下启动方式，同时设置允许循环依赖：
    // 关于循环依赖：在2.6.0之前，spring会自动处理循环依赖的问题，
    // 2.6.0 以后的版本默认禁止 Bean 之间的循环引用，如果存在循环引用就会启动失败报错。
    // 参考链接：https://blog.csdn.net/CutelittleBo/article/details/122411294
//    SpringApplication sa = new SpringApplication(AustinApplication.class);
//    sa.setAllowCircularReferences(Boolean.TRUE);
//    sa.run(args);
    log.info("项目启动成功啦！！！！！！！！！！！！！！！！！！！！");
  }
}
