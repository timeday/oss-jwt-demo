package com.oss.jwtdemo.ossjwtdemo.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SignatureException;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;

public class JWTUtils {

    private static final  String JWT_SECERT="adadalaaiadlaewjoei!@@#!!###@@@##"; //密钥

    //java对象转换
    private static final ObjectMapper MAPPER=new ObjectMapper();

    private static final int JWT_ERRCODE_EXPLRE=1005;//token过期

    private static final int JWT_ERRCODE_FAL=1006;//验证不通过

    private static final int JWT_ERRCODE_SIGNATURE_FAL=1007;//签名不通过

    /**
     * JWT 添加至HTTP HEAD中的前缀
     */
    private static final String JWT_SEPARATOR = "Bearer ";


    public static SecretKey generalKey(){
        try{
            //编码
            byte[] jwtSecertBytes = JWT_SECERT.getBytes("UTF-8");
            SecretKey key=new SecretKeySpec(jwtSecertBytes,0,jwtSecertBytes.length,"AES");
            return key;
        }catch (Exception e){
           e.printStackTrace();
            return null;
        }
    }

    /**
     * 签发JWT 创建token的方法
     * @param id jwt的唯一身份标识，主要用来作为一次性token，从而回避重放冲击
     * @param iss jwt签发者
     * @param subject jwt所面向的用户，payload中记录的public claims 当前环境中就是用户的登录名
     * @param time 有效时间 单位毫秒
     * @return token token是一次性的。是为一个用户的有效登陆周期准备的一个token，用户推出或超时，token失效
     */
    public static String createJWT(String id,String iss,Object subject,long time){
        SignatureAlgorithm signatureAlgorithm= SignatureAlgorithm.HS256;
        long currentTimeMillis = System.currentTimeMillis();
        Date now=new Date(currentTimeMillis);
        SecretKey secretKey = generalKey();
        JwtBuilder builder = Jwts.builder().setId(id)
                .setIssuer(iss)
                .setSubject(generalSubject(subject))//主体内容
                .setIssuedAt(now)
                .signWith(signatureAlgorithm, secretKey);
        if (time>=0){
            long expMillis=currentTimeMillis+time;
             Date expDate=new Date(expMillis);
             builder.setExpiration(expDate);
        }
        return JWT_SEPARATOR+builder.compact();

    }


    /**
     * 验证JWT
     * @param jwtStr
     * @return
     */
    public static JWTResult validateJWT(String jwtStr){
        JWTResult jwtResult=new JWTResult();
        Claims claims=null;
        try {
            claims=parseJWT(jwtStr);
            jwtResult.setSuccess(true);
            jwtResult.setClaims(claims);
        }catch (ExpiredJwtException e){
            jwtResult.setErroCode(JWT_ERRCODE_EXPLRE);
            jwtResult.setSuccess(false);
        }catch (SignatureException e){
            jwtResult.setErroCode(JWT_ERRCODE_SIGNATURE_FAL);
            jwtResult.setSuccess(false);
        }catch (Exception e){
            jwtResult.setErroCode(JWT_ERRCODE_FAL);
            jwtResult.setSuccess(false);
        }
        return jwtResult;
    }

    /**
     * 解析JWT
     * @param jwtStr
     * @return
     */
    private static Claims parseJWT(String jwtStr) throws Exception {
        SecretKey secretKey = generalKey();
        //截取字符串
        jwtStr=StringUtils.substringAfter(jwtStr, JWT_SEPARATOR);
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwtStr)
                .getBody();
    }

    /**
     * 生成subject信息
     * @param subObject 要转换的对象
     * @return Java对象->json字符串出错返回null
     */
    public static String generalSubject(Object subObject){
        try {
            return MAPPER.writeValueAsString(subObject);
        }catch (JsonProcessingException e){
            e.printStackTrace();
            return null;

        }

    }



}
