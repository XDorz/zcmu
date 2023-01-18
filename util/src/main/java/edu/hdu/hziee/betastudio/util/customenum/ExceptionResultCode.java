package edu.hdu.hziee.betastudio.util.customenum;

import edu.hdu.hziee.betastudio.util.customenum.basic.KVenum;
import lombok.Getter;

@Getter
public enum ExceptionResultCode implements KVenum<String,String> {

    /**
     * 调用成功
     */
    SUCCESS("调用成功"),

    /**
     * 系统异常
     */
    SYSTEM_ERROR("系统异常"),

    /**
     * 参数异常
     */
    ILLEGAL_PARAMETERS("参数异常"),

    /**
     * 无权访问(未登录)
     */
    UNAUTHORIZED("未登录无权访问"),

    /**
     * 无权访问(无权限)
     */
    FORBIDDEN("无权访问"),

    /**
     * 服务器错误
     */
    INTERNAL_SERVER_ERROR("服务器错误");

    ;

    String desc;

    ExceptionResultCode(String desc){
        this.desc=desc;
    }


    @Override
    public String getCode() {
        return name();
    }

    @Override
    public String getDesc() {
        return desc;
    }

}
