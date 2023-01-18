package edu.hdu.hziee.betastudio.util.resulttemplate;

import edu.hdu.hziee.betastudio.util.resulttemplate.restfulresult.ZCMUResult;

/**
 * 操作模板接口
 *
 * @param <T> 返回结果数据类型
 */
public interface OperateCallBack<T> {

    void before();

    ZCMUResult<T> operate() throws Exception;

    default void after(){};
}
