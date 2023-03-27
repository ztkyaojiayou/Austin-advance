package test;

import com.java3y.austin.AustinApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 关于测试类：
 * 该类的位置：需要放在test/java下，此时分为两种情况：
 * 1）若和启动类所在的包名一致，则此时无需使用classes来指定启动类，springboot会自动去找对应的启动类了运行
 * 2）而若在其他位置，则必须指定启动类，否则会因为找不到启动类而报错！
 * 3）对于测试类的类名，没有要求，只是一般都命名为“启动类名+Tests”而已！！！
 */
@SpringBootTest(classes = AustinApplication.class)
@Slf4j
class AustinApplicationTests112 {

  @Autowired RedisTemplate redisTemplate;
  // 测试HttpServletRequest和HttpServletResponse
  @Autowired protected HttpServletRequest request;
  @Autowired protected HttpServletResponse response;
  @Autowired protected HttpSession session;


  @Test
  void contextLoads() {}

  /** redis测试--通过 */
  @Test
  void test03() {
    redisTemplate.opsForValue().set("test01", "01");
    System.out.println(redisTemplate.opsForValue().get("test011"));
  }

  @Test
  void test07() {}
}
