package com.java3y.austin.cron.xxl.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import com.java3y.austin.common.enums.RespStatusEnum;
import com.java3y.austin.common.vo.BasicResultVO;
import com.java3y.austin.cron.xxl.constants.XxlJobConstant;
import com.java3y.austin.cron.xxl.entity.XxlJobGroup;
import com.java3y.austin.cron.xxl.entity.XxlJobInfo;
import com.java3y.austin.cron.xxl.service.CronTaskService;
import com.xxl.job.core.biz.model.ReturnT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 定时任务管理 思路：主要就是通过http请求的方式请求特点的url来实现定时任务的crud以及启动暂停等操作！！！
 *
 * <p>背景：xxl-job提供了图形化界面添加任务，所有针对业务系统如果是固定时间需要调度任务时，则可以用通过这种图形化界面方式添加，
 * 但是：有些业务系统需要根据自己的业务去创建任务，这样图形化的添加方式就不满足使用了， 此时就需要提供对外接口供业务系统调用，例如新增，编辑，删除，挂起。
 * 参考链接：https://blog.csdn.net/m0_37527542/article/details/104468785
 *
 * @author 3y
 */
@Slf4j
@Service
public class CronTaskServiceImpl implements CronTaskService {

  @Value("${xxl.job.admin.username}")
  private String xxlUserName;

  @Value("${xxl.job.admin.password}")
  private String xxlPassword;

  @Value("${xxl.job.admin.addresses}")
  private String xxlAddresses;

  @Override
  public BasicResultVO addOrUpdateCronTask(XxlJobInfo xxlJobInfo) {
    // 1.构建参数
    Map<String, Object> params = JSON.parseObject(JSON.toJSONString(xxlJobInfo), Map.class);
    // 2.构建保存定时任务的url
    String path =
        Objects.isNull(xxlJobInfo.getId())
            ? xxlAddresses + XxlJobConstant.INSERT_URL
            : xxlAddresses + XxlJobConstant.UPDATE_URL;

    HttpResponse response;
    ReturnT returnT = null;
    try {
      // 3.发送保存定时任务的url，执行保存操作，获取response，下同！
      response = HttpRequest.post(path).form(params).cookie(getCookie()).execute();
      // 3.1拿到xxl-job相关的响应结果，用于后续的业务判断
      returnT = JSON.parseObject(response.body(), ReturnT.class);

      // 3.2插入操作时需要返回Id，而更新操作则不需要
      if (response.isOk() && ReturnT.SUCCESS_CODE == returnT.getCode()) {
        if (path.contains(XxlJobConstant.INSERT_URL)) {
          Integer taskId = Integer.parseInt(String.valueOf(returnT.getContent()));
          return BasicResultVO.success(taskId);
        } else if (path.contains(XxlJobConstant.UPDATE_URL)) {
          return BasicResultVO.success();
        }
      }
    } catch (Exception e) {
      log.error(
          "CronTaskService#saveTask fail,e:{},param:{},response:{}",
          Throwables.getStackTraceAsString(e),
          JSON.toJSONString(xxlJobInfo),
          JSON.toJSONString(returnT));
    }
    return BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR, JSON.toJSONString(returnT));
  }

  @Override
  public BasicResultVO deleteCronTask(Integer taskId) {
    // url构建
    String path = xxlAddresses + XxlJobConstant.DELETE_URL;
    // 参数构建
    HashMap<String, Object> params = MapUtil.newHashMap();
    params.put("id", taskId);

    HttpResponse response;
    ReturnT returnT = null;
    try {
      // 执行，获取响应
      response = HttpRequest.post(path).form(params).cookie(getCookie()).execute();
      returnT = JSON.parseObject(response.body(), ReturnT.class);
      if (response.isOk() && ReturnT.SUCCESS_CODE == returnT.getCode()) {
        return BasicResultVO.success();
      }
    } catch (Exception e) {
      log.error(
          "CronTaskService#deleteCronTask fail,e:{},param:{},response:{}",
          Throwables.getStackTraceAsString(e),
          JSON.toJSONString(params),
          JSON.toJSONString(returnT));
    }
    return BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR, JSON.toJSONString(returnT));
  }

  @Override
  public BasicResultVO startCronTask(Integer taskId) {
    String path = xxlAddresses + XxlJobConstant.RUN_URL;

    HashMap<String, Object> params = MapUtil.newHashMap();
    params.put("id", taskId);

    HttpResponse response;
    ReturnT returnT = null;
    try {
      response = HttpRequest.post(path).form(params).cookie(getCookie()).execute();
      returnT = JSON.parseObject(response.body(), ReturnT.class);
      if (response.isOk() && ReturnT.SUCCESS_CODE == returnT.getCode()) {
        return BasicResultVO.success();
      }
    } catch (Exception e) {
      log.error(
          "CronTaskService#startCronTask fail,e:{},param:{},response:{}",
          Throwables.getStackTraceAsString(e),
          JSON.toJSONString(params),
          JSON.toJSONString(returnT));
    }
    return BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR, JSON.toJSONString(returnT));
  }

  @Override
  public BasicResultVO stopCronTask(Integer taskId) {
    String path = xxlAddresses + XxlJobConstant.STOP_URL;

    HashMap<String, Object> params = MapUtil.newHashMap();
    params.put("id", taskId);

    HttpResponse response;
    ReturnT returnT = null;
    try {
      response = HttpRequest.post(path).form(params).cookie(getCookie()).execute();
      returnT = JSON.parseObject(response.body(), ReturnT.class);
      if (response.isOk() && ReturnT.SUCCESS_CODE == returnT.getCode()) {
        return BasicResultVO.success();
      }
    } catch (Exception e) {
      log.error(
          "CronTaskService#stopCronTask fail,e:{},param:{},response:{}",
          Throwables.getStackTraceAsString(e),
          JSON.toJSONString(params),
          JSON.toJSONString(returnT));
    }
    return BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR, JSON.toJSONString(returnT));
  }

  @Override
  public BasicResultVO getGroupId(String appName, String title) {
    String path = xxlAddresses + XxlJobConstant.JOB_GROUP_PAGE_LIST;

    HashMap<String, Object> params = MapUtil.newHashMap();
    params.put("appname", appName);
    params.put("title", title);

    HttpResponse response = null;
    try {
      response = HttpRequest.post(path).form(params).cookie(getCookie()).execute();
      Integer id =
          JSON.parseObject(response.body()).getJSONArray("data").getJSONObject(0).getInteger("id");
      if (response.isOk() && Objects.nonNull(id)) {
        return BasicResultVO.success(id);
      }
    } catch (Exception e) {
      log.error(
          "CronTaskService#getGroupId fail,e:{},param:{},response:{}",
          Throwables.getStackTraceAsString(e),
          JSON.toJSONString(params),
          JSON.toJSONString(response.body()));
    }
    return BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR, JSON.toJSONString(response.body()));
  }

  @Override
  public BasicResultVO createGroup(XxlJobGroup xxlJobGroup) {
    Map<String, Object> params = JSON.parseObject(JSON.toJSONString(xxlJobGroup), Map.class);
    String path = xxlAddresses + XxlJobConstant.JOB_GROUP_INSERT_URL;

    HttpResponse response;
    ReturnT returnT = null;

    try {
      response = HttpRequest.post(path).form(params).cookie(getCookie()).execute();
      returnT = JSON.parseObject(response.body(), ReturnT.class);
      if (response.isOk() && ReturnT.SUCCESS_CODE == returnT.getCode()) {
        return BasicResultVO.success();
      }
    } catch (Exception e) {
      log.error(
          "CronTaskService#createGroup fail,e:{},param:{},response:{}",
          Throwables.getStackTraceAsString(e),
          JSON.toJSONString(params),
          JSON.toJSONString(returnT));
    }
    return BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR, JSON.toJSONString(returnT));
  }

  /**
   * 获取xxl cookie 思路：通过登录xxl-job获取
   *
   * @return String
   */
  private String getCookie() {
    // 登录xxl-job所需参数
    Map<String, Object> params = MapUtil.newHashMap();
    params.put("userName", xxlUserName);
    params.put("password", xxlPassword);
    params.put("randomCode", IdUtil.fastSimpleUUID());
    // 登录url
    String path = xxlAddresses + XxlJobConstant.LOGIN_URL;
    HttpResponse response = null;
    try {
      // 执行：登录xxl-job
      response = HttpRequest.post(path).form(params).execute();
      if (response.isOk()) {
        // 拿到cookie！
        List<HttpCookie> cookies = response.getCookies();
        StringBuilder sb = new StringBuilder();
        for (HttpCookie cookie : cookies) {
          sb.append(cookie.toString());
        }
        return sb.toString();
      }
    } catch (Exception e) {
      log.error(
          "CronTaskService#createGroup getCookie,e:{},param:{},response:{}",
          Throwables.getStackTraceAsString(e),
          JSON.toJSONString(params),
          JSON.toJSONString(response));
    }
    return null;
  }
}
