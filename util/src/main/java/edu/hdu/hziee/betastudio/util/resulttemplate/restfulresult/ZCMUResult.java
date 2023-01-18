package edu.hdu.hziee.betastudio.util.resulttemplate.restfulresult;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * restful类型的返回结果
 *
 * @param <T> 返回数据类型
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ZCMUResult<T> {

    /**
     * 执行结果
     */
    private boolean success = false;

    /**
     * 是否需要重试
     */
    private boolean isRetry = false;

    /**
     * 错误码
     */
    private String Code;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 数据
     */
    private T data;

    public ZCMUResult(boolean success, boolean isRetry, String code, String errorMsg) {
        this.success = success;
        this.isRetry = isRetry;
        Code = code;
        this.errorMsg = errorMsg;
    }
}
