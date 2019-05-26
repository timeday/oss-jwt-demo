package com.oss.jwtdemo.ossjwtdemo.utils;

import com.oss.jwtdemo.ossjwtdemo.bean.User;
import lombok.Data;

/**
 * 作为subject数据使用，也就是payload中保存的public claims
 * 其中不包含任何敏感数据
 * 开发中建议使用使用实体类型
 */
@Data
public class JWTSubject {

    private User user;

}
