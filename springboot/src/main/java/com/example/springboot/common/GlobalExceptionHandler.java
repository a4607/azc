package com.example.springboot.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException e) {
    Map<String, Object> response = new HashMap<>();
    response.put("code", 500);
    response.put("message", e.getMessage());
    return ResponseEntity.status(500).body(response);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleException(Exception e) {
    Map<String, Object> response = new HashMap<>();
    response.put("code", 500);
    response.put("message", "服务器内部错误");
    return ResponseEntity.status(500).body(response);
  }
}