package com.campushelper.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 创建任务请求
 */
@Data
public class TaskCreateReq {

    @NotBlank(message = "任务标题不能为空")
    @Size(max = 128, message = "标题不超过128字符")
    private String title;

    private String description;

    private Long categoryId;

    @NotNull(message = "报酬不能为空")
    private BigDecimal price;

    private String contactPhone;

    private String address;

    private Integer urgent;

    private LocalDateTime deadline;
}
