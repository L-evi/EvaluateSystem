package com.project.evaluate.realm;

import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.entity.Faculty;
import com.project.evaluate.util.JwtToken;
import com.project.evaluate.util.JwtUtil;
import com.project.evaluate.util.redis.RedisCache;
import io.jsonwebtoken.Claims;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2023/1/2 10:01
 */
public class JwtRealm extends AuthorizingRealm {
    @Resource
    private RedisCache redisCache;

    @Resource
    private JwtUtil jwtUtil;


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }


    //    认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String token = (String) authenticationToken.getPrincipal();

        try {
            if (JwtUtil.isTimeout(token)) {
                throw new AuthenticationException("token已过期，请重新登录");
            }
            Claims claims = JwtUtil.parseJwt(token);
            JSONObject jsonObject = JSONObject.parseObject(claims.getSubject());
            String userID = (String) jsonObject.get("userID");
//            从redis中获取信息
            jsonObject = redisCache.getCacheObject("Faculty:" + userID);
            if (Objects.isNull(jsonObject)) {
                throw new UnknownAccountException("用户为登录，请重新登录");
            }
            SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(userID, token, this.getName());
            return simpleAuthenticationInfo;
        } catch (Exception e) {
            throw new UnknownAccountException("token无效，请重新登录");
        }
    }


    //    限定该Realm只能处理JwtToken
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }
}
