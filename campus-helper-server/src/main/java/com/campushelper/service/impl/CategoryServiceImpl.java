package com.campushelper.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campushelper.common.BusinessException;
import com.campushelper.entity.Category;
import com.campushelper.mapper.CategoryMapper;
import com.campushelper.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<Category> getTree() {
        return buildTree(list());
    }

    @Override
    public List<Category> getActiveTree() {
        return buildTree(lambdaQuery().eq(Category::getStatus, 1).list());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCategory(Category category) {
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        if (category.getSort() == null) {
            category.setSort(0);
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        save(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(Category category) {
        Category exist = getById(category.getId());
        if (exist == null || exist.getDeleted() == 1) {
            throw new BusinessException("分类不存在");
        }
        // 防止将自己设为父分类
        if (category.getParentId() != null && category.getParentId().equals(category.getId())) {
            throw new BusinessException("不能将自己设为父分类");
        }
        BeanUtil.copyProperties(category, exist, "id", "createTime");
        updateById(exist);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long id) {
        Long childCount = lambdaQuery()
                .eq(Category::getParentId, id)
                .count();
        if (childCount > 0) {
            throw new BusinessException("请先删除子分类");
        }
        removeById(id);
    }

    /**
     * 构建树形结构
     */
    private List<Category> buildTree(List<Category> all) {
        Map<Long, List<Category>> parentMap = all.stream()
                .filter(c -> c.getParentId() != null && c.getParentId() != 0)
                .collect(Collectors.groupingBy(Category::getParentId));

        List<Category> roots = all.stream()
                .filter(c -> c.getParentId() == null || c.getParentId() == 0)
                .peek(root -> root.setChildren(parentMap.getOrDefault(root.getId(), new ArrayList<>())))
                .collect(Collectors.toList());

        // 填充子节点的 children（支持多级）
        for (List<Category> children : parentMap.values()) {
            for (Category child : children) {
                child.setChildren(parentMap.getOrDefault(child.getId(), new ArrayList<>()));
            }
        }
        return roots;
    }
}
