package com.oss.jwtdemo.ossjwtdemo.controller;

import com.oss.jwtdemo.ossjwtdemo.annotation.IgnoreToken;
import com.oss.jwtdemo.ossjwtdemo.annotation.UserLoginToken;
import com.oss.jwtdemo.ossjwtdemo.bean.User;
import com.oss.jwtdemo.ossjwtdemo.service.UserService;
import com.oss.jwtdemo.ossjwtdemo.utils.JWTResponseData;
import com.oss.jwtdemo.ossjwtdemo.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class UserController {


    @Autowired
    UserService userService;

    //登录
    @PostMapping("/login")
    @IgnoreToken //忽略验证token
    public JWTResponseData login(@RequestBody User user){
        JWTResponseData responseData  = new JWTResponseData();
        String md5Password = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        // 认证用户信息
        User userForBase=userService.findUser(user);
        if(userForBase==null){
            responseData.setCode(111111);
            responseData.setMsg("登录失败,用户不存在");
            responseData.setToken(null);
            responseData.setData(null);
            return responseData;
        }else {
            if (!userForBase.getPassword().equals(md5Password)) {
                responseData.setCode(222222);
                responseData.setMsg("登录失败,密码错误");
                return responseData;
            } else {
                String jwtToken = JWTUtils.createJWT(UUID.randomUUID().toString(), "xxx-oss-jwt",
                        JWTUtils.generalSubject(userForBase), 11111 * 60 * 1000);
                responseData.setCode(200);
                responseData.setData(null);
                responseData.setMsg("登录成功");
                responseData.setToken(jwtToken);
            }
        }
        return responseData;
    }

    @UserLoginToken
    @GetMapping("/getMessage")
    public JWTResponseData getMessage(){
        JWTResponseData responseData  = new JWTResponseData();
        responseData.setCode(200);
        responseData.setMsg("你已通过验证");
        return responseData;
    }

}
