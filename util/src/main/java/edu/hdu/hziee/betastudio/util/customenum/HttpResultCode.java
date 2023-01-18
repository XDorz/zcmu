package edu.hdu.hziee.betastudio.util.customenum;

import edu.hdu.hziee.betastudio.util.customenum.basic.KVenum;
import lombok.Getter;

@Getter
public enum HttpResultCode implements KVenum<String,String> {
    SUCCESS("200", "执行成功"),

    PARTIAL_CONTENT("206", "部分执行"),

    ILLEGAL_PARAMETERS("400", "参数异常"),

    UNAUTHORIZED("401", "未找到用户登录凭证"),

    FORBIDDEN("403", "无权访问"),

    NOT_FOUND("404", "请求内容不存在"),

    SYSTEM_ERROR("500", "系统异常"),
            ;

    String  errorCode;

    String desc;

    HttpResultCode(String errorCode,String desc){
        this.errorCode=errorCode;
        this.desc=desc;
    }

    public String getName(){
        return name();
    }

    @Override
    public String getCode() {
        return getErrorCode();
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
