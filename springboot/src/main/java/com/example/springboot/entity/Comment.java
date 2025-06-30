package com.example.springboot.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import com.baomidou.mybatisplus.annotation.TableField;

@Data
public class Comment {
  private Integer id;
  private Integer noteId;
  private Integer userId;
  private String content;
  private Integer parentId;
  private LocalDateTime createTime;

  @TableField(exist = false)
  private String username;

  @TableField(exist = false)
  private String avatar;

  @TableField(exist = false)
  private List<Comment> children;
}