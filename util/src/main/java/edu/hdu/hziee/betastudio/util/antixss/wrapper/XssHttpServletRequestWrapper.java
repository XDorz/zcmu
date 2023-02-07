package edu.hdu.hziee.betastudio.util.antixss.wrapper;

import edu.hdu.hziee.betastudio.util.antixss.XssUtil;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;

@Slf4j
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    public XssHttpServletRequestWrapper (HttpServletRequest request) {
        super(request);
    }

    /**
     * 对GET请求中参数进行过滤校验
     *
     * @param name
     * @return
     */
    @Override
    public String[] getParameterValues (String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            return null;
        }
        int count = values.length;
        String[] cleanParams = new String[count];
        for (int i = 0; i < count; i++) {
            cleanParams[i] = String.valueOf(XssUtil.filterParam(values[i]));
//            log.info("getParameterValues -> name：{}，过滤前参数：{}，过滤后参数：{}", name, values[i], cleanParams[i]);
        }
        return cleanParams;
    }

    /**
     * 对POST请求头进行参数过滤校验
     *
     * @param header
     * @return
     */
    //todo 此处好似未执行？？？
    @Override
    public Enumeration getHeaders (String header) {
        final String value = super.getHeader(header);
        final LinkedList<Object> list = new LinkedList<>();
        if (value != null) {
            final Object param = XssUtil.filterParam(value);
            list.addFirst(param);
            log.info("getHeaders -> header：{}，过滤前参数：{}，过滤后参数：{}", header, value, param);
        }
        return Collections.enumeration(list);
    }

    /**
     * 对POST请求中body参数进行校验
     *
     * @return
     * @throws IOException
     */
    @Override
    public ServletInputStream getInputStream () throws IOException {
        final ByteArrayInputStream stream = new ByteArrayInputStream(inputHandlers(super.getInputStream()).getBytes());
        return new ServletInputStream() {
            @Override
            public int read () {
                return stream.read();
            }

            @Override
            public boolean isFinished () {
                return false;
            }

            @Override
            public boolean isReady () {
                return false;
            }

            @Override
            public void setReadListener (ReadListener readListener) {

            }
        };
    }

    /**
     * 解析请求流参数
     *
     * @param servletInputStream
     * @return
     */
    public String inputHandlers (ServletInputStream servletInputStream) {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(servletInputStream, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            log.error("异常 e：", e);
        } finally {
            if (servletInputStream != null) {
                try {
                    servletInputStream.close();
                } catch (IOException e) {
                    log.error("servletInputStream 关闭异常 e：", e);
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error("reader 关闭异常 e：", e);
                }
            }
        }
        final String param = XssUtil.filterBody(sb.toString());
//        log.info("getInputStream -> 过滤前参数：{}，过滤后参数：{}", sb, param);
        return param;
    }
}
