package com.example.springboot.service;

import com.example.springboot.entity.User;

public interface UserService {
  String login(String username, String password);

  void register(User user);

  User getCurrentUser();

  void updateUser(User user);

  User findById(Integer id);
}