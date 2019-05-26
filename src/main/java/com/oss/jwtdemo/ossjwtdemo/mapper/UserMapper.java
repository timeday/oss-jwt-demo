package com.oss.jwtdemo.ossjwtdemo.mapper;

import com.oss.jwtdemo.ossjwtdemo.bean.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User findUser(User user);

    User findUserById(String id);
}
