package com.oss.jwtdemo.ossjwtdemo.utils;

import lombok.Data;

@Data
public class JWTResponseData<T> {

    private Integer code;

    private T data;

    private String msg;

    private String token;

}
