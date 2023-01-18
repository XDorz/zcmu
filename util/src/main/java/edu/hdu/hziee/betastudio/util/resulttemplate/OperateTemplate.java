package edu.hdu.hziee.betastudio.util.resulttemplate;

import edu.hdu.hziee.betastudio.util.common.IpUtil;
import edu.hdu.hziee.betastudio.util.common.ZCMUException;
import edu.hdu.hziee.betastudio.util.customenum.ExceptionResultCode;
import edu.hdu.hziee.betastudio.util.resulttemplate.restfulresult.RestUtil;
import edu.hdu.hziee.betastudio.util.resulttemplate.restfulresult.ZCMUResult;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;

/**
 * Restful风格数据返回操作模板
 */
public class OperateTemplate{

    /**
     * 执行操作模板
     *
     * @param logger      日志
     * @param description 方法描述
     * @param request     请求
     * @param callBack    操作方法
     * @param <T>         泛型
     * @return 请求返回体
     */
    public static <T> ZCMUResult<T> operate(Logger logger, String description, Object request, HttpServletRequest httpServletRequest, OperateCallBack<T> callBack) {
        long start = System.currentTimeMillis();
        ZCMUResult<T> result = null;
        String ip = IpUtil.getIp(httpServletRequest);
        try {
            callBack.before();
            result = callBack.operate();
            callBack.after();
            return result;
        } catch (ZCMUException c) {
            logger.warn("[FAIL  ] ip=[{}], description=[{}], request=[{}], result=[{}]", ip, c.getDesc(), request, result);
            c.printStackTrace();
            result = RestUtil.buildFailResult(c.getErrorCode(),c.getMessage());
            return result;
        } catch (Exception e) {
            logger.error("[ERROR ] ip=[{}], description=[{}], request=[{}], result=[{}]", ip, ExceptionResultCode.INTERNAL_SERVER_ERROR.getDesc(), request, result);
            e.printStackTrace();
            result= RestUtil.buildFailResult(ExceptionResultCode.INTERNAL_SERVER_ERROR);
            return result;
        } finally {
            long end = System.currentTimeMillis();
            logger.info("[FINAL ] ip=[{}], description=[{}], duration=[{}], request=[{}], result=[{}]", ip, description, end - start, request, result);
        }
    }
}