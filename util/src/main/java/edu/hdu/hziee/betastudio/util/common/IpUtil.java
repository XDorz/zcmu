package edu.hdu.hziee.betastudio.util.common;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;

public class IpUtil {

    /**
     * 获取请求的真实 IP
     *
     * @param httpServletRequest 请求
     * @return 请求的真实 IP
     */
    public static String getIp(HttpServletRequest httpServletRequest) {
        String ip = httpServletRequest.getHeader("X-Forwarded-For");
        if (StrUtil.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            // 第一个 IP 为真实 IP
            int index = ip.indexOf(",");
            return index != -1 ? ip.substring(0, index) : ip;
        }
        ip = httpServletRequest.getHeader("X-Real-IP");
        if (StrUtil.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return httpServletRequest.getRemoteAddr();
    }
}
