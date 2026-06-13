package com.campushelper.security;

import cn.dev33.satoken.stp.StpInterface;
import com.campushelper.entity.User;
import com.campushelper.service.UserService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Sa-Token 权限接口 — 自定义权限加载
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    private final UserService userService;

    public StpInterfaceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return new ArrayList<>();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        User user = userService.getById(Long.valueOf(loginId.toString()));
        List<String> roles = new ArrayList<>();
        if (user != null) {
            // userType: 0=管理员, 1=普通用户
            roles.add(user.getUserType() == 0 ? "admin" : "user");
        }
        return roles;
    }
}
