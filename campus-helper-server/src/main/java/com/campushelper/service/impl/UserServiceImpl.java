package com.campushelper.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campushelper.common.BusinessException;
import com.campushelper.common.PasswordEncoder;
import com.campushelper.dto.UserQueryReq;
import com.campushelper.dto.UserVO;
import com.campushelper.entity.User;
import com.campushelper.mapper.UserMapper;
import com.campushelper.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User findByUsername(String username) {
        return getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .last("LIMIT 1"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(User user, String rawPassword) {
        if (findByUsername(user.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }
        user.setPassword(PasswordEncoder.encode(rawPassword));
        save(user);
    }

    @Override
    public IPage<UserVO> listUsers(UserQueryReq req) {
        Page<User> page = page(new Page<>(req.getPage(), req.getPageSize()),
                new LambdaQueryWrapper<User>()
                        .like(req.getKeyword() != null, User::getUsername, req.getKeyword())
                        .or(req.getKeyword() != null, w -> w
                                .like(req.getKeyword() != null, User::getNickname, req.getKeyword())
                                .or().like(req.getKeyword() != null, User::getPhone, req.getKeyword()))
                        .eq(req.getUserType() != null, User::getUserType, req.getUserType())
                        .eq(req.getStatus() != null, User::getStatus, req.getStatus())
                        .eq(req.getCollege() != null, User::getCollege, req.getCollege())
                        .orderByDesc(User::getCreateTime));

        IPage<UserVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream()
                .map(u -> {
                    UserVO vo = BeanUtil.copyProperties(u, UserVO.class);
                                return vo;
                })
                .collect(Collectors.toList()));
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setUserStatus(Long id, Integer status) {
        User user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        lambdaUpdate()
                .eq(User::getId, id)
                .set(User::getStatus, status)
                .update();
    }
}
