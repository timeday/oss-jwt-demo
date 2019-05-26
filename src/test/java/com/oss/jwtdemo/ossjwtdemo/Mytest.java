package com.oss.jwtdemo.ossjwtdemo;

import org.springframework.util.DigestUtils;

public class Mytest {
    public static void main(String[] args) {
        String s = DigestUtils.md5DigestAsHex("123456".getBytes());
        System.out.println(s);
    }
}
