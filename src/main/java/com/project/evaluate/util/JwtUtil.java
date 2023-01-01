package com.project.evaluate.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description Jwt工具类
 * @since 2022/12/5 16:15
 */
public class JwtUtil {
    //    过期时间
    private static Long JWT_TTL = 60 * 60 * 1000 * 3L;
    //    Jwt的密钥Key
    private static String JWT_KEY = "evaluate";

    //    Jwt的发行者
    private static String JWT_SUBJECT = "ComputerSchool";

    /**
     * @param
     * @return 返回加密之后的JWT_KEY
     * @description 对JWT_KEY进行Base64编码后AES加密
     * @author Levi
     * @since 2022/12/5 16:21
     */
    private static SecretKey getBase64Key() {
        byte[] decode = Base64.getDecoder().decode(JWT_KEY);
        SecretKey key = new SecretKeySpec(decode, 0, decode.length, "AES");
        return key;
    }

    /**
     * @param
     * @return 返回uuid的字符串
     * @description 通过UUID生成uuid
     * @author Levi
     * @since 2022/12/5 16:23
     */
    private static String getUUID() {
//        生成uuid并将其中的-去掉
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * @param Object 主体数据
     * @param uuid   独特标识符
     * @param ttl    持续时间
     * @return JwtBuilder
     * @description 使用主体数据，uuid，以及过期时间建立JwtBuilder
     * @author Levi
     * @since 2022/12/5 16:39
     */
    private static JwtBuilder getJwtBuilder(String Object, String uuid, Long ttl) {
//        设置加密算法HS256
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
//        获取加密密钥
        SecretKey base64Key = getBase64Key();
//        设置过期时间
        Long nowTimeMills = System.currentTimeMillis();
        Date nowDate = new Date(nowTimeMills);
        if (ttl == null) {
            ttl = JWT_TTL;
        }
        Long endTimeMills = nowTimeMills + ttl;
        Date endDate = new Date(endTimeMills);
//        设置JwtBuilder
        return Jwts.builder()
//                设置主体数据
                .setSubject(Object)
//                设置uuid
                .setId(uuid)
//                设置签发机构
                .setIssuer(JWT_SUBJECT)
//                设置签发时间
                .setIssuedAt(nowDate)
//                设置签名算法以及加密串
                .signWith(signatureAlgorithm, base64Key)
//                设置过期时间
                .setExpiration(endDate);
    }

    /**
     * @param Object String类型主体数据
     * @return 返回Jwt字符串
     * @description 将传入的String类型主体数据加密之后返回Jwt字符串
     * @author Levi
     * @since 2022/12/5 16:41
     */
    public static String createJwt(String Object) {
        return getJwtBuilder(Object, getUUID(), null).compact();
    }

    /**
     * @param Object String类型主体数据
     * @param ttl    过期时间
     * @return 返回Jwt字符串
     * @description 将主体数据写入到Jwt字符串中，并设置过期时间，返回Jwt字符串
     * @author Levi
     * @since 2022/12/5 16:54
     */
    public static String createJwt(String Object, Long ttl) {
        return getJwtBuilder(Object, getUUID(), ttl).compact();
    }

    /**
     * @param Object String类型主体数据
     * @param uuid   独特标识
     * @param ttl    过期时间
     * @return 返回Jwt字符串
     * @description 设置独特标识、过期时间，将主体数据放入到Jwt后返回Jwt字符串
     * @author Levi
     * @since 2022/12/5 16:57
     */
    public static String createJwt(String Object, String uuid, Long ttl) {
        return getJwtBuilder(Object, uuid, ttl).compact();
    }


    /**
     * @param token Jwt生成的token
     * @return 返回从token中获取的Claims类型数据
     * @description 通过Jwt生成的Token获取其中的数据，并且返回其中的数据
     * @author Levi
     * @since 2022/12/5 17:00
     */
    public static Claims parseJwt(String token) throws Exception {
        SecretKey secretKey = getBase64Key();
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * @param token Jwt生成的token
     * @return 返回token是否过期
     * @description 通过token中的过期时间与现在时间比较知道是否过期
     * @author Levi
     * @since 2022/12/6 01:46
     */
    public static boolean isTimeout(String token) throws Exception {
        Claims claims = parseJwt(token);
        Date expirationTime = claims.getExpiration();
        Date nowTime = new Date(System.currentTimeMillis());
        if (nowTime.after(expirationTime)) {
            return true;
        } else {
            return false;
        }
    }

}
