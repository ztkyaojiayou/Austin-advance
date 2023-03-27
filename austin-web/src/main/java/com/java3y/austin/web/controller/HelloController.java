package com.java3y.austin.web.controller;


import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zoutongkun
 * @description: TODO
 * @date 2023/03/-04 14:10
 */
// @ApiIgnore
@Api(tags = "Hello接口")
@RestController
public class HelloController {
  //    @RequestMapping("/hello")
  // 不管是否使用value，斜杠都可加可不加（但加了也不冲突），spring会默认加上
  @GetMapping(value = "/hello")
  public String test01() {
    Date now = new Date();
    DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String nowTime = sdf.format(now);
    //    //构造异常
    //    int i = 10/0;
    //    if (StringUtils.isNotEmpty(nowTime)) {
    //      throw new BusinessException(BusinessExceptionType.COMMON_SERVER_ERROR);
    //    }
    return "你好呀，现在是北京时间：" + nowTime;
  }

  @GetMapping("hello02")
  public String test02() {

    return "hell,world-testHello02";
  }

  public static void main(String[] args) {
    // 测试labmda表达式中局部变量的定义
    //    AtomicInteger index = new AtomicInteger();
    //    ArrayList<Integer> list = Lists.newArrayList(1, 2, 3, 4, 5);
    //    list.stream()
    //        .map(
    //            e -> {
    //              index.getAndIncrement();
    //              String str = String.valueOf(index);
    //              System.out.println("value" + str);
    //              return e + 1;
    //            })
    //        .collect(Collectors.toList());

    //      Date d=new Date();
    //      System.out.println("Hour of the day is :"+d.getHours());
    //

    //    for (int i =0;i<6;i++){
    ////      StationInfoDBO stationInfoDBO = new StationInfoDBO();
    //      StationInfoDBO stationInfoDBO = (StationInfoDBO)
    // MockDataUtil.mockObject(StationInfoDBO.class);
    //      list.add(stationInfoDBO);
    //    }
    //    System.out.println(list);
  }
}
