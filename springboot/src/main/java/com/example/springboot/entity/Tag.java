package com.example.springboot.entity;

import java.util.Date;

public class Tag {
  private Integer id;
  private String name;
  private String username;
  private Date createTime;
  private Date updateTime;
  private Integer noteCount;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  public Date getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(Date updateTime) {
    this.updateTime = updateTime;
  }

  public Integer getNoteCount() {
    return noteCount;
  }

  public void setNoteCount(Integer noteCount) {
    this.noteCount = noteCount;
  }
}