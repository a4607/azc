package com.example.springboot.service;

import com.example.springboot.entity.Category;
import java.util.List;

public interface CategoryService {
  List<Category> getCategories();

  void createCategory(Category category);

  void updateCategory(Category category);

  void deleteCategory(Integer id);
}