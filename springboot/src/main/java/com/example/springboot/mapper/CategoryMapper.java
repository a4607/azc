package com.example.springboot.mapper;

import com.example.springboot.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CategoryMapper {
  List<Category> findByUsername(String username);

  int insert(Category category);

  int update(Category category);

  int delete(@Param("id") Integer id);

  Integer countNotesByCategoryIdAndUsername(@Param("categoryId") Integer categoryId,
      @Param("username") String username);
}