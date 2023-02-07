package edu.hdu.hziee.betastudio.util.antixss.filter;

import edu.hdu.hziee.betastudio.util.antixss.wrapper.XssHttpServletRequestWrapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;


@Slf4j
public class XssFilter implements Filter {

    @Override
    public void init (FilterConfig filterConfig) {
        // 初始化调用
    }

    @Override
    public void doFilter (ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        final XssHttpServletRequestWrapper requestWrapper = new XssHttpServletRequestWrapper((HttpServletRequest) servletRequest);
        filterChain.doFilter(requestWrapper, servletResponse);
    }

    @Override
    public void destroy () {
        // 销毁调用
    }
}