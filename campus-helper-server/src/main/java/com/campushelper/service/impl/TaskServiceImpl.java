package com.campushelper.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campushelper.common.BusinessException;
import com.campushelper.dto.TaskCreateReq;
import com.campushelper.dto.TaskQueryReq;
import com.campushelper.dto.TaskUpdateReq;
import com.campushelper.dto.TaskVO;
import com.campushelper.entity.Category;
import com.campushelper.entity.Task;
import com.campushelper.mapper.TaskMapper;
import com.campushelper.service.CategoryService;
import com.campushelper.service.TaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {

    private final CategoryService categoryService;

    public TaskServiceImpl(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskVO createTask(Long userId, TaskCreateReq req) {
        validateCategory(req.getCategoryId());

        Task task = BeanUtil.copyProperties(req, Task.class);
        task.setUserId(userId);
        task.setStatus(0);
        task.setViewCount(0);
        save(task);
        return getTaskDetail(task.getId());
    }

    @Override
    public IPage<TaskVO> listTasks(TaskQueryReq req) {
        Page<TaskVO> page = new Page<>(req.getPage(), req.getPageSize());
        return baseMapper.selectTaskVOPage(page, req);
    }

    @Override
    public TaskVO getTaskDetail(Long id) {
        TaskVO vo = baseMapper.selectTaskVOById(id);
        if (vo == null) {
            throw new BusinessException("任务不存在");
        }
        lambdaUpdate()
                .eq(Task::getId, id)
                .setSql("view_count = view_count + 1")
                .update();
        vo.setViewCount(vo.getViewCount() + 1);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskVO updateTask(Long id, Long userId, TaskUpdateReq req) {
        Task task = getById(id);
        if (task == null || task.getDeleted() == 1) {
            throw new BusinessException("任务不存在");
        }
        if (!task.getUserId().equals(userId)) {
            throw new BusinessException("只能编辑自己的任务");
        }
        if (task.getStatus() != 0) {
            throw new BusinessException("只能编辑待接单的任务");
        }
        if (req.getCategoryId() != null) {
            validateCategory(req.getCategoryId());
        }
        BeanUtil.copyProperties(req, task);
        updateById(task);
        return getTaskDetail(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelTask(Long id, Long userId) {
        Task task = getById(id);
        if (task == null || task.getDeleted() == 1) {
            throw new BusinessException("任务不存在");
        }
        if (!task.getUserId().equals(userId)) {
            throw new BusinessException("只能取消自己的任务");
        }
        if (task.getStatus() != 0) {
            throw new BusinessException("只能取消待接单的任务");
        }
        task.setStatus(3);
        updateById(task);
    }

    private void validateCategory(Long categoryId) {
        if (categoryId == null) return;
        Category cat = categoryService.getById(categoryId);
        if (cat == null || cat.getDeleted() == 1 || cat.getStatus() != 1) {
            throw new BusinessException("所选分类不存在或已禁用");
        }
    }
}
