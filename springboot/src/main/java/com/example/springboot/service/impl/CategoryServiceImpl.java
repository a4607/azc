package com.example.springboot.service.impl;

import com.example.springboot.entity.Category;
import com.example.springboot.mapper.CategoryMapper;
import com.example.springboot.service.CategoryService;
import com.example.springboot.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
  @Autowired
  private CategoryMapper categoryMapper;

  @Override
  public List<Category> getCategories() {
    String username = SecurityUtil.getCurrentUsername();
    List<Category> categories = categoryMapper.findByUsername(username);
    for (Category category : categories) {
      try {
        Integer noteCount = categoryMapper.countNotesByCategoryIdAndUsername(category.getId(), username);
        category.setNoteCount(noteCount != null ? noteCount : 0);
      } catch (Exception e) {
        System.err.println("获取分类 " + category.getName() + " 的笔记数量失败: " + e.getMessage());
        category.setNoteCount(0); // 出现错误时，将笔记数量设置为0
      }
    }
    return categories;
  }

  @Override
  public void createCategory(Category category) {
    category.setUsername(SecurityUtil.getCurrentUsername());
    categoryMapper.insert(category);
  }

  @Override
  public void updateCategory(Category category) {
    category.setUsername(SecurityUtil.getCurrentUsername());
    categoryMapper.update(category);
  }

  @Override
  public void deleteCategory(Integer id) {
    categoryMapper.delete(id);
  }
}