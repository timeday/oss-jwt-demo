package com.oss.jwtdemo.ossjwtdemo.service;

import com.oss.jwtdemo.ossjwtdemo.bean.User;
import com.oss.jwtdemo.ossjwtdemo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    public User findUser(User user) {
        //对密码进行 md5 加密 Spring自带的加密工具类
        String md5Password = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        user.setPassword(md5Password);
        return userMapper.findUser(user);
    }

    public User findUserById(String id) {
        return userMapper.findUserById(id);
    }
}
