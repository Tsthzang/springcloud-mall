package com.zang.cloud.mall.common.utils;

import com.zang.cloud.mall.common.common.Constant;
import org.apache.tomcat.util.codec.binary.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
    public static String getMD5Str(String value) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        String result = Base64.encodeBase64String(md5.digest((value + Constant.SALT).getBytes()));
        return result;
    }
}
