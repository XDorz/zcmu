package edu.hdu.hziee.betastudio.util.antixss;

import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
public final class XssUtil {

    private XssUtil() {
    }

    /**
     * 网上找的XSS匹配正则表达式
     */
    private final static Pattern[] PATTERNS = new Pattern[]{
            // Script fragments
            Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
            // src='...'
            Pattern.compile("src[\r\n]*=[\r\n]*\'(.*?)\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("src[\r\n]*=[\r\n]*\"(.*?)\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // lonely script tags
            Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // eval(...)
            Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // expression(...)
            Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // javascript:...
            Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
            // vbscript:...
            Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
            // 空格英文单双引号
            Pattern.compile("[\s'\"]+", Pattern.CASE_INSENSITIVE),
            // onload(...)=...
            Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // alert
            Pattern.compile("alert(.*?)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("<", Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile(">", Pattern.MULTILINE | Pattern.DOTALL),
            //Checks any html tags i.e. <script, <embed, <object etc.
            Pattern.compile("(<(script|iframe|embed|frame|frameset|object|img|applet|body|html|style|layer|link|ilayer|meta|bgsound))")
    };

    /**
     * 对请求对象参数进行过滤校验
     *
     * @param params
     * @return
     */
    public static String filterBody(String params) {
        try {
            if (StringUtils.isBlank(params)) {
                return params;
            }
            final Map<String, Object> map = JSONObject.parseObject(params, Map.class);
            if (map.isEmpty()) {
                return params;
            }

            // 参数过滤
            final Iterator<Map.Entry<String, Object>> iterator = map.entrySet().stream().iterator();
            while (iterator.hasNext()) {
                final Map.Entry<String, Object> next = iterator.next();
                next.setValue(filterParam(next.getValue()));
            }
            return JSON.toJSONString(map);
        } catch (Exception e) {
            log.error("XSS过滤异常：", e);
        }
        return params;
    }

    /**
     * 对请求字符串参数进行过滤校验
     *
     * @param param
     * @param <T>
     * @return
     */
    public static <T> Object filterParam(T param) {
        if (param instanceof String) {
            try {
                String value = String.valueOf(param);
                for (Pattern pattern : PATTERNS) {
                    value = pattern.matcher(value).replaceAll("");
                }
                return value;
            } catch (Exception e) {
                log.error("XSS参数过滤异常：", e);
            }
        }
        return param;
    }
}
