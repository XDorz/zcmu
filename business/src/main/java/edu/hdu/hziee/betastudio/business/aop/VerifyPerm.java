package edu.hdu.hziee.betastudio.business.aop;

import edu.hdu.hziee.betastudio.util.customenum.PermEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 鉴权注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface VerifyPerm {

    /**
     *需要的权限
     */
    PermEnum[] perms() default {};

    /**
     * 指定需要的权限是全部需要还是只有一个满足即可
     * 默认false是只满足一个
     */
    boolean requiredAll() default false;


}
