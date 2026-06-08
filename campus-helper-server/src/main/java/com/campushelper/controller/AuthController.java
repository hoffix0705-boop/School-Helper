package com.campushelper.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.campushelper.common.BusinessException;
import com.campushelper.common.PasswordEncoder;
import com.campushelper.common.R;
import com.campushelper.entity.User;
import com.campushelper.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public R<Map<String, Object>> login(@Valid @RequestBody LoginReq req) {
        User user = userService.findByUsername(req.getUsername());
        if (user == null) {
            return R.fail("用户名或密码错误");
        }
        if (user.getStatus() == 1) {
            return R.fail("账号已被禁用");
        }
        if (!PasswordEncoder.matches(req.getPassword(), user.getPassword())) {
            return R.fail("用户名或密码错误");
        }

        StpUtil.login(user.getId());
        String token = StpUtil.getTokenValue();

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", buildUserVO(user));
        return R.ok(data);
    }

    @PostMapping("/register")
    public R<Void> register(@Valid @RequestBody RegisterReq req) {
        User user = new User();
        user.setUsername(req.getUsername());
        user.setNickname(req.getNickname());
        user.setPhone(req.getPhone());
        userService.register(user, req.getPassword());
        return R.ok();
    }

    @PostMapping("/logout")
    public R<Void> logout() {
        StpUtil.logout();
        return R.ok();
    }

    @GetMapping("/me")
    public R<Map<String, Object>> me() {
        long userId = StpUtil.getLoginIdAsLong();
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return R.ok(buildUserVO(user));
    }

    private Map<String, Object> buildUserVO(User user) {
        Map<String, Object> vo = new HashMap<>();
        vo.put("id", user.getId());
        vo.put("username", user.getUsername());
        vo.put("nickname", user.getNickname());
        vo.put("avatar", user.getAvatar());
        vo.put("phone", user.getPhone());
        vo.put("email", user.getEmail());
        vo.put("gender", user.getGender());
        vo.put("userType", user.getUserType());
        vo.put("college", user.getCollege());
        return vo;
    }

    @Data
    static class LoginReq {
        @NotBlank(message = "用户名不能为空")
        private String username;
        @NotBlank(message = "密码不能为空")
        private String password;
    }

    @Data
    static class RegisterReq {
        @NotBlank(message = "用户名不能为空")
        private String username;
        @NotBlank(message = "密码不能为空")
        private String password;
        private String nickname;
        private String phone;
    }
}