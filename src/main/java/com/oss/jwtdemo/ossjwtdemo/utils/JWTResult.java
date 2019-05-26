package com.oss.jwtdemo.ossjwtdemo.utils;

import io.jsonwebtoken.Claims;
import lombok.Data;

@Data
public class JWTResult {

    private int erroCode;

    private boolean success;

    private Claims claims;

}
