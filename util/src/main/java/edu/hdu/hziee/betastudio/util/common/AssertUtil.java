package edu.hdu.hziee.betastudio.util.common;

import edu.hdu.hziee.betastudio.util.customenum.basic.KVenum;
import org.springframework.util.StringUtils;

public class AssertUtil {

    /**
     * 断言true
     *
     * @param expression        断言对象
     * @param resultCode        错误枚举
     */
    public static void assertTrue(Boolean expression, KVenum<String,String> resultCode) {
        if (expression == null || !expression) {
            throw new ZCMUException(resultCode);
        }
    }

    /**
     * @param expression        断言对象
     * @param resultCode        错误枚举
     * @param errorMsg          错误信息
     */
    public static void assertTrue(Boolean expression, KVenum<String,String> resultCode, String errorMsg) {
        if (expression == null || !expression) {
            throw new ZCMUException(resultCode, errorMsg);
        }
    }

    /**
     * 断言对象等于null
     *
     * @param obj               断言对象
     * @param resultCode        错误枚举
     * @param errorMsg          错误信息
     */
    public static void assertNull(Object obj,KVenum<String,String> resultCode,String errorMsg) {
        if (obj != null) {
            throw new ZCMUException(resultCode, errorMsg);
        }
    }

    /**
     * 断言对象不等于null
     *
     * @param obj               断言对象
     * @param resultCode        错误枚举
     * @param errorMsg          错误信息
     */
    public static void assertNotNull(Object obj,KVenum<String,String> resultCode,String errorMsg) {
        if (obj == null) {
            throw new ZCMUException(resultCode, errorMsg);
        }
        if(obj instanceof String){
            if("".equals((String)obj)){
                throw new ZCMUException(resultCode, errorMsg);
            }
        }
    }

    /**
     * 断言对象不等于null
     *
     * @param obj               断言对象
     * @param resultCode        错误枚举
     */
    public static void assertNotNull(Object obj,KVenum<String,String> resultCode) {
        if (obj == null) {
            throw new ZCMUException(resultCode);
        }
        if(obj instanceof String){
            if("".equals((String)obj)){
                throw new ZCMUException(resultCode);
            }
        }
    }

    /**
     * 断言字符串不为空串
     *
     * @param str               断言字符串
     * @param resultCode        错误枚举
     * @param errorMsg          错误信息
     */
    public static void assertStringNotBlank(String str,KVenum<String,String> resultCode, String errorMsg) {
        if (StringUtils.hasText(str)) {
            throw new ZCMUException(resultCode, errorMsg);
        }
    }

    /**
     * 断言字符串不为空串
     *
     * @param str               断言字符串
     * @param resultCode        错误枚举
     */
    public static void assertStringNotBlank(String str,KVenum<String,String> resultCode) {
        if (StringUtils.hasText(str)) {
            throw new ZCMUException(resultCode);
        }
    }

    /**
     * 断言字符串为空串
     *
     * @param str               断言字符串
     * @param resultCode        错误枚举
     * @param errorMsg          错误信息
     */
    public static void assertStringBlank(String str,KVenum<String,String> resultCode,String errorMsg) {
        if (StringUtils.hasText(str)) {
            throw new ZCMUException(resultCode,errorMsg);
        }
    }

    /**
     * 断言相等
     *
     * @param o1            断言对象1
     * @param o2            断言对象2
     * @param resultCode    错误枚举
     * @param errorMsg      错误信息
     */
    public static void assertEquals(Object o1, Object o2, KVenum<String,String> resultCode,String errorMsg) {
        if (!o1.equals(o2)) {
            throw new ZCMUException(resultCode, errorMsg);
        }
    }

    /**
     * 断言相等
     *
     * @param o1            断言对象1
     * @param o2            断言对象2
     * @param resultCode    错误枚举
     */
    public static void assertEquals(Object o1, Object o2,KVenum<String,String> resultCode) {
        if (!o1.equals(o2)) {
            throw new ZCMUException(resultCode);
        }
    }

    /**
     * 判断 a 是否大于 b 若大于则抛异常
     *
     * @param a             被比较数
     * @param b             比较数
     * @param resultCode    错误枚举
     * @param errorMsg      错误信息
     */
    public static void assertBigger(double a, double b,KVenum<String,String> resultCode,String errorMsg) {
        if (a > b) {
            throw new ZCMUException(resultCode,errorMsg);
        }
    }

    /**
     * 判断 a 是否大于 b 若大于则抛异常
     *
     * @param a             被比较数
     * @param b             比较数
     * @param resultCode    错误枚举
     */
    public static void assertBigger(double a, double b,KVenum<String,String> resultCode) {
        if (a > b) {
            throw new ZCMUException(resultCode);
        }
    }
}
