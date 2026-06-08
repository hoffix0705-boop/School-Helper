package com.campushelper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campushelper.entity.TaskAccept;

public interface TaskAcceptService extends IService<TaskAccept> {

    /**
     * 接单
     */
    void accept(Long taskId, Long userId);

    /**
     * 完成任务（接单人调用）
     */
    void complete(Long taskId, Long userId);

    /**
     * 获取当前任务的接单记录
     */
    TaskAccept getByTaskId(Long taskId);
}
