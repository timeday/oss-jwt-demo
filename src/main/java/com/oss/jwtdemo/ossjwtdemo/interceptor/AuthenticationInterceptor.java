package com.oss.jwtdemo.ossjwtdemo.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oss.jwtdemo.ossjwtdemo.annotation.IgnoreToken;
import com.oss.jwtdemo.ossjwtdemo.annotation.UserLoginToken;
import com.oss.jwtdemo.ossjwtdemo.bean.User;
import com.oss.jwtdemo.ossjwtdemo.service.UserService;
import com.oss.jwtdemo.ossjwtdemo.utils.JWTResponseData;
import com.oss.jwtdemo.ossjwtdemo.utils.JWTResult;
import com.oss.jwtdemo.ossjwtdemo.utils.JWTUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AuthenticationInterceptor implements HandlerInterceptor {
    private static final ObjectMapper MAPPER=new ObjectMapper();

    @Autowired
    UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {

        // 获取 HTTP HEAD 中的 TOKEN
        String authorization = httpServletRequest.getHeader("Authorization");

        // 如果不是映射到方法直接通过
        if(!(object instanceof HandlerMethod)){
            return true;
        }
        HandlerMethod handlerMethod=(HandlerMethod)object;
        Method method=handlerMethod.getMethod();

        //检查是否有IgnoreToken注释，有则跳过认证
        if (method.isAnnotationPresent(IgnoreToken.class)) {
            IgnoreToken passToken = method.getAnnotation(IgnoreToken.class);
            if (passToken.required()) {
                return true;
            }
        }
        //检查有没有需要用户权限的注解
        if (method.isAnnotationPresent(UserLoginToken.class)) {
            UserLoginToken userLoginToken = method.getAnnotation(UserLoginToken.class);
            if (userLoginToken.required()) {
                // 执行认证
                if (authorization == null) {
                    throw new RuntimeException("无token，请重新登录");
                }
                //验证token
                JWTResult result = JWTUtils.validateJWT(authorization);
                if(result.isSuccess()){
                    Claims claims = result.getClaims();
                    //解析字符串
                    String[] split = JSON.parseObject(claims.get("sub").toString(), String.class)
                            .replace("{", "")
                            .replace("}", "")
                            .split(",");
                    Map<String,String> map=new HashMap();
                    Arrays.stream(split).forEach(s -> {
                        String[] split1 = s.split(":");
                        map.put(split1[0],split1[1]);
                    });
                    String id = map.get("\"id\"");
                    //查询该对象是否存在
                    User user=userService.findUserById(id.replace("\"",""));
                    if (user == null) {
                        throw new RuntimeException("用户不存在，请重新登录");
                    }
                }else{
                    throw new RuntimeException(result.getErroCode()+"");
                }
                return true;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
