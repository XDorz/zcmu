package edu.hdu.hziee.betastudio.util.common;

import edu.hdu.hziee.betastudio.util.customenum.ExceptionResultCode;
import edu.hdu.hziee.betastudio.util.customenum.basic.KVenum;
import lombok.Data;
import lombok.ToString;

import java.lang.reflect.Method;

/**
 * 自定义异常
 */
@Data
@ToString
public class ZCMUException extends RuntimeException {

    private static final long serialVersionUID = 48505976702886775L;

    /**
     * 异常码
     */
    private String errorCode;

    /**
     * 异常信息
     */
    private String desc;

    /**
     * 针对其他异常生成Gims异常
     *
     * @param cause             其他异常
     */
    public ZCMUException(final Throwable cause) {
        super(cause);
        this.errorCode = ExceptionResultCode.SYSTEM_ERROR.getCode();
        try {
            Class<? extends Throwable> exception=cause.getClass();
            Method getMessage = exception.getDeclaredMethod("getMessage");
            this.desc=(String)getMessage.invoke(cause);
        } catch (Exception e) {
            this.desc = ExceptionResultCode.SYSTEM_ERROR.getDesc();
        }
    }

    /**
     * 通过错误码和描述构造
     *
     * @param errorCode         错误码
     * @param desc              描述
     */
    public ZCMUException(String errorCode, String desc) {
        this.errorCode = errorCode;
        this.desc = desc;
    }

    /**
     * 通过枚举类构造
     *
     * @param operateEnum    错误枚举类
     */
    public ZCMUException(KVenum<String,String> operateEnum) {
        this.errorCode = operateEnum.getCode();
        this.desc = operateEnum.getDesc();
    }

    /**
     * 通过枚举类和描述构造
     *
     * @param operateEnum    错误枚举类
     * @param desc              描述
     */
    public ZCMUException(KVenum<String,String> operateEnum, String desc) {
        this.errorCode = operateEnum.getCode();
        this.desc = desc;
    }

    /**
     * 通过其他异常和错误码构造
     *
     * @param cause         其他异常
     * @param errorCode     错误码
     */
    public ZCMUException(Throwable cause, String errorCode) {
        super(cause);
        this.errorCode = errorCode;
        try {
            Class<? extends Throwable> exception=cause.getClass();
            Method getMessage = exception.getDeclaredMethod("getMessage");
            this.desc=(String)getMessage.invoke(cause);
        } catch (Exception e) {
            this.desc = ExceptionResultCode.SYSTEM_ERROR.getDesc();
        }
    }

    /**
     * 仅描述构造
     *
     * @param desc      描述
     */
    public ZCMUException(String desc) {
        super(desc);
    }

    @Override
    public String getMessage() {
        return getDesc();
    }
}
