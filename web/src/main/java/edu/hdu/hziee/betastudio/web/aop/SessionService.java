package edu.hdu.hziee.betastudio.web.aop;

import edu.hdu.hziee.betastudio.business.user.service.UserService;
import edu.hdu.hziee.betastudio.util.common.AssertUtil;
import edu.hdu.hziee.betastudio.util.common.JwtTokenUtil;
import edu.hdu.hziee.betastudio.util.customenum.ExceptionResultCode;
import edu.hdu.hziee.betastudio.util.customenum.basic.ZCMUConstant;
import edu.hdu.hziee.betastudio.util.resulttemplate.restfulresult.RestUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 注解实现类
 * {@link CheckLogin}
 */
@Aspect
@Component
@Order(1)
public class SessionService {

    @Autowired
    UserService userService;

    @Autowired
    JwtTokenUtil tokenUtil;

    @Pointcut("execution(* edu.hdu.hziee.betastudio.web.controller..*(..)) && @annotation(edu.hdu.hziee.betastudio.web.aop.CheckLogin)")
    public void tag(){};

    @Around("tag()")
    public Object check(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] obj=joinPoint.getArgs();
        HttpServletRequest httpServletRequest=null;
        UserCheckedRequest request=null;
        for (Object o : obj) {
            if(o instanceof HttpServletRequest){
                httpServletRequest=(HttpServletRequest) o;
                break;
            }
        }
        for(Object o : obj){
            if(o instanceof UserCheckedRequest){
                request=(UserCheckedRequest) o;
            }
        }
        if(httpServletRequest==null){
            return RestUtil.buildFailResult(ExceptionResultCode.ILLEGAL_PARAMETERS.getCode(),"未找到http请求");
        }
        if(request==null){
            return RestUtil.buildFailResult(ExceptionResultCode.ILLEGAL_PARAMETERS.getCode(),"未找到鉴权请求");
        }
        String token=httpServletRequest.getHeader(ZCMUConstant.AUTH_HEAD);
        AssertUtil.assertNotNull(token,ExceptionResultCode.UNAUTHORIZED,"未登录或者登录凭证过期");
        if(!userService.verifyLogin(token)){
            return RestUtil.buildFailResult(ExceptionResultCode.UNAUTHORIZED.getCode(),"token验证失败，可能已经过期,请重新登录");
        }
        Long userId=Long.parseLong(tokenUtil.getUserTokenKey(token));
        request.setUserId(userId);
        return joinPoint.proceed();
    }
}
