package com.campushelper.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campushelper.dto.UserQueryReq;
import com.campushelper.dto.UserVO;
import com.campushelper.entity.User;

import java.util.List;

public interface UserService extends IService<User> {

    User findByUsername(String username);

    void register(User user, String rawPassword);

    IPage<UserVO> listUsers(UserQueryReq req);

    void setUserStatus(Long id, Integer status);
}
