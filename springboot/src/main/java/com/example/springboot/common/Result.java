package com.example.springboot.common;

import java.util.HashMap;
import java.util.Map;

public class Result extends HashMap<String, Object> {
  private static final long serialVersionUID = 1L;

  public Result() {
    put("code", 200);
    put("message", "success");
  }

  public static Result success(Object data, int total) {
    Result result = new Result();
    result.put("data", data);
    result.put("total", total);
    return result;
  }

  public static Result success(Object data) {
    Result result = new Result();
    result.put("data", data);
    return result;
  }

  public static Result error(String message) {
    Result result = new Result();
    result.put("code", 500);
    result.put("message", message);
    return result;
  }

  @Override
  public String toString() {
    return "Result{" +
        "code=" + get("code") +
        ", message='" + get("message") + '\'' +
        ", data=" + get("data") +
        '}';
  }
}