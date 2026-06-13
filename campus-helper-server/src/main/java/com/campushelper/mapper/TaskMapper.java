package com.campushelper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campushelper.dto.TaskQueryReq;
import com.campushelper.dto.TaskVO;
import com.campushelper.entity.Task;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TaskMapper extends BaseMapper<Task> {

    IPage<TaskVO> selectTaskVOPage(IPage<?> page, @Param("req") TaskQueryReq req);

    TaskVO selectTaskVOById(@Param("id") Long id);

    List<Map<String, Object>> selectRecentTasksWithPublisher(@Param("limit") int limit);
}