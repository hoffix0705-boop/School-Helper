package com.campushelper.common;

import cn.hutool.crypto.digest.BCrypt;

/**
 * 密码加密工具（基于 Hutool BCrypt）
 */
public class PasswordEncoder {

    /**
     * 加密密码
     */
    public static String encode(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    /**
     * 校验密码
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }
}
