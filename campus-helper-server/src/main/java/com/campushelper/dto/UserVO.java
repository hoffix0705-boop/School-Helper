package com.campushelper.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVO {

    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private String phone;
    private String email;
    private Integer gender;
    private Integer userType;
    private Integer status;
    private String realName;
    private String studentId;
    private String college;
    private String major;
    private String grade;
    private Integer creditScore;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
