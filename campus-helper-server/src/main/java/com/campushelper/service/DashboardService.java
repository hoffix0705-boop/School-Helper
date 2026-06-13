package com.campushelper.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campushelper.entity.Task;
import com.campushelper.entity.User;
import com.campushelper.entity.Category;
import com.campushelper.mapper.TaskMapper;
import com.campushelper.mapper.UserMapper;
import com.campushelper.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final TaskMapper taskMapper;
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;

    public DashboardService(TaskMapper taskMapper, UserMapper userMapper, CategoryMapper categoryMapper) {
        this.taskMapper = taskMapper;
        this.userMapper = userMapper;
        this.categoryMapper = categoryMapper;
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();

        long totalTasks = taskMapper.selectCount(new LambdaQueryWrapper<Task>().eq(Task::getDeleted, 0));
        long pendingTasks = taskMapper.selectCount(new LambdaQueryWrapper<Task>().eq(Task::getDeleted, 0).eq(Task::getStatus, 0));
        long inProgressTasks = taskMapper.selectCount(new LambdaQueryWrapper<Task>().eq(Task::getDeleted, 0).eq(Task::getStatus, 1));
        long completedTasks = taskMapper.selectCount(new LambdaQueryWrapper<Task>().eq(Task::getDeleted, 0).eq(Task::getStatus, 2));

        long totalUsers = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getDeleted, 0));
        long totalCategories = categoryMapper.selectCount(new LambdaQueryWrapper<Category>().eq(Category::getDeleted, 0));

        stats.put("totalTasks", totalTasks);
        stats.put("pendingTasks", pendingTasks);
        stats.put("inProgressTasks", inProgressTasks);
        stats.put("completedTasks", completedTasks);
        stats.put("totalUsers", totalUsers);
        stats.put("totalCategories", totalCategories);

        return stats;
    }

    public List<Map<String, Object>> getRecentTasks(int limit) {
        return taskMapper.selectRecentTasksWithPublisher(limit);
    }
}