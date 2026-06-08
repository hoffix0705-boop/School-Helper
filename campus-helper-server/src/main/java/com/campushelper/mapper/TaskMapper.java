package com.campushelper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campushelper.dto.TaskQueryReq;
import com.campushelper.dto.TaskVO;
import com.campushelper.entity.Task;
import org.apache.ibatis.annotations.Param;

public interface TaskMapper extends BaseMapper<Task> {

    /**
     * 分页查询任务列表（联表）
     */
    IPage<TaskVO> selectTaskVOPage(IPage<?> page, @Param("req") TaskQueryReq req);

    /**
     * 查询任务详情（联表）
     */
    TaskVO selectTaskVOById(@Param("id") Long id);
}
