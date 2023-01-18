package edu.hdu.hziee.betastudio.util.common;

import edu.hdu.hziee.betastudio.util.customenum.ExceptionResultCode;
import edu.hdu.hziee.betastudio.util.customenum.basic.ZCMUConstant;
import edu.hdu.hziee.betastudio.util.redis.RedisUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Data
@Component
@ConfigurationProperties(prefix = "zcmu.jwt.config")
@PropertySource("classpath:/config/application.yml")
public class JwtTokenUtil {

    /**
     * 私钥
     */
    public String key;

    /**
     * token保存天数
     */
    private Integer ttl;

    /**
     * jwt签名的claim内存储token的key
     */
    public static final String PRIFIX="ZCMU-JWT-TOKEN-KEY";

    public static final String REDIS_JWT_PREFIX="JWT_FOR_REDIS_";

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 生成token
     */
    public String getToken(Long userId){
        Map claims=new HashMap(){
            {
                put(PRIFIX,userId);
            }
        };
        return getToken(claims);
    }

    /**
     * 生成token
     *
     * @param claims
     * @return
     */
    public String getToken(Map<String,Object> claims){
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.DATE,ttl);
        JwtBuilder jwtBuilder= Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(calendar.getTime())
                .signWith(SignatureAlgorithm.HS256,key.getBytes());
        String token=jwtBuilder.compact();
        String key= REDIS_JWT_PREFIX+claims.get(PRIFIX).toString();
        redisUtil.getStringUtil().setEx(key,token,ttl.longValue(), TimeUnit.DAYS);
        return token;
    }

    /**
     * 解密token
     *
     * @return
     */
    public Claims parseToken(String token){
        try {
            return Jwts.parser().setSigningKey(key.getBytes()).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 验证 redis 中 token 是否存在或是否有效或是否过期
     *
     * @param token token
     * @return redis 中 token 是否存在或 token 是否有效
     */
    public boolean verify(String token) {
        Claims claims = parseToken(token);
        // 判断 token 是否有效
        AssertUtil.assertNotNull(claims, ExceptionResultCode.ILLEGAL_PARAMETERS,"无效的token");
        AssertUtil.assertTrue(claims.getExpiration().after(new Date()), ExceptionResultCode.ILLEGAL_PARAMETERS,"token已过期，请重新登录");
        String keyValue = String.valueOf(claims.get(PRIFIX));
        String tokenByKey = redisUtil.getStringUtil().get(REDIS_JWT_PREFIX+keyValue);
        // 判断 redis 中是否存在此用户 token 缓存
        AssertUtil.assertNotNull(tokenByKey,ExceptionResultCode.SYSTEM_ERROR,"token存储异常，请重新登录，多次登录不上请联系管理反馈问题");
        // 判断是否和 redis 中缓存的 token 一致
        AssertUtil.assertEquals(tokenByKey,token,ExceptionResultCode.SYSTEM_ERROR,"token验证错误，请尝试重新登录，多次登录不上请联系管理反馈问题");
        return true;
    }

    public boolean verifyWithoutThrow(String token) {
        Claims claims = parseToken(token);
        // 判断 token 是否有效
        if(claims==null) return false;
        if(claims.getExpiration().before(new Date())) return false;
        String keyValue = String.valueOf(claims.get(PRIFIX));
        String tokenByKey = redisUtil.getStringUtil().get(REDIS_JWT_PREFIX+keyValue);
        // 判断 redis 中是否存在此用户 token 缓存
        if(tokenByKey==null) return false;
        // 判断是否和 redis 中缓存的 token 一致
        return token.equals(tokenByKey);
    }

    /**
     * 删除用户 token 缓存
     */
    public void delete(Long userId) {
        redisUtil.getKeyUtil().delete(REDIS_JWT_PREFIX + String.valueOf(userId));
    }

    /**
     * 获取 token 里的 USER_TOKEN_KEY
     *
     * @param httpServletRequest httpServletRequest
     * @return USER_TOKEN_KEY
     */
    public String getUserTokenKey(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader(ZCMUConstant.AUTH_HEAD);
        return getUserTokenKey(token);
    }

    /**
     * 通过 token 获得 USER_TOKEN_KEY(userId)
     *
     * @param token token
     * @return USER_TOKEN_KEY
     */
    public String getUserTokenKey(String token) {
        Claims claims = parseToken(token);
        return String.valueOf(claims.get(PRIFIX));
    }
}
