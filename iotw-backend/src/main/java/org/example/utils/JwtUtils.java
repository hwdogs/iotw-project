package org.example.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author hwshou
 * @date 2025/5/19  15:43
 */
@Component
public class JwtUtils {

    @Value("${spring.security.jwt.key}")
    String key;

    @Value("${spring.security.jwt.expire}")
    int expire;

    @Resource
    StringRedisTemplate template;

    /**
     * 使jwt失效
     *
     * @param headerToken headerToken
     * @return boolean
     */
    public boolean invalidJwt(String headerToken) {
        String token = this.convertToken(headerToken);
        if (token == null) {
            return false;
        }
        Algorithm algorithm = Algorithm.HMAC256(key);   // HMAC256算法
        JWTVerifier verifier = JWT.require(algorithm).build();  //需要算法进行jwt验证
        try {
            DecodedJWT jwt = verifier.verify(token);    //解码token
            String id = jwt.getId();
            return deleteToken(id, jwt.getExpiresAt());
        } catch (JWTVerificationException e) {
            return false;
        }

    }

    /**
     * 删除token
     *
     * @param uuid jwt唯一uuid
     * @param time 结束事件
     * @return boolean
     */
    private boolean deleteToken(String uuid, Date time) {
        if (this.isInvalidToken(uuid)) {
            return false;
        }
        Date now = new Date();
        long expire = Math.max(time.getTime() - now.getTime(), 0);
        template.opsForValue().set(Const.JWT_BLACK_LIST + uuid, "", expire, TimeUnit.MILLISECONDS);
        return true;
    }

    /**
     * 判断token是否已经失效
     *
     * @param uuid jwt的uuid
     * @return boolean
     */
    private boolean isInvalidToken(String uuid) {
        return template.hasKey(Const.JWT_BLACK_LIST + uuid);
    }

    /**
     * 解析jwt
     *
     * @param HeaderToken 请求头token
     * @return DecodedJWT
     */
    public DecodedJWT resolveJwt(String HeaderToken) {
        String token = this.convertToken(HeaderToken);
        if (token == null) {
            return null;
        }
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            DecodedJWT verify = verifier.verify(token); //判断token是否被用户篡改
            if (this.isInvalidToken(verify.getId())) {    //判断jwt是否已经失效
                return null;
            }
            Date expiresAt = verify.getExpiresAt(); //获取过期日期
            return new Date().after(expiresAt) ? null : verify;
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    /**
     * 创建jwt
     *
     * @param details  UserDetails类
     * @param id       用户id
     * @param username 用户名
     * @return jwt
     */
    public String createJwt(UserDetails details, Integer id, String username) {
        Algorithm algorithm = Algorithm.HMAC256(key);
        Date expire = expireTime();
        return JWT.create()
                .withJWTId(UUID.randomUUID().toString())    //每个令牌都有自己随机的uuid
                .withClaim("id", id)
                .withClaim("name", username)
                .withClaim("authorities", details.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .withExpiresAt(expire)
                .withIssuedAt(new Date())
                .sign(algorithm);
    }

    public Date expireTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, expire * 24);
        return calendar.getTime();
    }

    /**
     * 用于解析header中用户的方法
     *
     * @param jwt DecodedJWT
     * @return UserDetails
     */
    public UserDetails toUser(DecodedJWT jwt) {
        Map<String, Claim> claims = jwt.getClaims();
        return User
                .withUsername(claims.get("name").asString())
                .password("*******")
                .authorities(claims.get("authorities").asArray(String.class))
                .build();
    }

    /**
     * 解析id
     *
     * @param jwt DecodedJWT
     * @return Integer
     */
    public Integer toId(DecodedJWT jwt) {
        Map<String, Claim> claims = jwt.getClaims();
        return claims.get("id").asInt();
    }

    /**
     * 判断token是否合法并切割
     *
     * @param HeaderToken 请求头token
     * @return 切割掉Bearer的token
     */
    private String convertToken(String HeaderToken) {
        if (HeaderToken == null || !HeaderToken.startsWith("Bearer ")) {
            return null;
        }
        return HeaderToken.substring(7);
    }
}
