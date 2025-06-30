package com.example.springboot.service.impl;

import com.example.springboot.entity.User;
import com.example.springboot.mapper.UserMapper;
import com.example.springboot.service.UserService;
import com.example.springboot.util.JwtUtil;
import com.example.springboot.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
  @Autowired
  private UserMapper userMapper;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private JwtUtil jwtUtil;

  @Override
  public String login(String username, String password) {
    User user = userMapper.findByUsername(username);
    if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
      throw new RuntimeException("用户名或密码错误");
    }
    return jwtUtil.generateToken(username);
  }

  @Override
  public void register(User user) {
    if (userMapper.findByUsername(user.getUsername()) != null) {
      throw new RuntimeException("用户名已存在");
    }
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    userMapper.insert(user);
  }

  @Override
  public User getCurrentUser() {
    String username = SecurityUtil.getCurrentUsername();
    return userMapper.findByUsername(username);
  }

  @Override
  public void updateUser(User user) {
    User existingUser = getCurrentUser();
    if (user.getUsername() != null)
      existingUser.setUsername(user.getUsername());
    if (user.getSignature() != null)
      existingUser.setSignature(user.getSignature());
    if (user.getGender() != null)
      existingUser.setGender(user.getGender());
    if (user.getBirthday() != null)
      existingUser.setBirthday(user.getBirthday());
    userMapper.update(existingUser);
  }

  @Override
  public User findById(Integer id) {
    return userMapper.findById(id);
  }
}