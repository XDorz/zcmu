package edu.hdu.hziee.betastudio.util.antixss.config;

import edu.hdu.hziee.betastudio.util.antixss.filter.XssFilter;
import jakarta.servlet.DispatcherType;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
//public class XssFilterConfig {
//
//    @Bean
//    public FilterRegistrationBean xssFilterRegistrationBean () {
//        FilterRegistrationBean initXssFilterBean = new FilterRegistrationBean();
//        // 设置自定义过滤器
//        initXssFilterBean.setFilter(new XssFilter());
//        // 设置优先级（值越低，优先级越高）
//        initXssFilterBean.setOrder(1);
//        // 设置过滤路径
//        initXssFilterBean.addUrlPatterns("/*");
//        // 设置过滤器名称
//        initXssFilterBean.setName("XSS_filter");
//        // 设置过滤器作用范围（可以配置多种，这里指定过滤请求资源）
//        initXssFilterBean.setDispatcherTypes(DispatcherType.REQUEST);
//        return initXssFilterBean;
//    }
//}