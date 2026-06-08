package com.campushelper.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campushelper.dto.TaskCreateReq;
import com.campushelper.dto.TaskQueryReq;
import com.campushelper.dto.TaskUpdateReq;
import com.campushelper.dto.TaskVO;
import com.campushelper.entity.Task;

public interface TaskService extends IService<Task> {

    /**
     * 发布任务
     */
    TaskVO createTask(Long userId, TaskCreateReq req);

    /**
     * 分页查询任务列表
     */
    IPage<TaskVO> listTasks(TaskQueryReq req);

    /**
     * 查询任务详情
     */
    TaskVO getTaskDetail(Long id);

    /**
     * 更新任务
     */
    TaskVO updateTask(Long id, Long userId, TaskUpdateReq req);

    /**
     * 取消任务
     */
    void cancelTask(Long id, Long userId);
}
