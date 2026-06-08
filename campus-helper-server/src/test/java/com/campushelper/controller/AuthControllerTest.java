package com.campushelper.controller;

import com.campushelper.common.R;
import com.campushelper.entity.User;
import com.campushelper.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private TestRestTemplate rest;
    @Autowired
    private UserService userService;

    private final String user = "auth_" + System.currentTimeMillis();

    @BeforeEach
    void setUp() {
        // 清理 + 重建测试用户
        userService.lambdaUpdate().remove();
        User u = new User();
        u.setUsername(user);
        userService.register(u, "123456");
    }

    @SuppressWarnings("unchecked")
    @Test
    void login_shouldReturnToken() {
        var resp = rest.postForEntity("/auth/login",
                Map.of("username", user, "password", "123456"), R.class);
        assertEquals(200, resp.getStatusCode().value());
        R result = resp.getBody();
        assertNotNull(result);
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertNotNull(data.get("token"));
        assertNotNull(data.get("user"));
    }

    @Test
    void login_shouldFailWithWrongPassword() {
        var resp = rest.postForEntity("/auth/login",
                Map.of("username", user, "password", "wrong"), R.class);
        R result = resp.getBody();
        assertNotNull(result);
        assertEquals(500, result.getCode());
    }

    @SuppressWarnings("unchecked")
    @Test
    void me_shouldReturnUserInfo() {
        var loginResp = rest.postForEntity("/auth/login",
                Map.of("username", user, "password", "123456"), R.class);
        Map<String, Object> loginData = (Map<String, Object>) loginResp.getBody().getData();
        String token = (String) loginData.get("token");

        HttpHeaders headers = new HttpHeaders();
        headers.set("token", token);
        var resp = rest.exchange("/auth/me", HttpMethod.GET,
                new HttpEntity<>(headers), R.class);
        assertEquals(200, resp.getStatusCode().value());
        Map<String, Object> me = (Map<String, Object>) resp.getBody().getData();
        assertEquals(user, me.get("username"));
    }
}