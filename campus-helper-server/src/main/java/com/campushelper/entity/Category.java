package com.campushelper.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("category")
public class Category extends BaseEntity {

    @NotBlank(message = "分类名称不能为空")
    private String name;
    private Long parentId;
    private Integer sort;
    private String icon;
    private Integer status;

    /** 子分类（非数据库字段） */
    @TableField(exist = false)
    private List<Category> children;
}
