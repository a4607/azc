package com.example.springboot.common;

public class BusinessException extends RuntimeException {
  public BusinessException(String message) {
    super(message);
  }
}