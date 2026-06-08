package com.campushelper.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("`user`")
public class User extends BaseEntity {

    private String username;
    private String password;
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
}
