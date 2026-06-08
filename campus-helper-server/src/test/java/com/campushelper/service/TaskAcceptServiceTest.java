package com.campushelper.service;

import com.campushelper.common.BusinessException;
import com.campushelper.dto.TaskCreateReq;
import com.campushelper.dto.TaskVO;
import com.campushelper.entity.TaskAccept;
import com.campushelper.entity.User;
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
class TaskAcceptServiceTest {

    @Autowired
    private TaskAcceptService taskAcceptService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private UserService userService;

    private Long publisherId, acceptorId, taskId;
    private final String prefix = "ta_" + System.currentTimeMillis() + "_";

    @BeforeEach
    void setUp() {
        userService.lambdaUpdate().remove();
        taskService.lambdaUpdate().remove();

        User pub = new User();
        pub.setUsername(prefix + "pub");
        userService.register(pub, "123456");
        publisherId = pub.getId();

        User acc = new User();
        acc.setUsername(prefix + "acc");
        userService.register(acc, "123456");
        acceptorId = acc.getId();

        TaskCreateReq req = new TaskCreateReq();
        req.setTitle(prefix + "可接任务");
        req.setPrice(new BigDecimal("30"));
        TaskVO vo = taskService.createTask(publisherId, req);
        taskId = vo.getId();
    }

    @Test
    void accept_shouldCreateRecordAndUpdateTaskStatus() {
        taskAcceptService.accept(taskId, acceptorId);
        TaskAccept accept = taskAcceptService.getByTaskId(taskId);
        assertNotNull(accept);
        assertEquals(acceptorId, accept.getUserId());
        assertEquals(1, taskService.getTaskDetail(taskId).getStatus());
    }

    @Test
    void accept_shouldThrowIfPublisherAcceptsOwnTask() {
        assertThrows(BusinessException.class, () -> taskAcceptService.accept(taskId, publisherId));
    }

    @Test
    void complete_shouldUpdateTaskStatus() {
        taskAcceptService.accept(taskId, acceptorId);
        taskAcceptService.complete(taskId, acceptorId);
        assertEquals(2, taskService.getTaskDetail(taskId).getStatus());
    }
}