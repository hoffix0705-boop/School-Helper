package com.campushelper.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 更新任务请求
 */
@Data
public class TaskUpdateReq {

    private String title;
    private String description;
    private Long categoryId;
    private BigDecimal price;
    private String contactPhone;
    private String address;
    private Integer urgent;
    private LocalDateTime deadline;
}
