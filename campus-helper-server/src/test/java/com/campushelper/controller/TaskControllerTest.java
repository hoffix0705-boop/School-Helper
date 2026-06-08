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
class TaskControllerTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private UserService userService;

    private HttpHeaders authHeaders;

    private final String user = "tc_" + System.currentTimeMillis();

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        userService.lambdaUpdate().remove();

        User u = new User();
        u.setUsername(user);
        u.setUserType(0);
        u.setNickname("测试管理员");
        userService.register(u, "123456");

        var loginResp = rest.postForEntity("/auth/login",
                Map.of("username", user, "password", "123456"), R.class);
        assertEquals(200, loginResp.getStatusCode().value());
        R result = loginResp.getBody();
        Map<String, Object> data = (Map<String, Object>) result.getData();
        String token = (String) data.get("token");

        authHeaders = new HttpHeaders();
        authHeaders.set("token", token);
    }

    @SuppressWarnings("unchecked")
    @Test
    void createAndListTask() {
        var body = Map.of("title", "集成测试任务", "price", 25.00);
        var createResp = rest.exchange("/tasks", HttpMethod.POST,
                new HttpEntity<>(body, authHeaders), R.class);
        assertEquals(200, createResp.getStatusCode().value());
        var task = (Map<String, Object>) createResp.getBody().getData();
        assertNotNull(task.get("id"));

        var listResp = rest.exchange("/tasks?page=1&pageSize=10",
                HttpMethod.GET, new HttpEntity<>(authHeaders), R.class);
        var page = (Map<String, Object>) listResp.getBody().getData();
        assertEquals(1, ((Number) page.get("total")).longValue());
    }

    @Test
    void createTask_shouldFailWithoutAuth() {
        var headers = new HttpHeaders();
        var resp = rest.exchange("/tasks", HttpMethod.GET,
                new HttpEntity<>(headers), String.class);
        assertEquals(401, resp.getStatusCode().value());
    }
}