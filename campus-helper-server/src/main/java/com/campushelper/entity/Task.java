package com.campushelper.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("task")
public class Task extends BaseEntity {

    private String title;
    private String description;
    private Long categoryId;
    private Long userId;
    private BigDecimal price;
    private String contactPhone;
    private String address;
    private Integer status;
    private Integer urgent;
    private LocalDateTime deadline;
    private Integer viewCount;
}
