package com.example.springboot.controller;

import com.example.springboot.entity.Category;
import com.example.springboot.service.CategoryService;
import com.example.springboot.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
  @Autowired
  private CategoryService categoryService;

  @GetMapping
  public Result getCategories() {
    try {
      List<Category> categories = categoryService.getCategories();
      return Result.success(categories);
    } catch (Exception e) {
      return Result.error("获取分类列表失败");
    }
  }

  @PostMapping
  public Result createCategory(@RequestBody Category category) {
    try {
      categoryService.createCategory(category);
      return Result.success(category);
    } catch (Exception e) {
      return Result.error("创建分类失败");
    }
  }

  @PutMapping("/{id}")
  public Result updateCategory(@PathVariable Integer id, @RequestBody Category category) {
    try {
      category.setId(id);
      categoryService.updateCategory(category);
      return Result.success(category);
    } catch (Exception e) {
      return Result.error("更新分类失败");
    }
  }

  @DeleteMapping("/{id}")
  public Result deleteCategory(@PathVariable Integer id) {
    try {
      categoryService.deleteCategory(id);
      return Result.success(null);
    } catch (Exception e) {
      return Result.error("删除分类失败");
    }
  }
}