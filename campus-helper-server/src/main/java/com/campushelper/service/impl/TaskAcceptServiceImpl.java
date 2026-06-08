package com.campushelper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campushelper.common.BusinessException;
import com.campushelper.entity.Task;
import com.campushelper.entity.TaskAccept;
import com.campushelper.mapper.TaskAcceptMapper;
import com.campushelper.service.TaskAcceptService;
import com.campushelper.service.TaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TaskAcceptServiceImpl extends ServiceImpl<TaskAcceptMapper, TaskAccept> implements TaskAcceptService {

    private final TaskService taskService;

    public TaskAcceptServiceImpl(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void accept(Long taskId, Long userId) {
        Task task = taskService.getById(taskId);
        if (task == null || task.getDeleted() == 1) {
            throw new BusinessException("任务不存在");
        }
        if (task.getUserId().equals(userId)) {
            throw new BusinessException("不能接自己的任务");
        }
        if (task.getStatus() != 0) {
            throw new BusinessException("该任务已被接走或已结束");
        }

        // 检查是否已接单
        Long count = lambdaQuery()
                .eq(TaskAccept::getTaskId, taskId)
                .eq(TaskAccept::getUserId, userId)
                .ne(TaskAccept::getStatus, 2)
                .count();
        if (count > 0) {
            throw new BusinessException("您已接过该任务");
        }

        // 创建接单记录
        TaskAccept accept = new TaskAccept();
        accept.setTaskId(taskId);
        accept.setUserId(userId);
        accept.setStatus(0); // 进行中
        accept.setAcceptTime(LocalDateTime.now());
        save(accept);

        // 更新任务状态为进行中
        task.setStatus(1);
        taskService.updateById(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void complete(Long taskId, Long userId) {
        TaskAccept accept = getOne(new LambdaQueryWrapper<TaskAccept>()
                .eq(TaskAccept::getTaskId, taskId)
                .eq(TaskAccept::getUserId, userId)
                .eq(TaskAccept::getStatus, 0)
                .last("LIMIT 1"));
        if (accept == null) {
            throw new BusinessException("未找到进行中的接单记录");
        }

        accept.setStatus(1); // 已完成
        accept.setCompleteTime(LocalDateTime.now());
        updateById(accept);

        // 更新任务状态为已完成
        Task task = taskService.getById(taskId);
        task.setStatus(2);
        taskService.updateById(task);
    }

    @Override
    public TaskAccept getByTaskId(Long taskId) {
        return getOne(new LambdaQueryWrapper<TaskAccept>()
                .eq(TaskAccept::getTaskId, taskId)
                .orderByAsc(TaskAccept::getCreateTime)
                .last("LIMIT 1"));
    }
}
