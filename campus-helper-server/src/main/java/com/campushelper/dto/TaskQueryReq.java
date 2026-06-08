package com.campushelper.dto;

import lombok.Data;

/**
 * 任务列表查询请求
 */
@Data
public class TaskQueryReq {

    private long page = 1;
    private long pageSize = 10;

    private String keyword;
    private Long categoryId;
    private Integer status;
    private Long userId;
}
