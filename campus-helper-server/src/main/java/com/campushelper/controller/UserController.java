package com.campushelper.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.campushelper.common.PageResult;
import com.campushelper.common.R;
import com.campushelper.dto.UserQueryReq;
import com.campushelper.dto.UserVO;
import com.campushelper.entity.User;
import com.campushelper.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @SaCheckRole("admin")
    public R<PageResult<UserVO>> list(UserQueryReq req) {
        return R.ok(PageResult.of(userService.listUsers(req)));
    }

    @PutMapping("/{id}/status")
    @SaCheckRole("admin")
    public R<Void> setStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        userService.setUserStatus(id, body.get("status"));
        return R.ok();
    }

    @GetMapping("/me")
    public R<Map<String, Object>> me() {
        long userId = StpUtil.getLoginIdAsLong();
        User user = userService.getById(userId);
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
        vo.put("major", user.getMajor());
        vo.put("grade", user.getGrade());
        vo.put("creditScore", user.getCreditScore());
        return R.ok(vo);
    }
}