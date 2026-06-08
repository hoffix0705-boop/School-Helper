package com.campushelper.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.campushelper.common.R;
import com.campushelper.entity.TaskAccept;
import com.campushelper.service.TaskAcceptService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/tasks/{taskId}/accept")
public class TaskAcceptController {

    private final TaskAcceptService taskAcceptService;

    public TaskAcceptController(TaskAcceptService taskAcceptService) {
        this.taskAcceptService = taskAcceptService;
    }

    @PostMapping
    public R<Void> accept(@PathVariable Long taskId) {
        long userId = StpUtil.getLoginIdAsLong();
        taskAcceptService.accept(taskId, userId);
        return R.ok();
    }

    @PutMapping("/complete")
    public R<Void> complete(@PathVariable Long taskId) {
        long userId = StpUtil.getLoginIdAsLong();
        taskAcceptService.complete(taskId, userId);
        return R.ok();
    }

    @GetMapping
    public R<Map<String, Object>> getAcceptInfo(@PathVariable Long taskId) {
        TaskAccept accept = taskAcceptService.getByTaskId(taskId);
        if (accept == null) {
            return R.ok(new HashMap<>());
        }
        Map<String, Object> data = new HashMap<>();
        data.put("id", accept.getId());
        data.put("userId", accept.getUserId());
        data.put("status", accept.getStatus());
        data.put("acceptTime", accept.getAcceptTime());
        data.put("completeTime", accept.getCompleteTime());
        return R.ok(data);
    }
}