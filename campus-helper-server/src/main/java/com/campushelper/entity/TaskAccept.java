package com.campushelper.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("task_accept")
public class TaskAccept extends BaseEntity {

    private Long taskId;
    private Long userId;
    private Integer status;
    private LocalDateTime acceptTime;
    private LocalDateTime completeTime;
}
