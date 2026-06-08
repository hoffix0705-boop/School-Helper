package com.campushelper.service;

import com.campushelper.common.BusinessException;
import com.campushelper.dto.TaskCreateReq;
import com.campushelper.dto.TaskQueryReq;
import com.campushelper.dto.TaskVO;
import com.campushelper.entity.User;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Rollback
class TaskServiceTest {

    @Autowired
    private TaskService taskService;
    @Autowired
    private UserService userService;

    private Long userId;
    private final String prefix = "ts_" + System.currentTimeMillis() + "_";

    @BeforeEach
    void setUp() {
        userService.lambdaUpdate().remove();
        taskService.lambdaUpdate().remove();
        User user = new User();
        user.setUsername(prefix + "publisher");
        userService.register(user, "123456");
        userId = user.getId();
    }

    private TaskCreateReq createReq() {
        TaskCreateReq req = new TaskCreateReq();
        req.setTitle(prefix + "测试任务");
        req.setDescription("测试描述");
        req.setPrice(new BigDecimal("50.00"));
        req.setContactPhone("13800138000");
        return req;
    }

    @Test
    void createTask_shouldCreateAndReturnVO() {
        TaskVO vo = taskService.createTask(userId, createReq());
        assertNotNull(vo.getId());
        assertEquals(prefix + "测试任务", vo.getTitle());
        assertEquals(0, vo.getStatus());
    }

    @Test
    void getTaskDetail_shouldIncrementViewCount() {
        TaskVO vo = taskService.createTask(userId, createReq());
        assertEquals(1, vo.getViewCount().intValue());
        TaskVO detail = taskService.getTaskDetail(vo.getId());
        assertEquals(2, detail.getViewCount().intValue());
    }

    @Test
    void cancelTask_shouldSetStatusCancelled() {
        TaskVO vo = taskService.createTask(userId, createReq());
        taskService.cancelTask(vo.getId(), userId);
        assertEquals(3, taskService.getTaskDetail(vo.getId()).getStatus());
    }

    @Test
    void cancelTask_shouldThrowIfNotOwner() {
        TaskVO vo = taskService.createTask(userId, createReq());
        assertThrows(BusinessException.class, () -> taskService.cancelTask(vo.getId(), 999L));
    }

    @Test
    void listTasks_shouldReturnPaginated() {
        for (int i = 0; i < 3; i++) {
            TaskCreateReq req = createReq();
            req.setTitle(prefix + "任务" + i);
            taskService.createTask(userId, req);
        }
        TaskQueryReq query = new TaskQueryReq();
        query.setPage(1);
        query.setPageSize(2);
        IPage<TaskVO> result = taskService.listTasks(query);
        assertEquals(3, result.getTotal());
        assertEquals(2, result.getRecords().size());
    }

    @Test
    void listTasks_shouldFilterByKeyword() {
        TaskCreateReq r1 = createReq();
        r1.setTitle(prefix + "取快递");
        taskService.createTask(userId, r1);
        TaskCreateReq r2 = createReq();
        r2.setTitle(prefix + "代买奶茶");
        taskService.createTask(userId, r2);

        TaskQueryReq query = new TaskQueryReq();
        query.setKeyword("快递");
        IPage<TaskVO> result = taskService.listTasks(query);
        assertEquals(1, result.getTotal());
    }
}