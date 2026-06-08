package com.campushelper.controller;

import com.campushelper.common.R;
import com.campushelper.entity.Category;
import com.campushelper.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/tree")
    public R<List<Category>> tree() {
        return R.ok(categoryService.getTree());
    }

    @GetMapping("/active-tree")
    public R<List<Category>> activeTree() {
        return R.ok(categoryService.getActiveTree());
    }

    @PostMapping
    public R<Void> create(@Valid @RequestBody Category category) {
        categoryService.addCategory(category);
        return R.ok();
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        categoryService.updateCategory(category);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return R.ok();
    }
}