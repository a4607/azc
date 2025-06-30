package com.example.springboot.controller;

import com.example.springboot.entity.User;
import com.example.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
  @Autowired
  private UserService userService;

  @PostMapping("/login")
  public Map<String, Object> login(@RequestBody Map<String, String> loginForm) {
    String token = userService.login(loginForm.get("username"), loginForm.get("password"));
    Map<String, Object> result = new HashMap<>();
    result.put("code", 200);
    result.put("message", "登录成功");
    result.put("data", new HashMap<String, Object>() {
      {
        put("token", token);
      }
    });
    return result;
  }

  @PostMapping("/register")
  public Map<String, Object> register(@RequestBody User user) {
    userService.register(user);
    Map<String, Object> result = new HashMap<>();
    result.put("code", 200);
    result.put("message", "注册成功");
    return result;
  }

  @GetMapping("/current")
  public Map<String, Object> getCurrentUser() {
    User user = userService.getCurrentUser();
    Map<String, Object> result = new HashMap<>();
    result.put("code", 200);
    result.put("message", "获取成功");
    result.put("data", user);
    return result;
  }

  @PutMapping("/current")
  public Map<String, Object> updateUser(@RequestBody User user) {
    userService.updateUser(user);
    Map<String, Object> result = new HashMap<>();
    result.put("code", 200);
    result.put("message", "更新成功");
    return result;
  }

  @PostMapping("/avatar")
  public Map<String, Object> uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
    Map<String, Object> result = new HashMap<>();
    // 校验类型
    String contentType = file.getContentType();
    if (contentType == null || !(contentType.equals("image/jpeg") || contentType.equals("image/png")
        || contentType.equals("image/gif") || contentType.equals("image/webp"))) {
      result.put("code", 400);
      result.put("message", "仅支持jpg/png/gif/webp格式图片");
      return result;
    }
    // 校验大小（2MB）
    if (file.getSize() > 2 * 1024 * 1024) {
      result.put("code", 400);
      result.put("message", "图片大小不能超过2MB");
      return result;
    }
    // 存入数据库
    User user = userService.getCurrentUser();
    user.setAvatarBlob(file.getBytes());
    user.setAvatarType(contentType);
    userService.updateUser(user); // 覆盖旧头像
    result.put("code", 200);
    result.put("message", "上传成功");
    result.put("data", "/api/users/avatar/" + user.getId() + "?t=" + System.currentTimeMillis());
    return result;
  }

  @GetMapping("/avatar/{userId}")
  public void getAvatar(@PathVariable Integer userId, HttpServletResponse response) throws IOException {
    User user = userService.findById(userId);
    if (user == null || user.getAvatarBlob() == null) {
      response.setStatus(404);
      return;
    }
    response.setContentType(user.getAvatarType());
    response.getOutputStream().write(user.getAvatarBlob());
  }
}