package edu.hdu.hziee.betastudio.util.resulttemplate.restfulresult;

import edu.hdu.hziee.betastudio.util.customenum.HttpResultCode;
import edu.hdu.hziee.betastudio.util.customenum.basic.KVenum;

public class RestUtil {

    /**
     * 构建失败结果
     *
     * @param <T>
     * @return
     */
    public static <T> ZCMUResult<T> buildFailResult(KVenum<String,String> resultCode) {
        ZCMUResult<T> result = buildResult(resultCode);
        result.setSuccess(false);
        result.setRetry(false);
        return result;
    }

    /**
     * 构建失败结果
     *
     * @param message
     * @param <T>
     * @return
     */
    public static <T> ZCMUResult<T> buildFailResult(String message) {
        ZCMUResult<T> result = buildResult(HttpResultCode.SYSTEM_ERROR, message);
        result.setSuccess(false);
        result.setRetry(false);
        return result;
    }

    /**
     * 构建失败结果
     *
     * @param message
     * @param <T>
     * @return
     */
    public static <T> ZCMUResult<T> buildFailResult(String code,String message) {
        ZCMUResult<T> result = buildResult(HttpResultCode.SYSTEM_ERROR, message);
        result.setCode(code);
        result.setSuccess(false);
        result.setRetry(false);
        return result;
    }

    /**
     * 构建成功结果
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ZCMUResult<T> buildSuccessResult(T data) {
        ZCMUResult<T> result = buildSuccessResult();
        result.setData(data);
        return result;
    }

    /**
     * 构建成功结果
     *
     * @param data
     * @param message
     * @param <T>
     * @return
     */
    public static <T> ZCMUResult<T> buildSuccessResult(T data, String message) {
        ZCMUResult<T> result = buildSuccessResult(message);
        result.setData(data);
        return result;
    }

    /**
     * 构建成功结果
     *
     * @param <T>
     * @return
     */
    public static <T> ZCMUResult<T> buildSuccessResult() {
        ZCMUResult<T> result = buildResult(HttpResultCode.SUCCESS);
        result.setSuccess(true);
        result.setRetry(false);
        return result;
    }

    /**
     * 构建成功结果
     *
     * @param message
     * @param <T>
     * @return
     */
    public static <T> ZCMUResult<T> buildSuccessResult(String message) {
        ZCMUResult<T> result = buildResult(HttpResultCode.SUCCESS, message);
        result.setSuccess(true);
        result.setRetry(false);
        return result;
    }

    /**
     * 构建结果
     *
     * @param resultCode
     * @param <T>
     * @return
     */
    public static <T> ZCMUResult<T> buildResult(KVenum<String,String> resultCode) {
        return buildResult(resultCode,resultCode.getDesc());
    }

    /**
     * 构建结果
     *
     * @param resultCode
     * @param message
     * @param <T>
     * @return
     */
    public static <T> ZCMUResult<T> buildResult(KVenum<String,String> resultCode, String message) {
        ZCMUResult<T> result = new ZCMUResult<>();
        result.setCode(resultCode.getCode());
        result.setErrorMsg(message);
        result.setRetry(false);
        if (resultCode != HttpResultCode.SUCCESS) {
            result.setSuccess(true);
        } else {
            result.setSuccess(false);
        }
        return result;
    }
}
