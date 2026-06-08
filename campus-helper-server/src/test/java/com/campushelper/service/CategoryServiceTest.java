package com.campushelper.service;

import com.campushelper.common.BusinessException;
import com.campushelper.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Rollback
class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryService.lambdaUpdate().remove();
    }

    @Test
    void addCategory_shouldCreate() {
        Category cat = new Category();
        cat.setName("跑腿");
        categoryService.addCategory(cat);
        assertNotNull(cat.getId());
    }

    @Test
    void getTree_shouldReturnTree() {
        Category parent = new Category();
        parent.setName("跑腿");
        categoryService.addCategory(parent);

        Category child = new Category();
        child.setName("取快递");
        child.setParentId(parent.getId());
        categoryService.addCategory(child);

        List<Category> tree = categoryService.getTree();
        assertEquals(1, tree.size());
        assertEquals(1, tree.get(0).getChildren().size());
    }

    @Test
    void deleteCategory_shouldThrowIfHasChildren() {
        Category parent = new Category();
        parent.setName("代课");
        categoryService.addCategory(parent);
        Category child = new Category();
        child.setName("代签到");
        child.setParentId(parent.getId());
        categoryService.addCategory(child);

        assertThrows(BusinessException.class, () -> categoryService.deleteCategory(parent.getId()));
    }

    @Test
    void deleteCategory_shouldDeleteLeaf() {
        Category cat = new Category();
        cat.setName("其他");
        categoryService.addCategory(cat);
        categoryService.deleteCategory(cat.getId());
        assertNull(categoryService.getById(cat.getId()));
    }
}