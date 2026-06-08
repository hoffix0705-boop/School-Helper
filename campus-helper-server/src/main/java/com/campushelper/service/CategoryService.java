package com.campushelper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campushelper.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {

    /**
     * 获取分类树
     */
    List<Category> getTree();

    /**
     * 获取启用的分类树
     */
    List<Category> getActiveTree();

    /**
     * 新增分类
     */
    void addCategory(Category category);

    /**
     * 更新分类
     */
    void updateCategory(Category category);

    /**
     * 删除分类（有子类则抛异常）
     */
    void deleteCategory(Long id);
}
