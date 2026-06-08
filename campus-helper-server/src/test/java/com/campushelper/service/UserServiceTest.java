package com.campushelper.service;

import com.campushelper.common.BusinessException;
import com.campushelper.dto.UserQueryReq;
import com.campushelper.dto.UserVO;
import com.campushelper.entity.User;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Rollback
class UserServiceTest {

    @Autowired
    private UserService userService;

    private final String prefix = "u_" + System.currentTimeMillis() + "_";

    private String u(String name) {
        return prefix + name;
    }

    @BeforeEach
    void setUp() {
        // 每个测试方法前清理旧数据，避免相互影响
        userService.lambdaUpdate().remove();
    }

    @Test
    void register_shouldCreateUser() {
        User user = new User();
        user.setUsername(u("create"));
        userService.register(user, "123456");

        User found = userService.findByUsername(u("create"));
        assertNotNull(found);
        assertEquals(u("create"), found.getUsername());
        assertNotNull(found.getPassword());
        assertTrue(found.getPassword().startsWith("$2a$"));
    }

    @Test
    void register_shouldThrowOnDuplicate() {
        User user = new User();
        user.setUsername(u("dup"));
        userService.register(user, "123456");
        assertThrows(BusinessException.class, () -> userService.register(user, "123456"));
    }

    @Test
    void listUsers_shouldReturnPage() {
        for (int i = 0; i < 5; i++) {
            User u = new User();
            u.setUsername(u("list_" + i));
            userService.register(u, "123456");
        }

        UserQueryReq req = new UserQueryReq();
        req.setPage(1);
        req.setPageSize(2);
        IPage<UserVO> result = userService.listUsers(req);

        assertEquals(5, result.getTotal());
        assertEquals(2, result.getRecords().size());
    }

    @Test
    void setUserStatus_shouldUpdateStatus() {
        User user = new User();
        user.setUsername(u("status"));
        userService.register(user, "123456");

        userService.setUserStatus(user.getId(), 1);
        User updated = userService.getById(user.getId());
        assertEquals(1, updated.getStatus());
    }
}