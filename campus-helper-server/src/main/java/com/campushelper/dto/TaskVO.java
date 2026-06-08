package com.campushelper.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TaskVO {

    private Long id;
    private String title;
    private String description;
    private Long categoryId;
    private String categoryName;
    private Long userId;
    private String publisherNickname;
    private String publisherAvatar;
    private BigDecimal price;
    private String contactPhone;
    private String address;
    private Integer status;
    private Integer urgent;
    private LocalDateTime deadline;
    private Integer viewCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 接单人信息
    private Long acceptorId;
    private String acceptorNickname;
    private LocalDateTime acceptTime;
    private LocalDateTime completeTime;
}
