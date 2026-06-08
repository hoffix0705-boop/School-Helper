package com.campushelper.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.campushelper.common.PageResult;
import com.campushelper.common.R;
import com.campushelper.dto.TaskCreateReq;
import com.campushelper.dto.TaskQueryReq;
import com.campushelper.dto.TaskUpdateReq;
import com.campushelper.dto.TaskVO;
import com.campushelper.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public R<TaskVO> create(@Valid @RequestBody TaskCreateReq req) {
        long userId = StpUtil.getLoginIdAsLong();
        return R.ok(taskService.createTask(userId, req));
    }

    @GetMapping
    public R<PageResult<TaskVO>> list(TaskQueryReq req) {
        return R.ok(PageResult.of(taskService.listTasks(req)));
    }

    @GetMapping("/{id}")
    public R<TaskVO> detail(@PathVariable Long id) {
        return R.ok(taskService.getTaskDetail(id));
    }

    @PutMapping("/{id}")
    public R<TaskVO> update(@PathVariable Long id, @Valid @RequestBody TaskUpdateReq req) {
        long userId = StpUtil.getLoginIdAsLong();
        return R.ok(taskService.updateTask(id, userId, req));
    }

    @DeleteMapping("/{id}")
    public R<Void> cancel(@PathVariable Long id) {
        long userId = StpUtil.getLoginIdAsLong();
        taskService.cancelTask(id, userId);
        return R.ok();
    }
}
