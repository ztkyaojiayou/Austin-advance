package com.java3y.austin.web.config.response;

import com.java3y.austin.common.vo.BasicResultVO;
import com.java3y.austin.web.config.annotation.AustinResult;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Objects;

/**
 * 关于@ControllerAdvice 注解：本质上是一个Component，因此也会被当成组建扫描，一视同仁，扫扫扫！
 * 这个类是为那些声明了（@ExceptionHandler、@InitBinder 或 @ModelAttribute注解修饰的）方法的类而提供的专业化的@Component ,
 * 以供多个Controller类所共享。
 *
 * <p>说白了，就是aop思想的一种实现，你告诉我需要拦截规则，我帮你把他们拦下来，
 * 具体你想做更细致的拦截筛选和拦截之后的处理，你自己通过@ExceptionHandler、@InitBinder 或 @ModelAttribute这三个注解以及被其注解的方法来自定义。
 *
 * <p>说明:
 *
 * <p>supports方法: 判断是否要执行beforeBodyWrite方法,true为执行,false不执行. 通过该方法可以选择哪些类或那些方法的response要进行处理,
 * 其他的不进行处理.
 * beforeBodyWrite方法: 对response方法进行具体操作处理
 *
 * @author kl
 * @version 1.0.0
 * @description 统一返回结构
 * @date 2023/2/9 19:00
 */
@ControllerAdvice(basePackages = "com.java3y.austin.web.controller")
public class AustinResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private static final String RETURN_CLASS = "BasicResultVO";

    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return methodParameter.getContainingClass().isAnnotationPresent(AustinResult.class) || methodParameter.hasMethodAnnotation(AustinResult.class);
    }

    /**
     * 返回 true时 则下面 beforeBodyWrite方法被调用, 否则就不调用下述方法
     * @param data
     * @param methodParameter
     * @param mediaType
     * @param aClass
     * @param serverHttpRequest
     * @param serverHttpResponse
     * @return
     */
    @Override
    public Object beforeBodyWrite(Object data, MethodParameter methodParameter, MediaType mediaType, Class aClass,
                                  ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if (Objects.nonNull(data) && Objects.nonNull(data.getClass())) {
            String simpleName = data.getClass().getSimpleName();
            if (RETURN_CLASS.equalsIgnoreCase(simpleName)) {
                return data;
            }
        }
        return BasicResultVO.success(data);
    }
}
