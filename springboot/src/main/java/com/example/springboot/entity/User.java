package com.example.springboot.entity;

import lombok.Data;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class User {
  private Integer id;
  private String username;
  private String password;
  private String email;
  private String avatar;
  private String signature;
  private String gender;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private java.sql.Date birthday;
  private byte[] avatarBlob;
  private String avatarType;
  private Date createTime;
  private Date updateTime;
}