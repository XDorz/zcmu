package edu.hdu.hziee.betastudio.business.aop;

import edu.hdu.hziee.betastudio.business.perm.request.UserPermRequest;
import edu.hdu.hziee.betastudio.business.perm.service.PermService;
import edu.hdu.hziee.betastudio.util.common.AssertUtil;
import edu.hdu.hziee.betastudio.util.customenum.ExceptionResultCode;
import edu.hdu.hziee.betastudio.util.customenum.PermEnum;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@Order(1)
public class VerifyPermService {

    @Autowired
    PermService permService;

    @Pointcut("execution(* edu.hdu.hziee.betastudio.business..*(..)) && @annotation(edu.hdu.hziee.betastudio.business.aop.VerifyPerm)")
    public void tag(){};

    @Around("tag() && @annotation(verify)")
    public Object verify(ProceedingJoinPoint joinPoint,VerifyPerm verify) throws Throwable{
        //获取所有入参，取得PermRequest
        Object[] objs = joinPoint.getArgs();
        PermRequest request = null;
        for (Object o : objs) {
            if (o instanceof PermRequest) {
                request = (PermRequest) o;
                break;
            }
        }
        AssertUtil.assertNotNull(request, ExceptionResultCode.SYSTEM_ERROR, "鉴权失败, 没有鉴权对象");
        //判断是否略过鉴权
        if(!request.isSkipVerify()){
            PermEnum[] perms = verify.perms();
            //构建权限查询请求
            UserPermRequest permRequest = UserPermRequest.builder()
                    .userId(request.getVerifyId())
                    .build();
            permRequest.setSkipVerify(true);
            //按是否全部需求分类
            if(verify.requiredAll()){
                for (PermEnum permEnum : perms) {
                    permRequest.setCodeName(permEnum.getCode());
                    boolean b = permService.userExistPerm(permRequest);
                    AssertUtil.assertTrue(b,ExceptionResultCode.FORBIDDEN,"您无权访问！");
                }
            }else {
                boolean b = false;
                for (PermEnum permEnum : perms) {
                    permRequest.setCodeName(permEnum.getCode());
                    if(permService.userExistPerm(permRequest)){
                        b=true;
                        break;
                    }
                }
                AssertUtil.assertTrue(b,ExceptionResultCode.FORBIDDEN,"您无权访问！");
            }
        }
        return joinPoint.proceed();
    }
}
