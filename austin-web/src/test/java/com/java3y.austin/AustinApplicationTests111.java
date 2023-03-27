package com.java3y.austin;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@SpringBootTest
@Slf4j
class AustinApplicationTests111 {

  @Autowired RedisTemplate redisTemplate;
  // 测试HttpServletRequest和HttpServletResponse
  @Autowired protected HttpServletRequest request;
  @Autowired protected HttpServletResponse response;
  @Autowired protected HttpSession session;

  // 继承了BaseMapper，所有的方法都来自己父类

  @Test
  void contextLoads() {}

  /** redis测试--通过 */
  @Test
  void test03() {
    redisTemplate.opsForValue().set("test01", "01");
    System.out.println(redisTemplate.opsForValue().get("test0111"));
  }

  @Test
  void test07() {}
}
