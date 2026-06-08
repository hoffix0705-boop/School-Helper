package com.campushelper.dto;

import lombok.Data;

@Data
public class UserQueryReq {

    private long page = 1;
    private long pageSize = 10;

    private String keyword;
    private Integer userType;
    private Integer status;
    private String college;
}
