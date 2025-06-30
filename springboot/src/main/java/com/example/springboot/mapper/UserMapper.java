package com.example.springboot.mapper;

import com.example.springboot.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
  User findByUsername(String username);

  User findById(Integer id);

  int insert(User user);

  int update(User user);

  int updatePassword(@Param("id") Integer id, @Param("password") String password);
}